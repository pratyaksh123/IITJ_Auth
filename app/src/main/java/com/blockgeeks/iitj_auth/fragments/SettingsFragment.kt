package com.blockgeeks.iitj_auth.fragments

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.security.crypto.EncryptedSharedPreferences
import com.blockgeeks.iitj_auth.BuildConfig
import com.blockgeeks.iitj_auth.R
import com.blockgeeks.iitj_auth.utils.getMasterKey
import com.google.android.material.textfield.TextInputLayout
import java.util.*
import java.util.regex.Pattern

class SettingsFragment : Fragment() {

    private lateinit var settingsNameTextView: TextView
    private lateinit var settingsProfile1TextView: TextView
    private lateinit var settingsVersionTextView: TextView
    private lateinit var settingsNameLinearLayout: LinearLayout
    private lateinit var settingsProfile1LinearLayout: LinearLayout
    private lateinit var settingsHavingProblemsLinearLayout: LinearLayout
    private lateinit var settingsRateOnGooglePlayLinearLayout: LinearLayout
    private lateinit var settingsViewPrivacyPolicyLinearLayout: LinearLayout
    private lateinit var settingsGithubRepoLinkLinearLayout: LinearLayout
    private lateinit var sharedPreferences: SharedPreferences

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = layoutInflater.inflate(R.layout.fragement_settings, container, false)
        findIds(view)

        sharedPreferences = EncryptedSharedPreferences.create(
            requireContext(),
            "initial_setup",
            getMasterKey(requireContext()),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )

        findIds(view)
        setValues()

        settingsNameLinearLayout.setOnClickListener { editNameViaDialog() }

        settingsProfile1LinearLayout.setOnClickListener { v -> editProfileViaDialog(v) }

        settingsHavingProblemsLinearLayout.setOnClickListener { troubleShootingDialog() }

        settingsRateOnGooglePlayLinearLayout.setOnClickListener { openMarketPageForApp() }

        settingsViewPrivacyPolicyLinearLayout.setOnClickListener { openPrivacyPolicyWebPage() }

        settingsGithubRepoLinkLinearLayout.setOnClickListener { openGithubRepository() }

