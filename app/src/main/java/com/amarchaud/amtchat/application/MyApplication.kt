package com.amarchaud.amtchat.application

import android.app.Application
import android.util.Log
import androidx.work.Configuration
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.amarchaud.amtchat.service.MessageWorker
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import java.util.concurrent.TimeUnit


class MyApplication : Application(), Configuration.Provider {

    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setMinimumLoggingLevel(Log.VERBOSE)
            .build()
    }
}