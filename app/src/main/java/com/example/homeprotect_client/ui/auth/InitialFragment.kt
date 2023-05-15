package com.example.homeprotect_client.ui.auth

import android.os.Bundle
import android.provider.ContactsContract
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.lifecycleScope
import com.example.homeprotect_client.R
import com.example.homeprotect_client.data.local.PreferenceDataStoreConstants
import com.example.homeprotect_client.data.local.PreferenceDataStoreConstants.FIRST_TIME
import com.example.homeprotect_client.data.local.PreferenceDataStoreConstants.IP_KEY
import com.example.homeprotect_client.data.local.PreferenceDataStoreConstants.IS_DEVICE
import com.example.homeprotect_client.data.local.PreferenceDataStoreConstants.LOGIN_AUTOMATICALLY
import com.example.homeprotect_client.data.local.PreferenceDataStoreConstants.UUID_KEY
import com.example.homeprotect_client.data.local.PreferenceDataStoreHelper
import com.example.homeprotect_client.remote.Constant
import kotlinx.coroutines.launch


class InitialFragment : Fragment() {

    private lateinit var ipAddress: EditText
    private lateinit var standardIpChoose: SwitchCompat
    private lateinit var cameraDevice: SwitchCompat
    private lateinit var askLogin: SwitchCompat
    private lateinit var enterButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var viewOfLayout = inflater!!.inflate(R.layout.fragment_initial, container, false)
        val preferenceDataStoreHelper = PreferenceDataStoreHelper(activity?.applicationContext)
        ipAddress = viewOfLayout.findViewById(R.id.ipAddress)
        standardIpChoose = viewOfLayout.findViewById(R.id.ipButton)
        cameraDevice = viewOfLayout.findViewById(R.id.switchButton)
        askLogin = viewOfLayout.findViewById(R.id.askButton)
        enterButton = viewOfLayout.findViewById(R.id.initialButton)
        enterButton.setOnClickListener {
            lateinit var ip: String
            val CAMERA_DEVICE = cameraDevice.isChecked
            val ASK_LOGIN = askLogin.isChecked
            if(standardIpChoose.isChecked) {
                ip = Constant.DEFAULT_IP+":8080"
                Log.d("INITIAL", ip)
            } else {
                ip = ipAddress.text.toString()
            }
            Log.d("INITIAL", ip)
            lifecycleScope.launch {
                preferenceDataStoreHelper.putPreference(IP_KEY,ip)
                preferenceDataStoreHelper.putPreference(IS_DEVICE, CAMERA_DEVICE)
                preferenceDataStoreHelper.putPreference(LOGIN_AUTOMATICALLY, ASK_LOGIN)
                preferenceDataStoreHelper.putPreference(FIRST_TIME, false)
                if(CAMERA_DEVICE && activity?.contentResolver!=null) {
                    val uuid = Settings.Secure.getString(activity?.contentResolver, Settings.Secure.ANDROID_ID)
                    Log.d("InitialFragment", uuid)
                    preferenceDataStoreHelper.putPreference(UUID_KEY, uuid)
                    val fragmentManager = parentFragmentManager.beginTransaction()
                    fragmentManager.replace(R.id.fragmentContainer,LoginFragment())
                    fragmentManager.commit()
                } else {
                    //Log.d("InitialFragment", "$ip | $CAMERA_DEVICE | $ASK_LOGIN ")
                    val fragmentManager = parentFragmentManager.beginTransaction()
                    fragmentManager.replace(R.id.fragmentContainer, RegistrationFragment())
                    fragmentManager.commit()
                }
            }

        }
        // Inflate the layout for this fragment
        return viewOfLayout
    }


}