package com.blockgeeks.iitj_auth.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.Fragment
import androidx.security.crypto.EncryptedSharedPreferences
import com.blockgeeks.iitj_auth.R
import com.blockgeeks.iitj_auth.fragments.AboutFragment
import com.blockgeeks.iitj_auth.fragments.DashboardFragment
import com.blockgeeks.iitj_auth.fragments.SettingsFragment
import com.blockgeeks.iitj_auth.utils.getMasterKey
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var analytics: FirebaseAnalytics
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        val aboutFragment = AboutFragment()
        val dashboardFragment = DashboardFragment()
        val settingsFragment = SettingsFragment()
        // Obtain the FirebaseAnalytics instance.
        analytics = Firebase.analytics
        val masterKey = getMasterKey(applicationContext)
        val sharedPreferences = EncryptedSharedPreferences.create(
            applicationContext,
            "initial_setup",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
        val initialSetupBoolean = sharedPreferences.getBoolean("initial_setup", false)

        if (!initialSetupBoolean) {
            intent = Intent(applicationContext, InitialSetupActivity::class.java)
            startActivity(intent)
            finish()
        }
        checkAppUpdate(applicationContext)

        setContentView(R.layout.activity_main)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.menu.getItem(1).isChecked = true
        title = getString(R.string.dashboard)
        setCurrentFragment(dashboardFragment)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_about -> {
                    title = getString(R.string.about)
                    setCurrentFragment(aboutFragment)
                }
                R.id.navigation_dashboard -> {
                    title = getString(R.string.dashboard)
                    setCurrentFragment(dashboardFragment)
                }
                R.id.navigation_settings -> {
                    title = getString(R.string.settings)
                    setCurrentFragment(settingsFragment)
                }
            }
            true
        }
    }

    private fun checkAppUpdate(context: Context) {
        val appUpdateManager = AppUpdateManagerFactory.create(context)

        // Returns an intent object that you use to check for an update.
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                // Request the update.
                appUpdateManager.startUpdateFlowForResult(
                    // Pass the intent that is returned by 'getAppUpdateInfo()'.
                    appUpdateInfo,
                    AppUpdateType.IMMEDIATE,
                    // The current activity making the update request.
                    this,
                    // Include a request code to later monitor this update request.
                    100
                )
            }
        }
    }

    private fun setCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.container, fragment).commit()
        }
    }
}

