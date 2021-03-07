package com.amarchaud.amtchat.injection

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.amarchaud.amtchat.model.FirebaseUserModel
import com.amarchaud.amtchat.ui.tchat.TchatViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val app: Application) : ViewModelProvider.Factory {

    private lateinit var chatUser: FirebaseUserModel

    constructor(
        app: Application,
        charUser: FirebaseUserModel
    ) : this(app) {
        this.chatUser = charUser
    }

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(TchatViewModel::class.java)) {
            return TchatViewModel(app, chatUser) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}