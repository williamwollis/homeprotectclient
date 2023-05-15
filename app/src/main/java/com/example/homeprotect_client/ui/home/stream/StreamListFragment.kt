package com.example.homeprotect

import android.content.Intent
import com.example.homeprotect_client.MainActivity
import com.example.homeprotect_client.remote.dto.Device
import com.example.homeprotect_client.ui.home.stream.StreamAdapter



import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.homeprotect_client.R
import com.example.homeprotect_client.data.local.PreferenceDataStoreConstants
import com.example.homeprotect_client.data.local.PreferenceDataStoreHelper
import com.example.homeprotect_client.remote.Constant
import com.example.homeprotect_client.remote.RestApiService
import com.example.homeprotect_client.ui.auth.LoginFragment
import com.example.homeprotect_client.ui.home.stream.PlayerFragment
import kotlinx.coroutines.launch


class StreamListFragment : Fragment() {
    private var itemsList = ArrayList<String>()
    private lateinit var streamAdapter: StreamAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var viewOfLayout = inflater.inflate(R.layout.fragment_stream_list, container, false)
        val preferenceDataStoreHelper = PreferenceDataStoreHelper(activity?.applicationContext)
        val apiService = RestApiService()


        val recyclerView: RecyclerView = viewOfLayout.findViewById(R.id.streamList)
        val layoutManager = LinearLayoutManager(activity?.applicationContext)
        recyclerView.layoutManager = layoutManager
        if (activity is MainActivity) {
            var thisActivity = activity as MainActivity
            var devices = thisActivity.intent.extras?.getParcelableArrayList<Device>("Devices")
            Log.d("Streamlist", devices.toString())
            if (devices != null) {
                for (i in devices.indices) {
                    if (i != 0) {
                        if (devices.get(i - 1) != devices.get(i)) {
                            itemsList.add(devices.get(i).model)
                        }
                    }
                }
            }
                Log.d("Streamlist", itemsList.toString())
                streamAdapter = StreamAdapter(itemsList)
                recyclerView.adapter = streamAdapter
                streamAdapter.notifyDataSetChanged()
                streamAdapter.setOnClickListener(listener = object : StreamAdapter.OnClickListener {
                    override fun onItemClick(item: String) {
                       // var fragmentManager = parentFragmentManager.beginTransaction()
                        var bundle = Bundle()
                        if (devices != null) {
                            for (device in devices) {
                                if (device.model == item) {

                                    bundle.putString("key", device.streamKey)
                                    Log.d("STREAMLIST", device.streamKey)
                                    break
                                }
                            }
                        }
                        Log.d("bundlee", bundle.toString())
                       val playerFragment = PlayerFragment()
                        playerFragment.arguments = bundle
                        fragmentManager?.beginTransaction()
                            ?.replace(R.id.nav_host_fragment_activity_main,playerFragment)?.commit()
                       // playerFragment.arguments = bundle
                       // fragmentManager.replace(
                       //     R.id.nav_host_fragment_activity_main,
                       //     playerFragment
                       // )
                       // fragmentManager.commit()
                    }

                })
            }



            return viewOfLayout
        }


    }

