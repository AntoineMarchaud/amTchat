package com.amarchaud.amtchat.ui.splash

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import com.amarchaud.amtchat.base.BaseViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class SplashViewModel(app: Application) : BaseViewModel(app) {

    val actionLiveData: MutableLiveData<NavDirections> = MutableLiveData()

    init {
        lateinit var action: NavDirections;

        val f = FirebaseApp.initializeApp(app)
        if (f != null) {
            val user = FirebaseAuth.getInstance().currentUser

            Observable.timer(2000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (user == null) {
                        action =
                            SplashFragmentDirections.actionSplashFragmentToCreateAccountFragment()
                    } else {
                        action =
                            SplashFragmentDirections.actionSplashFragmentToLastMessagesFragment()
                    }

                    actionLiveData.postValue(action)
                };


        }
    }
}