        return view
    }

    private fun openGithubRepository() {
        val uri = Uri.parse("https://github.com/pratyaksh123/IITJ_Auth")
        val githubRepoIntent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(githubRepoIntent)
    }

    private fun openPrivacyPolicyWebPage() {
        val uri =
            Uri.parse("https://pages.flycricket.io/iitj-auth-0/privacy.html")
        val privacyPolicyIntent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(privacyPolicyIntent)
    }

    private fun openMarketPageForApp() {
        val uri = Uri.parse("market://details?id=" + context?.packageName)
        val marketIntent = Intent(Intent.ACTION_VIEW, uri)
        marketIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        try {
            startActivity(marketIntent)
        } catch (e: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + context?.packageName)
                )
            )
        }
    }

    private fun troubleShootingDialog() {
        val searchForDeviceTextView: TextView
        val dialogView: View =
            LayoutInflater.from(context).inflate(R.layout.alert_dialog_this_might_help, null, false)
        searchForDeviceTextView = dialogView.findViewById(R.id.searchForDeviceTextView)
        searchForDeviceTextView.setOnClickListener {
            val manufacturer = Build.MANUFACTURER
            val model = Build.MODEL
            val searchIntent = Intent(Intent.ACTION_WEB_SEARCH)
            searchIntent.putExtra(
                SearchManager.QUERY,
                "How to turn off battery optimization on $manufacturer $model"
            )
            startActivity(searchIntent)
        }
        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle("This might help you!")
            .setCancelable(true)
            .setView(dialogView)
            .create()
        alertDialog.show()
    }


    private fun editProfileViaDialog(v: View) {
        var profileNumber = 0
        if (v.id == R.id.settingsProfile1LinearLayout) {
            profileNumber = 1
        }
        val dialogView =
            LayoutInflater.from(context).inflate(R.layout.alert_dialog_edit_profile, null, false)
        val alertDialogUserNameEditText =
            dialogView.findViewById<EditText>(R.id.alertDialogUserNameEditText)
        alertDialogUserNameEditText.setText(
            sharedPreferences.getString(
                "username$profileNumber",
                "null"
            )
        )
        alertDialogUserNameEditText.setSelection(alertDialogUserNameEditText.text.length)
        val alertDialogPasswordEditText =
            dialogView.findViewById<EditText>(R.id.alertDialogPasswordEditText)
        alertDialogPasswordEditText.setText(
            sharedPreferences.getString(
                "password$profileNumber",
                "null"
            )
        )
        alertDialogPasswordEditText.setSelection(alertDialogPasswordEditText.text.length)
        val alertDialogTextInputLayoutUserName: TextInputLayout =
            dialogView.findViewById(R.id.alertDialogTextInputLayoutUserName)
        val alertDialogTextInputLayoutPassword: TextInputLayout =
            dialogView.findViewById(R.id.alertDialogTextInputLayoutPassword)
        val finalProfileNumber = profileNumber
        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle("Edit Profile")
            .setCancelable(false)
            .setView(dialogView)
            .setPositiveButton("Done") { _, _ ->
                var userName = false
                var password = false
                if (!TextUtils.isEmpty(alertDialogUserNameEditText.text.toString().lowercase())) {
                    alertDialogTextInputLayoutUserName.isErrorEnabled = false
                    userName = true
                } else {
                    alertDialogTextInputLayoutUserName.error = "This cannot be empty"
                    Toast.makeText(context, "Invalid Username", Toast.LENGTH_SHORT).show()
                }
                if (!TextUtils.isEmpty(alertDialogPasswordEditText.text)) {
                    password = true
                    alertDialogTextInputLayoutPassword.isErrorEnabled = false
                } else {
                    alertDialogTextInputLayoutPassword.error = "This cannot be empty"
                    Toast.makeText(context, "Invalid Password", Toast.LENGTH_SHORT).show()
                }
                if (userName && password) {
                    val editor = sharedPreferences.edit()
                    editor.putString(
                        "username$finalProfileNumber",
                        alertDialogUserNameEditText.text.toString().lowercase()
                    )
                    editor.putString(
                        "password$finalProfileNumber",
                        alertDialogPasswordEditText.text.toString()
                    )
                    editor.apply()
                    Toast.makeText(context, "Profile Updated", Toast.LENGTH_SHORT).show()
                    setValues()
                }
            }
            .setNegativeButton(
                "Cancel"
            ) { _, _ ->
            }.create()
        Objects.requireNonNull(alertDialog.window)
            ?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        alertDialog.show()
    }

    private fun editNameViaDialog() {
        val dialogView =
            LayoutInflater.from(context).inflate(R.layout.alert_dialog_edit_name, null, true)
        val alertDialogNameEditText =
            dialogView.findViewById<EditText>(R.id.alertDialogNameEditText)
        alertDialogNameEditText.setText(sharedPreferences.getString("name", "null"))
        alertDialogNameEditText.setSelection(alertDialogNameEditText.text.length)
        val alertDialogTextInputLayoutName: TextInputLayout =
            dialogView.findViewById(R.id.alertDialogTextInputLayoutName)
        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle("Edit Name")
            .setCancelable(false)
            .setView(dialogView)
            .setPositiveButton("Done") { _, _ ->
                val namePattern = Pattern.compile("^[a-zA-Z ]+$")
                val nameMatcher = namePattern.matcher(alertDialogNameEditText.text.toString())
                if (nameMatcher.matches()) {
                    alertDialogTextInputLayoutName.isErrorEnabled = false
                    val editor = sharedPreferences.edit()
                    editor.putString("name", alertDialogNameEditText.text.toString())
                    editor.apply()
                    Toast.makeText(context, "Name updated!", Toast.LENGTH_SHORT).show()
                    setValues()
                } else {
                    alertDialogTextInputLayoutName.error = "Invalid Name"
                    Toast.makeText(context, "Invalid Name Input", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(
                "Cancel"
            ) { dialog, _ -> dialog.cancel() }.create()
        Objects.requireNonNull(alertDialog.window)?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        alertDialog.show()
    }

    @SuppressLint("SetTextI18n")
    private fun setValues() {
        settingsNameTextView.text = sharedPreferences.getString("name", "null")
        settingsProfile1TextView.text = sharedPreferences.getString("username1", "null")
        settingsVersionTextView.text = "Version: " + BuildConfig.VERSION_NAME
    }

    private fun findIds(view: View) {
        settingsNameTextView = view.findViewById(R.id.settingsNameTextView)
        settingsProfile1TextView = view.findViewById(R.id.settingsProfile1TextView)
        settingsVersionTextView = view.findViewById(R.id.settingsVersionTextView)
        settingsNameLinearLayout = view.findViewById(R.id.settingsNameLinearLayout)
        settingsProfile1LinearLayout =
            view.findViewById(R.id.settingsProfile1LinearLayout)
        settingsHavingProblemsLinearLayout =
            view.findViewById(R.id.settingsHavingProblemsLinearLayout)
        settingsRateOnGooglePlayLinearLayout =
            view.findViewById(R.id.settingsRateOnGooglePlayLinearLayout)
        settingsViewPrivacyPolicyLinearLayout =
            view.findViewById(R.id.settingsViewPrivacyPolicyLinearLayout)
        settingsGithubRepoLinkLinearLayout =
            view.findViewById(R.id.settingsGithubRepoLinkLinearLayout)
    }
}