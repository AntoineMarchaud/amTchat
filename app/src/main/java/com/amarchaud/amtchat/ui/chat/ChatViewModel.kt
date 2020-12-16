package com.amarchaud.amtchat.ui.chat

import android.app.Application
import android.util.Log
import android.view.View
import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import com.amarchaud.amtchat.BR
import com.amarchaud.amtchat.base.BaseViewModel
import com.amarchaud.amtchat.base.PersonalInformations
import com.amarchaud.amtchat.model.FirebaseChatMessageModel
import com.amarchaud.amtchat.model.FirebaseUserModel
import com.amarchaud.amtchat.network.FirebaseAddr
import com.amarchaud.amtchat.ui.lastmessages.LastMessagesViewModel
import com.amarchaud.amtchat.viewmodel.ItemChatViewModel
import com.google.firebase.database.*

class ChatViewModel(
    private val app: Application,
    private val ChatUser: FirebaseUserModel
) :
    BaseViewModel(app) {

    companion object {
        const val TAG = "ChatViewModel"

        enum class typeItem {
            ITEM_INSERTED,
            ITEM_MODIFIED,
            ITEM_DELETED
        }
    }

    @Bindable
    var theMessage: String? = null


    init {
        listenForMessages()
    }

    private var listOfMessages: MutableList<ItemChatViewModel> = mutableListOf()
    var listOfMessagesLiveData: MutableLiveData<Triple<List<ItemChatViewModel>, typeItem, Int>> =
        MutableLiveData()


    private fun listenForMessages() {

        val myUid = PersonalInformations.MySelf?.uid ?: return
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
                    else
                        listOfMessages.add(ItemChatViewModel(chatMessage, ChatUser.profileImageUrl))

                    listOfMessagesLiveData.postValue(
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
                    }

                    listOfMessagesLiveData.postValue(
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

                    listOfMessagesLiveData.postValue(
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

        val myUid = PersonalInformations.MySelf?.uid ?: return
        val userUid = ChatUser.uid

        /**
         * Create duplicate
         */

        val fromRef = FirebaseDatabase.getInstance().getReference(
            FirebaseAddr.loadUserMessageForOnePerso(myUid, userUid)
        ).push()

        val toRef = FirebaseDatabase.getInstance().getReference(
            FirebaseAddr.loadUserMessageForOnePerso(userUid, myUid) + "/" + fromRef.key
        )

        theMessage?.let { theMessageNotNull ->

            val f = FirebaseChatMessageModel(
                fromRef.key!!,
                theMessageNotNull,
                myUid,
                userUid,
                System.currentTimeMillis() / 1000,
                false
            )

            // envoie du sens moi vers l'autre
            fromRef.setValue(f)
                .addOnSuccessListener {
                    Log.d(TAG, "Saved our chat message: ${fromRef.key}")

                    theMessage = null
                    notifyPropertyChanged(BR.theMessage)
                }

            // envoie du reverse (pour que lui retrouve la conversation)
            toRef.setValue(f)
        }
    }
}