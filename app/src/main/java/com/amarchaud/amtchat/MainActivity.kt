package com.amarchaud.amtchat

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.amarchaud.amtchat.base.PersonalInformations
import com.amarchaud.amtchat.base.PersonalInformationsListener
import com.amarchaud.amtchat.service.MessageService
import com.google.firebase.database.FirebaseDatabase


class MainActivity : AppCompatActivity(), PersonalInformationsListener {

    lateinit var navController: NavController

    companion object {
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.my_first_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        NavigationUI.setupActionBarWithNavController(this, navController)

        PersonalInformations.addListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()

        PersonalInformations.removeListener(this)
    }


    fun getStackCount(): Int {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.my_first_host_fragment) as NavHostFragment
        return navHostFragment.childFragmentManager.backStackEntryCount
    }

    fun getStackInfos() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.my_first_host_fragment) as NavHostFragment

        val totalFragmentInStack = navHostFragment.childFragmentManager.backStackEntryCount
        Log.d(TAG, "Nb Fragments in stack : $totalFragmentInStack")

        for (index in 0 until totalFragmentInStack) {
            with(navHostFragment.childFragmentManager.getBackStackEntryAt(index)) {
                Log.d(TAG, "Stack Name : $name")
                Log.d(TAG, "Stack Id : $id")
            }
        }

        // Return a private FragmentManager for placing and managing Fragments inside of this Fragment.
        val fragments = navHostFragment.childFragmentManager.fragments
        fragments.forEach {
            Log.d(TAG, "Current Fragment Name : ${it.javaClass.canonicalName}")
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        navController.navigateUp()
        return super.onSupportNavigateUp()
    }

    // **************************************************************************
    // to move in ViewModel ?
    private var mService: MessageService? = null
    private var mBound: Boolean = false


    /** Defines callbacks for service binding, passed to bindService()  */
    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as MessageService.LocalBinder
            mService = binder.service
            mBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mService = null
            mBound = false
        }
    }

    override fun onFirebaseInfoUserFinish() {
        // Bind to LocalService
        /*
        Intent(this, MessageService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }*/
    }

    override fun onStop() {
        super.onStop()
        unbindService(connection)
        mBound = false
    }

    override fun onFirebaseInfoNoUser() {

    }
}