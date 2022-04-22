package com.blockgeeks.iitj_auth.activities

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.blockgeeks.iitj_auth.R
import com.blockgeeks.iitj_auth.fragments.AboutFragment
import com.blockgeeks.iitj_auth.fragments.DashboardFragment
import com.blockgeeks.iitj_auth.fragments.SettingsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView

const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val aboutFragment = AboutFragment()
        val dashboardFragment = DashboardFragment()
        val settingsFragment = SettingsFragment()

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

