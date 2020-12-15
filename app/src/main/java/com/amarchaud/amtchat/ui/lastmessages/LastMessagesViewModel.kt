package com.amarchaud.amtchat.ui.lastmessages

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.amarchaud.amtchat.base.BaseViewModel
import com.amarchaud.amtchat.model.FirebaseChatMessageModel
import com.amarchaud.amtchat.model.FirebaseUserModel
import com.amarchaud.amtchat.network.FirebaseAddr
import com.amarchaud.amtchat.viewmodel.ItemLastMessageViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LastMessagesViewModel(app: Application) : BaseViewModel(app) {

    companion object {
        var currentUser: FirebaseUserModel? = null
        const val TAG: String = "LastMessagesViewModel"
    }

    init {
        fetchLastMessages()
    }

    var listOfLastMessagesLiveData: MutableLiveData<List<ItemLastMessageViewModel>> =
        MutableLiveData()

    private fun fetchLastMessages() {
        val myUid: String = FirebaseAuth.getInstance().uid ?: return

        val ref =
            FirebaseDatabase.getInstance().getReference(FirebaseAddr.loadAllMessagesOfUser(myUid))
        ref.addListenerForSingleValueEvent(object : ValueEventListener {// appelé qu'une fois

            override fun onDataChange(nodeConversationOf: DataSnapshot) {

                val listOfLastMessage: MutableList<ItemLastMessageViewModel> = mutableListOf()

                nodeConversationOf.children.forEach { nodeWith: DataSnapshot ->

                    Log.d(TAG, "Load all conversation with : ${nodeWith.key}")

                    // get last message
                    val lastmessageModel =
                        nodeWith.children.last().getValue(FirebaseChatMessageModel::class.java)
                    lastmessageModel?.let { lastMessageModel ->

                        // get photo
                        val idToLoad =
                            if (lastMessageModel.fromId == myUid) lastMessageModel.toId else lastMessageModel.fromId
                        val refUser =
                            FirebaseDatabase.getInstance().getReference("/users/$idToLoad")
                        refUser.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(nodeUser: DataSnapshot) {
                                Log.d(TAG, "Try load photo of : ${nodeUser.key}")

                                val user = nodeUser.getValue(FirebaseUserModel::class.java)
                                user?.let { userTo ->
                                    listOfLastMessage.add(
                                        ItemLastMessageViewModel(
                                            userTo,
                                            lastMessageModel
                                        )
                                    )

                                    listOfLastMessagesLiveData.postValue(listOfLastMessage)
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {

                            }

                        })


                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}