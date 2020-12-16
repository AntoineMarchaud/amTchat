package com.amarchaud.amtchat.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.amarchaud.amtchat.model.FirebaseChatMessageModel
import com.amarchaud.amtchat.network.FirebaseAddr
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase

class MessageService : Service() {

    companion object {
        const val TAG = "MessageService"
    }

    // Binder given to clients
    private val binder = LocalBinder()

    // put here what Fragment can take !

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    inner class LocalBinder : Binder() {
        // Return this instance of ListenMessageService so clients can call public methods
        val service: MessageService
            get() = this@MessageService
    }

    override fun onBind(intent: Intent): IBinder {
        listenForMessages()
        return binder
    }

    private fun listenForMessages() {
        val myUid = FirebaseAuth.getInstance().uid ?: return

        val ref = FirebaseDatabase.getInstance().getReference(
            FirebaseAddr.loadAllMessagesOfUser(myUid)
        )
        ref.addChildEventListener(object :
            ChildEventListener {


            override fun onChildAdded(
                nodeConversationOf: DataSnapshot,
                previousChildName: String?
            ) {

                Log.d(TAG, "Received new message of : ${nodeConversationOf.key}")

                nodeConversationOf.children.forEach {
                    val message  = it.getValue(FirebaseChatMessageModel::class.java)
                    Log.d(TAG, message.toString())
                }


            }

            override fun onChildChanged(
                nodeConversationOf: DataSnapshot,
                previousChildName: String?
            ) {
                Log.d(TAG, "Received modified message of : ${nodeConversationOf.key}")

                nodeConversationOf.children.forEach {
                    val message  = it.getValue(FirebaseChatMessageModel::class.java)
                    Log.d(TAG, message.toString())
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
}