package com.amarchaud.amtchat.ui.createaccount

import android.app.Application
import android.content.ContentResolver
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.Toast
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
import com.amarchaud.amtchat.model.FirebaseUserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.*
import javax.inject.Inject

class CreateAccountViewModel(private val app: Application) : AndroidViewModel(app), PersonalInformationsListener {

    companion object {
        const val TAG: String = "CreateAccount"
    }

    @Inject
    lateinit var injectedContentResolver: ContentResolver

    // two ways binding
    val username = MutableLiveData<String?>()
    val email = MutableLiveData<String?>()
    val password = MutableLiveData<String?>()
    val passwordBis = MutableLiveData<String?>()


    private val _selectedPhotoUri = SingleLiveEvent<Uri?>()
    val selectedPhotoUri: LiveData<Uri?>
        get() = _selectedPhotoUri

    private val _pickPhotoAction = SingleLiveEvent<Boolean>()
    val pickPhotoAction: LiveData<Boolean>
        get() = _pickPhotoAction


    private val _actionToNextScreen = SingleLiveEvent<NavDirections>()
    val actionToNextScreen: LiveData<NavDirections>
        get() = _actionToNextScreen


    private val component: ViewModelInjectorComponent = DaggerViewModelInjectorComponent
        .builder()
        .appModule(AppModule(app))
        .contentResolverModule(ContentResolverModule(app))
        .daoModule(DaoModule(app))
        .build()
    init {
        component.inject(this)
    }

    /**
     *  CALLBACK FROM VIEW
     */
    fun onRegister(v: View) {

        if (email.value.isNullOrEmpty() || password.value.isNullOrEmpty() || passwordBis.value.isNullOrEmpty()) {
            Toast.makeText(
                app,
                "Please enter text in email/password",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if(password != passwordBis) {
            Toast.makeText(
                app,
                "Please enter the same password",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        Log.d(TAG, "Attempting to create user with email: $email")

        // Firebase Authentication to create a user with email and password
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.value!!, password.value!!)
            .addOnCompleteListener {
                if (!it.isSuccessful)
                    return@addOnCompleteListener

                Log.d(TAG, "Successfully created user with uid: ${it.result?.user?.uid}")

                if (selectedPhotoUri != null)
                    uploadImageToFirebaseStorage()
                else
                    saveUserToFirebaseDatabase("")
            }
            .addOnFailureListener {
                Log.d(TAG, "Failed to create user: ${it.message}")
                Toast.makeText(
                    app,
                    "Failed to create user: ${it.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    fun onAlreadyHaveAccount(v: View) {
        Log.d(TAG, "Try to show l  ogin activity")

        val action: NavDirections =
            CreateAccountFragmentDirections.actionCreateAccountFragmentToLoginFragment()
        _actionToNextScreen.postValue(action)
    }

    fun onSelectPhoto() {
        Log.d(TAG, "Try to show photo selector")
        _pickPhotoAction.postValue(true)
    }

    fun onSelectedPhoto(uri: Uri?) {
        _selectedPhotoUri.value = uri
    }


    /**
     * Other functiopns
     *
     */
    private fun uploadImageToFirebaseStorage() {

        if (selectedPhotoUri.value == null)
            return

        // create a random UID for the image
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(selectedPhotoUri.value!!)
            .addOnSuccessListener { it ->

                Log.d(TAG, "Successfully uploaded image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d(TAG, "File Location: $it")
                    saveUserToFirebaseDatabase(it.toString())
                }
            }
            .addOnFailureListener {
                Log.d(TAG, "Failed to upload image to storage: ${it.message}")
            }
    }

    /**
     * Save a user with uid / email / photo url
     */
    private fun saveUserToFirebaseDatabase(profileImageUrl: String?) {

        val uid = FirebaseAuth.getInstance().uid ?: return

        val user = FirebaseUserModel(uid, username.value!!, profileImageUrl)

        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.setValue(user)
            .addOnSuccessListener {
                Log.d(TAG, "Finally we saved the user to Firebase Database")

                PersonalInformations.addListener(this)
                PersonalInformations.updateMyself()
            }
            .addOnFailureListener {
                Log.d(TAG, "Failed to set value to database: ${it.message}")

                Toast.makeText(
                    app,
                    "Error when creating user",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    override fun onFirebaseInfoUserFinish() {
        val action: NavDirections =
            CreateAccountFragmentDirections.actionCreateAccountFragmentToLastMessagesFragment()
        _actionToNextScreen.postValue(action)
    }

    override fun onFirebaseInfoNoUser() {
        Toast.makeText(
            app,
            "Error register, please retry",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onCleared() {
        super.onCleared()
        PersonalInformations.removeListener(this)
    }
}