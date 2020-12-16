package com.amarchaud.amtchat.service

import android.R.id
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.app.job.JobScheduler
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.core.app.JobIntentService
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavDeepLinkBuilder
import com.amarchaud.amtchat.R
import com.amarchaud.amtchat.base.PersonalInformations
import com.amarchaud.amtchat.base.PersonalInformationsListener
import com.amarchaud.amtchat.model.FirebaseChatMessageModel
import com.amarchaud.amtchat.model.FirebaseUserModel
import com.amarchaud.amtchat.network.FirebaseAddr
import com.amarchaud.amtchat.ui.lastmessages.LastMessagesViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.BaseTarget
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class MessageService : Service(), PersonalInformationsListener {

    companion object {
        const val TAG = "MessageService"
        const val CHANNEL_ID = "myChannelId"
    }


    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null // dont want to have a relation Activity - Service
    }

    override fun onCreate() {
        createNotificationChannel()
        PersonalInformations.addListener(this)
    }

    override fun onDestroy() {
        PersonalInformations.removeListener(this)
    }



    private fun proceedLastMessage(lastMessageModel: FirebaseChatMessageModel) {
        val myUid: String = FirebaseAuth.getInstance().uid ?: return

        // get photo
        val idToLoad =
            if (lastMessageModel.fromId == myUid) lastMessageModel.toId else lastMessageModel.fromId
        val refUser =
            FirebaseDatabase.getInstance().getReference("/users/$idToLoad")
        refUser.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(nodeUser: DataSnapshot) {

                val user = nodeUser.getValue(FirebaseUserModel::class.java)
                user?.let { userTo: FirebaseUserModel ->
                    // show a Pop-up to user !

                    val b = Bundle()
                    b.putParcelable("ChatUser", userTo)
                    val pendingIntent = NavDeepLinkBuilder(this@MessageService)
                        .setGraph(R.navigation.nav_graph)
                        .setDestination(R.id.chatFragment)
                        .setArguments(b)
                        .createPendingIntent()



                    Glide.with(this@MessageService)
                        .asBitmap()
                        .load(Uri.parse(userTo.profileImageUrl))
                        .into(object : CustomTarget<Bitmap>() {

                            override fun onResourceReady(
                                resource: Bitmap,
                                transition: Transition<in Bitmap>?
                            ) {
                                val builder = NotificationCompat.Builder(this@MessageService, CHANNEL_ID)
                                    .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                                    .setLargeIcon(resource)
                                    .setContentTitle(userTo.username)
                                    .setContentText(lastMessageModel.text)
                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                    .setContentIntent(pendingIntent)
                                    .setAutoCancel(true)

                                with(NotificationManagerCompat.from(this@MessageService)) {
                                    // notificationId is a unique int for each notification that you must define
                                    notify(0, builder.build())
                                }
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {

                            }

                        })


                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun listenForMessages() {
        val myUid = FirebaseAuth.getInstance().uid ?: return

        val ref = FirebaseDatabase.getInstance().getReference(
            FirebaseAddr.loadAllMessagesOfUser(myUid)
        )
        ref.addChildEventListener(object :
            ChildEventListener {


            override fun onChildAdded(
                nodeWith: DataSnapshot,
                previousChildName: String?
            ) {
                Log.d(LastMessagesViewModel.TAG, "onChildAdded : ${nodeWith.key}")
                val lastMessageModel =
                    nodeWith.children.last().getValue(FirebaseChatMessageModel::class.java)
                Log.d(LastMessagesViewModel.TAG, lastMessageModel.toString())

                lastMessageModel?.let {
                    proceedLastMessage(it)
                }
            }

            override fun onChildChanged(
                nodeWith: DataSnapshot,
                previousChildName: String?
            ) {
                Log.d(LastMessagesViewModel.TAG, "onChildChanged : ${nodeWith.key}")
                val lastMessageModel =
                    nodeWith.children.last().getValue(FirebaseChatMessageModel::class.java)
                Log.d(LastMessagesViewModel.TAG, lastMessageModel.toString())

                lastMessageModel?.let {
                    proceedLastMessage(it)
                }
            }

            override fun onChildRemoved(nodeConversationOf: DataSnapshot) {
                Log.d(TAG, "Received removed message of : ${nodeConversationOf.key}")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

    override fun onFirebaseInfoUserFinish() {
        listenForMessages()
    }

    override fun onFirebaseInfoNoUser() {

    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}