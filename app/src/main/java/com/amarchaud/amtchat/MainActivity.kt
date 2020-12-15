package com.amarchaud.amtchat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.firebase.database.FirebaseDatabase


class MainActivity : AppCompatActivity() {

    lateinit var navController : NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // save everything on local phone
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.my_first_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        NavigationUI.setupActionBarWithNavController(this, navController)
    }


    fun getStackCount(): Int {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.my_first_host_fragment) as NavHostFragment
        return navHostFragment.childFragmentManager.backStackEntryCount
    }

    override fun onSupportNavigateUp(): Boolean {
        navController.navigateUp()
        return super.onSupportNavigateUp()
    }
}