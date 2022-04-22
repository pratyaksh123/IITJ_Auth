package com.blockgeeks.iitj_auth.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.blockgeeks.iitj_auth.BuildConfig
import com.blockgeeks.iitj_auth.R

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
        settingsVersionTextView.text = "Version" + BuildConfig.VERSION_NAME
        settingsGithubRepoLinkLinearLayout.setOnClickListener{
            openGithubRepository()
        }
        return view
    }

    private fun openGithubRepository() {
        val uri = Uri.parse("https://github.com/pratyaksh123/IITJ_Auth")
        val githubRepoIntent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(githubRepoIntent)
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