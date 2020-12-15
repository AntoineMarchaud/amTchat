package com.amarchaud.amtchat.ui.chooseuser

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.amarchaud.amtchat.base.BaseViewModel
import com.amarchaud.amtchat.model.FirebaseUserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChooseUserViewModel(app: Application) : BaseViewModel(app) {

    companion object {
        const val TAG: String = "ChooseUserViewModel"
    }

    init {
        fetchUsers()
    }

    var listOfUsersLiveData: MutableLiveData<List<FirebaseUserModel>> = MutableLiveData()
    var MyselfLiveData: MutableLiveData<FirebaseUserModel> = MutableLiveData()

    private fun fetchUsers() {
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener { // appelé qu'une fois

            override fun onDataChange(p0: DataSnapshot) {
                val l: MutableList<FirebaseUserModel> = mutableListOf()

                p0.children.forEach { dataSnapShot: DataSnapshot ->
                    Log.d(TAG, "users : $dataSnapShot")
                    val user = dataSnapShot.getValue(FirebaseUserModel::class.java)
                    user?.let {
                        if (it.uid != FirebaseAuth.getInstance().uid) {// do not add myself
                            l.add(it)
                        } else {
                            MyselfLiveData.postValue(it)
                        }
                    }
                    listOfUsersLiveData.postValue(l)
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.d(TAG, "onCancelled + $p0")
            }
        })
    }
}