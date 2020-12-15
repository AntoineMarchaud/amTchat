package com.amarchaud.amtchat.ui.lastmessages

import android.app.Application
import com.amarchaud.amtchat.base.BaseViewModel
import com.amarchaud.amtchat.model.FirebaseChatMessageModel
import com.amarchaud.amtchat.model.FirebaseUserModel
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
        fetchCurrentUser()
        fetchLastMessages()
    }

    // todo
    private fun fetchCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                val chatMessage = p0.getValue(FirebaseChatMessageModel::class.java)

            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun fetchLastMessages() {
        val fromId = FirebaseAuth.getInstance().uid


    }
}