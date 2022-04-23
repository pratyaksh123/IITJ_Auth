package com.blockgeeks.iitj_auth.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.Fragment
import com.blockgeeks.iitj_auth.R
import com.blockgeeks.iitj_auth.fragments.AboutFragment
import com.blockgeeks.iitj_auth.fragments.DashboardFragment
import com.blockgeeks.iitj_auth.fragments.SettingsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        val aboutFragment = AboutFragment()
        val dashboardFragment = DashboardFragment()
        val settingsFragment = SettingsFragment()

        val sharedPreferences = applicationContext.getSharedPreferences("initial_setup", Context.MODE_PRIVATE)
        val initialSetupBoolean = sharedPreferences.getBoolean("initial_setup", false)

        if(!initialSetupBoolean) {
            intent = Intent(applicationContext, InitialSetupActivity::class.java)
            startActivity(intent)
            finish()
        }

        setContentView(R.layout.activity_main)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.menu.getItem(1).isChecked = true
        setCurrentFragment(dashboardFragment)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_about -> setCurrentFragment(aboutFragment)
                R.id.navigation_dashboard -> setCurrentFragment(dashboardFragment)
                R.id.navigation_settings -> setCurrentFragment(settingsFragment)
            }
            true
        }
    }

    private fun setCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.container, fragment).commit()
        }
    }

}

