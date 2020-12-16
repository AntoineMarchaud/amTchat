package com.amarchaud.amtchat

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.firebase.database.FirebaseDatabase


class MainActivity : AppCompatActivity() {

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

        /*
        // Return the FragmentManager for interacting with fragments associated with this fragment's activity.
        val fragmentsActive = navHostFragment.parentFragmentManager.fragments
        fragmentsActive.forEach {
            Log.d(TAG, "Current parent Name : ${it.javaClass.canonicalName}")
        }*/

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
}