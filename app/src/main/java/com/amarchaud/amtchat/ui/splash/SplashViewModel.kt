package com.amarchaud.amtchat.ui.splash

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavDirections
import com.amarchaud.amtchat.base.BaseViewModel
import com.amarchaud.amtchat.base.PersonalInformations
import com.amarchaud.amtchat.base.PersonalInformationsListener
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class SplashViewModel(app: Application) : BaseViewModel(app), PersonalInformationsListener {

    val actionLiveData: MutableLiveData<NavDirections> = MutableLiveData()

    init {
        PersonalInformations.listener = this
        PersonalInformations.updateMyself()
        //val user = FirebaseAuth.getInstance().currentUser
    }

    override fun onFirebaseInfoUserFinish() {
        Observable.timer(1000, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                actionLiveData.postValue( SplashFragmentDirections.actionSplashFragmentToLastMessagesFragment())
            }
    }

    override fun onFirebaseInfoNoUser() {
        Observable.timer(2000, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                actionLiveData.postValue(SplashFragmentDirections.actionSplashFragmentToCreateAccountFragment())
            }
    }

    override fun onCleared() {
        super.onCleared()
        PersonalInformations.listener = null
    }
}