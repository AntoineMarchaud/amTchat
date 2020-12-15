package com.amarchaud.amtchat.injection.component

import android.content.ContentResolver
import androidx.lifecycle.ViewModel
import com.amarchaud.amtchat.injection.module.AppModule
import com.amarchaud.amtchat.injection.module.ContentResolverModule
import com.amarchaud.amtchat.injection.module.DaoModule
import com.amarchaud.amtchat.ui.createaccount.CreateAccountViewModel
import com.amarchaud.amtchat.ui.login.LoginViewModel
import com.amarchaud.amtchat.ui.splash.SplashViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [(AppModule::class), (ContentResolverModule::class), (DaoModule::class)])
interface ViewModelInjectorComponent {

    // todo insert here the ViewModel you want to inject !
    fun inject(createAccountViewModel: CreateAccountViewModel)
    fun inject(loginViewModel: LoginViewModel)

    @Component.Builder
    interface Builder {
        fun build(): ViewModelInjectorComponent
        fun appModule(appModule: AppModule): Builder
        fun contentResolverModule(contentResolverModule: ContentResolverModule): Builder
        fun daoModule(daoModule: DaoModule) : Builder
    }
}