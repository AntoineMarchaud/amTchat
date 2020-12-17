package com.amarchaud.amtchat.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavDeepLinkBuilder
import com.amarchaud.amtchat.R
import com.amarchaud.amtchat.model.FirebaseChatMessageModel
import com.amarchaud.amtchat.model.FirebaseUserModel
import com.amarchaud.amtchat.network.FirebaseAddr
import com.amarchaud.amtchat.ui.lastmessages.LastMessagesViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MessageService : Service() {

    companion object {
        const val TAG = "MessageService"
        const val CHANNEL_ID = "channelIdService"
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
        createNotificationChannel()
        listenForMessages()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")
        // If we get killed, after returning from here, restart
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        // We don't provide binding, so return null
        return null
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

                    Log.d(TAG, "onDestroy")

                    // can go directly to chat by pressing the notif
                    val b = Bundle()
                    b.putParcelable("ChatUser", userTo)
                    val pendingIntent = NavDeepLinkBuilder(this@MessageService)
                        .setGraph(R.navigation.nav_graph)
                        .setDestination(R.id.chatFragment)
                        .setArguments(b)
                        .createPendingIntent()



                    val builder = NotificationCompat.Builder(
                        this@MessageService,
                        CHANNEL_ID
                    )
                        .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                        .setContentTitle(userTo.username)
                        .setContentText(lastMessageModel.text)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)

                    // show a Pop-up to user !
                    with(NotificationManagerCompat.from(this@MessageService)) {
                        // notificationId is a unique int for each notification that you must define
                        notify(0, builder.build())
                        updateReceiveStatus(lastMessageModel)
                    }
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
                Log.d(MessageWorker.TAG, "Received removed message of : ${nodeConversationOf.key}")
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
            val name = getString(R.string.channel_name_service)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = getString(R.string.channel_description_service)
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}