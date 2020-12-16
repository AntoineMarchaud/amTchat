package com.amarchaud.amtchat.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavDeepLinkBuilder
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.amarchaud.amtchat.R
import com.amarchaud.amtchat.model.FirebaseChatMessageModel
import com.amarchaud.amtchat.model.FirebaseUserModel
import com.amarchaud.amtchat.network.FirebaseAddr
import com.amarchaud.amtchat.ui.lastmessages.LastMessagesViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MessageWorker(private val appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    companion object {
        const val TAG = "MessageWorker"
        const val CHANNEL_ID = "myChannelId"
        const val SLEEP_DURATION_MIN = 15L
    }

    override fun doWork(): Result {
        createNotificationChannel()
        listenForMessages()
        Thread.sleep(SLEEP_DURATION_MIN)
        return Result.success()
    }

    private fun updateReceiveStatus(message: FirebaseChatMessageModel) {

        val myUid: String = FirebaseAuth.getInstance().uid ?: return

        val fromRef = FirebaseDatabase.getInstance().getReference(
            FirebaseAddr.loadUserMessageForOnePerso(message.fromId, message.toId) + "/" + message.id
        )

        val toRef = FirebaseDatabase.getInstance().getReference(
            FirebaseAddr.loadUserMessageForOnePerso(message.toId, message.fromId) + "/" + message.id
        )

        message.isReceived = true

        fromRef.setValue(message)
        toRef.setValue(message)
    }

    private fun proceedLastMessageReceived(lastMessageModel: FirebaseChatMessageModel) {
        val myUid: String = FirebaseAuth.getInstance().uid ?: return

        if (lastMessageModel.fromId == myUid)
            return

        if (lastMessageModel.fromId != myUid && lastMessageModel.isReceived)
            return

        // get photo
        val idToLoad =
            if (lastMessageModel.fromId == myUid) lastMessageModel.toId else lastMessageModel.fromId
        val refUser =
            FirebaseDatabase.getInstance().getReference("/users/$idToLoad")
        refUser.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(nodeUser: DataSnapshot) {
                val user = nodeUser.getValue(FirebaseUserModel::class.java)
                user?.let { userTo: FirebaseUserModel ->

                    // can go directly to chat by pressing the notif
                    val b = Bundle()
                    b.putParcelable("ChatUser", userTo)
                    val pendingIntent = NavDeepLinkBuilder(appContext)
                        .setGraph(R.navigation.nav_graph)
                        .setDestination(R.id.chatFragment)
                        .setArguments(b)
                        .createPendingIntent()


                    val builder = NotificationCompat.Builder(
                        appContext,
                        CHANNEL_ID
                    )
                        .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                        .setContentTitle(userTo.username)
                        .setContentText(lastMessageModel.text)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)

                    // show a Pop-up to user !
                    with(NotificationManagerCompat.from(appContext)) {
                        // notificationId is a unique int for each notification that you must define
                        notify(0, builder.build())

                        updateReceiveStatus(lastMessageModel)
                    }


                    // do not know why : crash if i use glide
/*
                    Glide.with(appContext)
                        .asBitmap()
                        .load(Uri.parse(userTo.profileImageUrl))
                        .into(object : CustomTarget<Bitmap>() {

                            override fun onResourceReady(
                                resource: Bitmap,
                                transition: Transition<in Bitmap>?
                            ) {
                                val builder = NotificationCompat.Builder(
                                    appContext,
                                    CHANNEL_ID
                                )
                                    .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                                    .setLargeIcon(resource)
                                    .setContentTitle(userTo.username)
                                    .setContentText(lastMessageModel.text)
                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                    .setContentIntent(pendingIntent)
                                    .setAutoCancel(true)

                                with(NotificationManagerCompat.from(appContext)) {
                                    // notificationId is a unique int for each notification that you must define
                                    notify(0, builder.build())
                                    latch.countDown()
                                }
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {
                                latch.countDown()
                            }

                        })

*/
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
        ref.addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(
                nodeWith: DataSnapshot,
                previousChildName: String?
            ) {
                Log.d(LastMessagesViewModel.TAG, "onChildAdded : ${nodeWith.key}")
                val lastMessageModel =
                    nodeWith.children.last().getValue(FirebaseChatMessageModel::class.java)
                Log.d(LastMessagesViewModel.TAG, lastMessageModel.toString())

                lastMessageModel?.let {
                    proceedLastMessageReceived(it)
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
                    proceedLastMessageReceived(it)
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

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = appContext.getString(R.string.channel_name)
            val descriptionText = appContext.getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}