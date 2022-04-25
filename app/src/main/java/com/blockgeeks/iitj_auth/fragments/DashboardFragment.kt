package com.blockgeeks.iitj_auth.fragments

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.blockgeeks.iitj_auth.R
import com.blockgeeks.iitj_auth.services.MyForegroundService
import com.github.angads25.toggle.widget.LabeledSwitch


const val TAG = "DashBoardFragment"

class DashboardFragment : Fragment() {
    private lateinit var serviceIconSwitch: LabeledSwitch
    private lateinit var foregroundServiceIntent: Intent
    private lateinit var statusInfoInactiveTextView: TextView
    private lateinit var statusInfoActiveTextView: TextView

    override fun onAttach(context: Context) {
        Log.e(TAG, "onAttach: " + "called")
        super.onAttach(context)

        foregroundServiceIntent = Intent(context, MyForegroundService::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_dashboard, container, false)
        serviceIconSwitch = view.findViewById(R.id.serviceIconSwitch)
        statusInfoActiveTextView = view.findViewById(R.id.statusInfoActiveTextView)
        statusInfoInactiveTextView = view.findViewById(R.id.statusInfoInactiveTextView)

        if (isServiceRunningInForeground(requireContext(), MyForegroundService::class.java)) {
            serviceIconSwitch.isOn = true
            statusInfoActiveTextView.visibility = View.VISIBLE
            statusInfoInactiveTextView.visibility = View.INVISIBLE
        } else {
            serviceIconSwitch.isOn = false
            statusInfoInactiveTextView.visibility = View.VISIBLE
            statusInfoActiveTextView.visibility = View.INVISIBLE
        }

        serviceIconSwitch.setOnToggledListener { _, isOn ->
            if (!isOn) {
                Log.i(TAG, "Off")
                context?.stopService(foregroundServiceIntent)
                statusInfoInactiveTextView.visibility = View.VISIBLE
                statusInfoActiveTextView.visibility = View.INVISIBLE
            } else {
                Log.i(TAG, "On")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context?.startForegroundService(foregroundServiceIntent)
                } else {
                    context?.startService(foregroundServiceIntent)
                }
                statusInfoActiveTextView.visibility = View.VISIBLE
                statusInfoInactiveTextView.visibility = View.INVISIBLE
            }

        }

        return view
    }

    private fun isServiceRunningInForeground(context: Context, serviceClass: Class<*>): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                if (service.foreground) {
                    return true
                }
            }
        }
        return false
    }


}