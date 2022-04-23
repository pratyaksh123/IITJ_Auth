package com.blockgeeks.iitj_auth.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.blockgeeks.iitj_auth.R
import com.google.android.material.textfield.TextInputLayout
import java.util.regex.Pattern

class InitialSetupActivity: Activity() {
    private lateinit var initialSetupPageHeadingTextView: TextView
    private lateinit var initialSetupNameEditText: EditText
    private var initialSetupUserName1EditText:EditText? = null
    private var initialSetupPassword1EditText:EditText? = null
    private var initialSetupTextInputLayoutName: TextInputLayout? = null
    private var initialSetupTextInputLayoutUserName1:TextInputLayout? = null
    private var initialSetupTextInputLayoutPassword1:TextInputLayout? = null
    private var initialSetupDoneButton: Button? = null
    private var userName1: String? = null
    private var password1:String? = null
    private var name:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.initial_setup_screen)

        val sharedPreferences = getSharedPreferences("initial_setup", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        findIds()

        initialSetupDoneButton!!.setOnClickListener {
            if (validateInputs()) {
                editor.putString("name", name)
                editor.putString("username1", userName1)
                editor.putString("password1", password1)
                editor.putBoolean("initial_setup", true)
                editor.apply()
                val intent = Intent(this@InitialSetupActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun validateInputs(): Boolean {
        var nameBoolean = false
        var username1Boolean = false
        var password1Boolean = false
        val namePattern = Pattern.compile("^[a-zA-Z ]+$")
        val nameMatcher = namePattern.matcher(initialSetupNameEditText.text.toString())
        if (nameMatcher.matches()) {
            nameBoolean = true
            name = initialSetupNameEditText.text.toString()
            initialSetupTextInputLayoutName!!.isErrorEnabled = false
        } else {
            initialSetupTextInputLayoutName!!.error = getString(R.string.invalid_name)
        }
        if (!TextUtils.isEmpty(initialSetupUserName1EditText!!.text.toString().lowercase())) {
            username1Boolean = true
            userName1 = initialSetupUserName1EditText!!.text.toString().lowercase()
            initialSetupTextInputLayoutUserName1!!.isErrorEnabled = false
        } else {
            initialSetupTextInputLayoutUserName1!!.error = getString(R.string.this_cannot_be_empty)
        }
        if (!TextUtils.isEmpty(initialSetupPassword1EditText!!.text)) {
            password1Boolean = true
            password1 = initialSetupPassword1EditText!!.text.toString()
            initialSetupTextInputLayoutPassword1!!.isErrorEnabled = false
        } else {
            initialSetupTextInputLayoutPassword1!!.error = getString(R.string.this_cannot_be_empty)
        }
        return nameBoolean &&
                username1Boolean &&
                password1Boolean
    }

    private fun findIds() {
        initialSetupPageHeadingTextView = findViewById(R.id.initialSetupPageHeadingTextView)
        initialSetupNameEditText = findViewById(R.id.initialSetupNameEditText)
        initialSetupUserName1EditText = findViewById(R.id.initialSetupUserName1EditText)
        initialSetupPassword1EditText = findViewById(R.id.initialSetupPassword1EditText)
        initialSetupTextInputLayoutName = findViewById(R.id.initialSetupTextInputLayoutName)
        initialSetupTextInputLayoutUserName1 =
            findViewById(R.id.initialSetupTextInputLayoutUserName1)
        initialSetupTextInputLayoutPassword1 =
            findViewById(R.id.initialSetupTextInputLayoutPassword1)
        initialSetupDoneButton = findViewById(R.id.initialSetupDoneButton)
    }
}