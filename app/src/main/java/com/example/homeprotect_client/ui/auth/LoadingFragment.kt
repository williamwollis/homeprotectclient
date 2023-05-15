package com.example.homeprotect_client.ui.auth

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.ColorSpace
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.homeprotect_client.MainActivity

import com.example.homeprotect_client.R
import com.example.homeprotect_client.data.local.PreferenceDataStoreConstants
import com.example.homeprotect_client.data.local.PreferenceDataStoreConstants.FIRST_TIME
import com.example.homeprotect_client.data.local.PreferenceDataStoreConstants.IP_KEY
import com.example.homeprotect_client.data.local.PreferenceDataStoreConstants.IS_DEVICE
import com.example.homeprotect_client.data.local.PreferenceDataStoreConstants.STREAM_KEY
import com.example.homeprotect_client.data.local.PreferenceDataStoreConstants.TOKEN_KEY
import com.example.homeprotect_client.data.local.PreferenceDataStoreConstants.UUID_KEY
import com.example.homeprotect_client.data.local.PreferenceDataStoreHelper
import com.example.homeprotect_client.remote.Constant
import com.example.homeprotect_client.remote.RestApiService
import com.example.homeprotect_client.remote.dto.Device
import kotlinx.coroutines.launch


class LoadingFragment : Fragment() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.d("LOADING", Build.MODEL)
        val preferenceDataStoreHelper = PreferenceDataStoreHelper(activity?.applicationContext)
        val apiService = RestApiService()
        lifecycleScope.launch {
            val IP_ADDRESS =
                preferenceDataStoreHelper.getFirstPreference(IP_KEY, Constant.DEFAULT_IP+":8080")
            Log.d("LOADING", IP_ADDRESS)
            val token = preferenceDataStoreHelper.getFirstPreference(TOKEN_KEY, "")
            val uuid = preferenceDataStoreHelper.getFirstPreference(UUID_KEY, "")
            val FIRST_TIME = preferenceDataStoreHelper.getFirstPreference(FIRST_TIME, true)
            val IS_DEVICE = preferenceDataStoreHelper.getFirstPreference(IS_DEVICE, false)
            val STREAM_KEY = preferenceDataStoreHelper.getFirstPreference(STREAM_KEY, "")

            Log.d("Loading", FIRST_TIME.toString() + " " + STREAM_KEY.toString())
            if (FIRST_TIME) {
                var fragmentManager = parentFragmentManager.beginTransaction()
                fragmentManager.replace(R.id.fragmentContainer, InitialFragment())
                fragmentManager.commit()
            } else {
                // Log.d("LoadingFragment", "$IP_ADDRESS | $token | $FIRST_TIME")
                apiService.verify(IP_ADDRESS, token.toString()) {
                    if (it != "Unauthorized" && it!=null) {
                        apiService.getDevices(IP_ADDRESS, token.toString()) {

                            val intent = Intent(activity, MainActivity::class.java)
                            intent.putExtra("Devices", it as ArrayList<Device>)
                            activity?.startActivity(intent)


                        }


                    } else {
                        var fragmentManager = parentFragmentManager.beginTransaction()
                        fragmentManager.replace(R.id.fragmentContainer, LoginFragment())
                        fragmentManager.commit()
                    }
                }
                Log.i("TOKEN", token)
            }
        }
        return inflater.inflate(R.layout.fragment_loading, container, false)
    }



}