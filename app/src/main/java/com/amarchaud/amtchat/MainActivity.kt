package com.amarchaud.amtchat

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    lateinit var navController: NavController

    companion object {
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar);

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.my_first_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        NavigationUI.setupActionBarWithNavController(this, navController)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
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
}