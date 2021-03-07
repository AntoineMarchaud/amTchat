package com.amarchaud.amtchat.ui.login

import android.app.Application
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.Bindable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavDirections
import com.amarchaud.amtchat.base.PersonalInformations
import com.amarchaud.amtchat.base.PersonalInformationsListener
import com.amarchaud.amtchat.base.SingleLiveEvent
import com.amarchaud.amtchat.injection.component.DaggerViewModelInjectorComponent
import com.amarchaud.amtchat.injection.component.ViewModelInjectorComponent
import com.amarchaud.amtchat.injection.module.AppModule
import com.amarchaud.amtchat.injection.module.ContentResolverModule
import com.amarchaud.amtchat.injection.module.DaoModule
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class LoginViewModel(private val app: Application) : AndroidViewModel(app),
    PersonalInformationsListener {

    companion object {
        const val TAG: String = "Login"
    }

    @Inject
    lateinit var injectedApplication: Application

    // two way bindings
    val email = MutableLiveData<String?>()
    val password = MutableLiveData<String?>()

    private val _actionLiveData = SingleLiveEvent<NavDirections>()
    val actionLiveData: LiveData<NavDirections>
        get() = _actionLiveData


    private val component: ViewModelInjectorComponent = DaggerViewModelInjectorComponent
        .builder()
        .appModule(AppModule(app))
        .contentResolverModule(ContentResolverModule(app))
        .daoModule(DaoModule(app))
        .build()


    init {
        component.inject(this)
    }

    fun onLoginClick(v: View) {

        if (email.value.isNullOrEmpty() || password.value.isNullOrEmpty()) {
            Toast.makeText(injectedApplication, "Please fill out email/pw.", Toast.LENGTH_SHORT)
                .show()
            return
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email.value!!, password.value!!)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener

                Log.d(TAG, "Successfully logged in: ${it.result?.user?.uid}")

                PersonalInformations.addListener(this)
                PersonalInformations.updateMyself()
            }
            .addOnFailureListener {
                Toast.makeText(
                    injectedApplication,
                    "$TAG Failed to log in: ${it.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    override fun onFirebaseInfoUserFinish() {
        val action: NavDirections =
            LoginFragmentDirections.actionLoginFragmentToLastMessagesFragment()
        _actionLiveData.postValue(action)
    }

    override fun onFirebaseInfoNoUser() {
        Toast.makeText(
            app,
            "Error login, please retry",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onCleared() {
        super.onCleared()
        PersonalInformations.removeListener(this)
    }
}