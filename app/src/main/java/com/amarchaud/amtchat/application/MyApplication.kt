package com.amarchaud.amtchat.application

import android.app.Application
import android.content.Intent
import com.amarchaud.amtchat.service.MessageService
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }
}