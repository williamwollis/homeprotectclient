package com.example.homeprotect_client.ui.notifications

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.homeprotect_client.MainActivity
import com.example.homeprotect_client.R
import com.example.homeprotect_client.data.local.PreferenceDataStoreConstants
import com.example.homeprotect_client.data.local.PreferenceDataStoreHelper
import com.example.homeprotect_client.remote.RestApiService
import com.example.homeprotect_client.remote.dto.Device
import com.example.homeprotect_client.ui.home.stream.StreamAdapter
import kotlinx.coroutines.launch

class NotificationsFragment : Fragment() {
    private var itemsList = ArrayList<String>()
    private lateinit var notificationAdapter: NotificationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var viewOfLayout = inflater.inflate(R.layout.fragment_notifications, container, false)
        if (activity is MainActivity) {
            val apiService = RestApiService()
            val preferenceDataStoreHelper = PreferenceDataStoreHelper(activity?.applicationContext)
            val recyclerView: RecyclerView = viewOfLayout.findViewById(R.id.notificationList)
            val layoutManager = LinearLayoutManager(activity?.applicationContext)
            recyclerView.layoutManager = layoutManager
            lifecycleScope.launch {
                val ip = preferenceDataStoreHelper.getFirstPreference(PreferenceDataStoreConstants.IP_KEY,"")
                val token = preferenceDataStoreHelper.getFirstPreference(PreferenceDataStoreConstants.TOKEN_KEY,"")
                apiService.getDevices(ip, token) {
                    Log.d("NOTIFICATIONS", it.toString())
                    if(it!=null) {
                        for (i in it.indices) {
                            if(it.get(i).warnings.get(0)!="test") {
                                itemsList.add(it.get(i).warnings.get(0))
                            }
                        }
                    }
                    Log.d("NOTIFICATIONS", itemsList.toString())
                    notificationAdapter = NotificationAdapter(itemsList)
                    recyclerView.adapter = notificationAdapter
                    notificationAdapter.notifyDataSetChanged()


                }
            }

        }
        return viewOfLayout
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

}