package com.amarchaud.amtchat.ui.login

import android.app.Application
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavDirections
import com.amarchaud.amtchat.base.BaseViewModel
import com.amarchaud.amtchat.base.PersonalInformations
import com.amarchaud.amtchat.base.PersonalInformationsListener
import com.amarchaud.amtchat.base.SingleLiveEvent
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class LoginViewModel(private val app: Application) : BaseViewModel(app), PersonalInformationsListener {

    companion object {
        const val TAG : String = "Login"
    }

    @Inject
    lateinit var injectedApplication: Application

    @Bindable
    var email: String? = null

    @Bindable
    var password: String? = null

    val actionLiveData: SingleLiveEvent<NavDirections> = SingleLiveEvent()

    fun onLoginClick(v: View) {

        if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
            Toast.makeText(injectedApplication, "Please fill out email/pw.", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email!!, password!!)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener

                Log.d(TAG, "Successfully logged in: ${it.result?.user?.uid}")

                PersonalInformations.addListener(this)
                PersonalInformations.updateMyself()
            }
            .addOnFailureListener {
                Toast.makeText(injectedApplication, "$TAG Failed to log in: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onFirebaseInfoUserFinish() {
        val action: NavDirections = LoginFragmentDirections.actionLoginFragmentToLastMessagesFragment()
        actionLiveData.postValue(action)
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