package com.example.homeprotect_client.ui.home

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.homeprotect.StreamListFragment
import com.example.homeprotect_client.R
import com.example.homeprotect_client.data.local.PreferenceDataStoreConstants
import com.example.homeprotect_client.data.local.PreferenceDataStoreConstants.IS_DEVICE
import com.example.homeprotect_client.data.local.PreferenceDataStoreHelper
import com.example.homeprotect_client.remote.RestApiService
import com.example.homeprotect_client.remote.dto.Device
import com.example.homeprotect_client.ui.auth.LoadingFragment
import com.example.homeprotect_client.ui.auth.RegistrationFragment
import com.example.homeprotect_client.ui.home.stream.StreamFragment
import kotlinx.coroutines.launch


class HomeFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val preferenceDataStoreHelper = PreferenceDataStoreHelper(activity?.applicationContext)
        val apiService = RestApiService()
        lifecycleScope.launch {
            val CAMERA_DEVICE = preferenceDataStoreHelper.getFirstPreference(IS_DEVICE, false)
            val STREAM_KEY = preferenceDataStoreHelper.getFirstPreference(
                PreferenceDataStoreConstants.STREAM_KEY, "")
            val IP_ADDRESS = preferenceDataStoreHelper.getFirstPreference(
                PreferenceDataStoreConstants.IP_KEY,"192.168.0.12:8080")
            val uuid = preferenceDataStoreHelper.getFirstPreference(PreferenceDataStoreConstants.UUID_KEY,"")
            val token =  preferenceDataStoreHelper.getFirstPreference(PreferenceDataStoreConstants.TOKEN_KEY,"")
            val warnings = ArrayList<String>()
            warnings.add("test")
            if(CAMERA_DEVICE) {
                Log.d("Stream key", STREAM_KEY)
                    if(STREAM_KEY=="") {
                       apiService.updateStreamKey(IP_ADDRESS,token.toString(),
                            Device(uuid,STREAM_KEY,
                            Build.MODEL,warnings)
                        ) {
                           Log.d("stream key", it.toString())
                            if (it != null) {
                                lifecycleScope.launch {
                                    preferenceDataStoreHelper.putPreference(
                                        PreferenceDataStoreConstants.STREAM_KEY, it.streamKey
                                    )
                                }
                            }
                        }
                    }

                var fragmentManager = parentFragmentManager.beginTransaction()
                fragmentManager.replace(R.id.nav_host_fragment_activity_main, StreamFragment())
                fragmentManager.commit()
            }
            else {
                var fragmentManager = parentFragmentManager.beginTransaction()
                fragmentManager.replace(R.id.nav_host_fragment_activity_main, StreamListFragment())
                fragmentManager.commit()
            }
        }
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

}