package com.amarchaud.amtchat.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.amarchaud.amtchat.base.PersonalInformations
import com.amarchaud.amtchat.base.PersonalInformationsListener
import com.amarchaud.amtchat.model.FirebaseChatMessageModel
import com.amarchaud.amtchat.model.FirebaseUserModel
import com.amarchaud.amtchat.network.FirebaseAddr
import com.amarchaud.amtchat.ui.lastmessages.LastMessagesViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MessageService : Service(), PersonalInformationsListener {

    companion object {
        const val TAG = "MessageService"
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
       return null // dont want to have a relation Activity - Service
    }

    override fun onCreate() {
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
                    Log.d(TAG, "want to show pupup with $userTo - $lastMessageModel")
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
}