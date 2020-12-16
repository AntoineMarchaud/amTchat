package com.amarchaud.amtchat.base

import com.amarchaud.amtchat.model.FirebaseUserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

interface PersonalInformationsListener {
    fun onFirebaseInfoUserFinish()
    fun onFirebaseInfoNoUser()
}

object PersonalInformations {

    private var listener: MutableList<PersonalInformationsListener> = mutableListOf()
    fun addListener(oneListener: PersonalInformationsListener?) {
        oneListener?.let {
            listener.add(it)
        }
    }

    fun removeListener(oneListener: PersonalInformationsListener?) {
        oneListener?.let {
            listener.remove(it)
        }
    }


    var MySelf: FirebaseUserModel? = null
        private set

    fun updateMyself() {
        val myUid: String? = FirebaseAuth.getInstance().uid
        if (myUid == null) {
            listener.forEach {
                it.onFirebaseInfoNoUser()
            }
            return
        }

        val ref =
            FirebaseDatabase.getInstance().getReference("/users/$myUid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                MySelf = snapshot.getValue(FirebaseUserModel::class.java)
                listener.forEach {
                    it.onFirebaseInfoUserFinish()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

}