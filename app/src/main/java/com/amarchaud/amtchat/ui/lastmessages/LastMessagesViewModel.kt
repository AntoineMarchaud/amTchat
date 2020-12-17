package com.amarchaud.amtchat.ui.lastmessages

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.MutableLiveData
import com.amarchaud.amtchat.base.BaseViewModel
import com.amarchaud.amtchat.model.FirebaseChatMessageModel
import com.amarchaud.amtchat.model.FirebaseUserModel
import com.amarchaud.amtchat.network.FirebaseAddr
import com.amarchaud.amtchat.service.MessageService
import com.amarchaud.amtchat.viewmodel.ItemLastMessageViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class LastMessagesViewModel(private val app: Application) : BaseViewModel(app) {

    companion object {
        var currentUser: FirebaseUserModel? = null
        const val TAG: String = "LastMessagesViewModel"
    }

    init {
        listenNewMessage()

        // launch service
        Intent(app, MessageService::class.java).also { intent ->
            app.startService(intent)
        }

        /*
        // Launch worker
        val request = PeriodicWorkRequest.Builder(MessageWorker::class.java, MessageWorker.SLEEP_DURATION_MIN, TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(app).enqueue(request);*/
    }

    var listOfLastMessagesLiveData: MutableLiveData<List<ItemLastMessageViewModel>> =
        MutableLiveData()

    private fun listenNewMessage() {
        val myUid: String = FirebaseAuth.getInstance().uid ?: return

        val listOfLastMessage: MutableList<ItemLastMessageViewModel> = mutableListOf()

        fun proceedLastMessage(lastMessageModel: FirebaseChatMessageModel) {

            // get photo
            val idToLoad =
                if (lastMessageModel.fromId == myUid) lastMessageModel.toId else lastMessageModel.fromId
            val refUser =
                FirebaseDatabase.getInstance().getReference("/users/$idToLoad")
            refUser.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(nodeUser: DataSnapshot) {

                    val user = nodeUser.getValue(FirebaseUserModel::class.java)
                    user?.let { userTo: FirebaseUserModel ->

                        val alreadyLastMessageFromThisUser = listOfLastMessage.find {
                            it.lastConvUser == userTo
                        }
                        if (alreadyLastMessageFromThisUser == null) {
                            listOfLastMessage.add(
                                ItemLastMessageViewModel(
                                    userTo,
                                    lastMessageModel
                                )
                            )
                        } else {
                            alreadyLastMessageFromThisUser.lastConvChat = lastMessageModel
                        }

                        listOfLastMessagesLiveData.postValue(listOfLastMessage)
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }

        val ref =
            FirebaseDatabase.getInstance().getReference(FirebaseAddr.loadAllMessagesOfUser(myUid))
        ref.addChildEventListener(object :
            ChildEventListener {
            override fun onChildAdded(nodeWith: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildAdded : ${nodeWith.key}")
                val lastMessageModel =
                    nodeWith.children.last().getValue(FirebaseChatMessageModel::class.java)
                Log.d(TAG, lastMessageModel.toString())

                lastMessageModel?.let {
                    proceedLastMessage(it)
                }
            }

            // onChildChanged will be call each time, because when userB send a message, Firebase considers the entire "nodeWith" node is changing
            override fun onChildChanged(nodeWith: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildChanged : ${nodeWith.key}")
                val lastMessageModel =
                    nodeWith.children.last().getValue(FirebaseChatMessageModel::class.java)
                Log.d(TAG, lastMessageModel.toString())

                lastMessageModel?.let {
                    proceedLastMessage(it)
                }
            }

            override fun onChildRemoved(nodeWith: DataSnapshot) {
                Log.d(TAG, "onChildRemoved : ${nodeWith.key}")
                val lastMessageModel =
                    nodeWith.children.last().getValue(FirebaseChatMessageModel::class.java)
                Log.d(TAG, lastMessageModel.toString())

                lastMessageModel?.let {
                    proceedLastMessage(it)
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}