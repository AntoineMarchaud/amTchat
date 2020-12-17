package com.amarchaud.amtchat

import android.app.Application
import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import com.amarchaud.amtchat.base.BaseViewModel
import com.amarchaud.amtchat.service.MessageService

class MainViewModel(private val app: Application) : BaseViewModel(app) {


    init {
        println("")
    }

}