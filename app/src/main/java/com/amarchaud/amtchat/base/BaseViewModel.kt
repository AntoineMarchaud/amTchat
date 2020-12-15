package com.amarchaud.amtchat.base

import android.app.Application
import android.content.Context
import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.amarchaud.amtchat.injection.component.DaggerViewModelInjectorComponent
import com.amarchaud.amtchat.injection.component.ViewModelInjectorComponent
import com.amarchaud.amtchat.injection.module.AppModule
import com.amarchaud.amtchat.injection.module.ContentResolverModule
import com.amarchaud.amtchat.injection.module.DaoModule
import com.amarchaud.amtchat.ui.createaccount.CreateAccountViewModel
import com.amarchaud.amtchat.ui.login.LoginViewModel
import com.amarchaud.amtchat.ui.splash.SplashViewModel

abstract class BaseViewModel(app : Application) : AndroidViewModel(app), Observable {

    // *****************************************
    //        Manage NotifyPropertyChanged
    // *****************************************

    private val callbacks: PropertyChangeRegistry by lazy { PropertyChangeRegistry() }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
        callbacks.add(callback)
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
        callbacks.remove(callback)
    }

    /**
     * Notifies listeners that all properties of this instance have changed.
     */
    @Suppress("unused")
    fun notifyChange() {
        callbacks.notifyCallbacks(this, 0, null)
    }

    /**
     * Notifies listeners that a specific property has changed. The getter for the property
     * that changes should be marked with [Bindable] to generate a field in
     * `BR` to be used as `fieldId`.
     *
     * @param fieldId The generated BR id for the Bindable field.
     */
    fun notifyPropertyChanged(fieldId: Int) {
        callbacks.notifyCallbacks(this, fieldId, null)
    }


    // *****************************************
    //        Manage Dagger injection
    // *****************************************

    // call Dagger2
    // convention is Dagger[Name of @Component]
    // DaggerViewModelInjector is generated at compile
    private val component: ViewModelInjectorComponent = DaggerViewModelInjectorComponent
        .builder()
        .appModule(AppModule(app))
        .contentResolverModule(ContentResolverModule(app))
        .daoModule(DaoModule(app))
        .build()

    init {
        requestInjection()
    }

    /**
     * Injects the required dependencies
     */
    private fun requestInjection() {
        when (this) {
            is CreateAccountViewModel -> {
                component.inject(this)
            }
            is LoginViewModel -> {
                component.inject(this)
            }
        }
    }

}