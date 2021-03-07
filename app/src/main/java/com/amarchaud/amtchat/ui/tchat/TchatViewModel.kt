package com.amarchaud.amtchat.ui.tchat

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.amarchaud.amtchat.base.PersonalInformations
import com.amarchaud.amtchat.model.FirebaseChatMessageModel
import com.amarchaud.amtchat.model.FirebaseUserModel
import com.amarchaud.amtchat.network.FirebaseAddr
import com.amarchaud.amtchat.service.MessageService
import com.amarchaud.amtchat.model.app.ItemChatViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class TchatViewModel(
    private val app: Application,
    private val ChatUser: FirebaseUserModel
) : AndroidViewModel(app) {

    companion object {
        const val TAG = "ChatViewModel"

        enum class typeItem {
            ITEM_INSERTED,
            ITEM_MODIFIED,
            ITEM_DELETED
        }
    }

    // two way binding
    val theMessage = MutableLiveData<String?>()

    init {
        listenForMessages()
    }


    private val _listOfMessagesLiveData = MutableLiveData<Triple<List<ItemChatViewModel>, typeItem, Int>>()
    val listOfMessagesLiveData: LiveData<Triple<List<ItemChatViewModel>, typeItem, Int>>
        get() = _listOfMessagesLiveData

    private var listOfMessages: MutableList<ItemChatViewModel> = mutableListOf()

    private fun listenForMessages() {

        val myUid = FirebaseAuth.getInstance().uid ?: return
        val userUid = ChatUser.uid

        val ref = FirebaseDatabase.getInstance().getReference(
            FirebaseAddr.loadUserMessageForOnePerso(myUid, userUid)
        )
        ref.addChildEventListener(object :
            ChildEventListener { // appelé chaque fois qu'un truc apparait coté base

            // called each time new messages are coming
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {

                val chatMessage = p0.getValue(FirebaseChatMessageModel::class.java)

                if (chatMessage != null) {
                    Log.d(TAG, "onChildAdded ${chatMessage.text}")

                    if (chatMessage.fromId == myUid)
                        listOfMessages.add(
                            ItemChatViewModel(
                                chatMessage,
                                PersonalInformations.MySelf?.profileImageUrl
                            )
                        )
                    else {
                        chatMessage.isReceived = true
                        listOfMessages.add(ItemChatViewModel(chatMessage, ChatUser.profileImageUrl))
                    }

                    _listOfMessagesLiveData.postValue(
                        Triple(
                            listOfMessages,
                            typeItem.ITEM_INSERTED,
                            listOfMessages.size - 1
                        )
                    ) // un evement a chaque message, la recycler view se débrouille
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

                Log.d(TAG, "onChildChanged $p0 - $p1")
                val chatMessageModified = p0.getValue(FirebaseChatMessageModel::class.java)
                val pos = listOfMessages.indexOfFirst {
                    it.firebaseChatMessageModel?.id == chatMessageModified?.id
                }
                if (pos >= 0) {
                    with(listOfMessages[pos]) {
                        firebaseChatMessageModel?.text = chatMessageModified?.text
                        firebaseChatMessageModel?.isDeleted = chatMessageModified?.isDeleted == true
                        firebaseChatMessageModel?.isSent = chatMessageModified?.isSent == true
                        firebaseChatMessageModel?.isReceived =
                            chatMessageModified?.isReceived == true
                        firebaseChatMessageModel?.isRead = chatMessageModified?.isRead == true
                    }

                    _listOfMessagesLiveData.postValue(
                        Triple(
                            listOfMessages,
                            typeItem.ITEM_MODIFIED,
                            pos
                        )
                    )
                }
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {
                Log.d(TAG, "onChildRemoved $p0")

                val chatMessageDeleted = p0.getValue(FirebaseChatMessageModel::class.java)
                val pos = listOfMessages.indexOfFirst {
                    it.firebaseChatMessageModel?.id == chatMessageDeleted?.id
                }
                if (pos >= 0) {
                    listOfMessages.removeAt(pos)

                    _listOfMessagesLiveData.postValue(
                        Triple(
                            listOfMessages,
                            typeItem.ITEM_DELETED,
                            pos
                        )
                    )
                }
            }

        })
    }

    fun onSendMessage(v: View) {

        val myUid = FirebaseAuth.getInstance().uid ?: return
        val userUid = ChatUser.uid

        /**
         * Create duplicate
         */
        val fromRef = FirebaseDatabase.getInstance().getReference(
            FirebaseAddr.loadUserMessageForOnePerso(myUid, userUid)
        ).push() // push mean, create a new uid

        val toRef = FirebaseDatabase.getInstance().getReference(
            FirebaseAddr.loadUserMessageForOnePerso(userUid, myUid) + "/" + fromRef.key
        )

        theMessage.value?.let { theMessageNotNull ->

            val f = FirebaseChatMessageModel(
                fromRef.key!!,
                theMessageNotNull,
                myUid,
                userUid,
                System.currentTimeMillis() / 1000,
                false,
                isSent = true
            )

            // envoie du sens moi vers l'autre
            fromRef.setValue(f)
                .addOnSuccessListener {
                    // mis a jour du flag isSent
                    theMessage.value = null
                }

            // envoie du reverse (pour que lui retrouve la conversation)
            toRef.setValue(f)
        }
    }

    private var mService: MessageService? = null
    private var bound: Boolean = false

    val mConnection = object : ServiceConnection {
        // Called when the connection with the service is established
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as MessageService.LocalBinder
            mService = binder.getService()
            bound = true

            // informe service
            mService?.currentUidConversationTo = ChatUser.uid
        }

        // Called when the connection with the service disconnects unexpectedly
        override fun onServiceDisconnected(className: ComponentName) {
            Log.e(TAG, "onServiceDisconnected")
            bound = false

            mService?.currentUidConversationTo = null
        }
    }

    fun onStart() {
        // Bind to the service
        Intent(app, MessageService::class.java).also { intent ->
            app.bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
        }
    }


    fun onStop() {
        if (bound) {
            app.unbindService(mConnection)
            mService?.currentUidConversationTo = null
            bound = false
        }
    }

}