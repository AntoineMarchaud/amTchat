package com.amarchaud.amtchat.injection

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.amarchaud.amtchat.model.FirebaseUserModel
import com.amarchaud.amtchat.ui.chat.ChatViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val app: Application) : ViewModelProvider.Factory {

    private lateinit var meUser: FirebaseUserModel
    private lateinit var chatUser: FirebaseUserModel

    constructor(
        app: Application,
        meUser: FirebaseUserModel,
        charUser: FirebaseUserModel
    ) : this(app) {
        this.meUser = meUser
        this.chatUser = charUser
    }

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            return ChatViewModel(app, meUser, chatUser) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}