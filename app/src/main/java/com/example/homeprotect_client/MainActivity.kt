package com.example.homeprotect_client

import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ReportFragment.Companion.reportFragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.homeprotect_client.databinding.ActivityMainBinding
import com.example.homeprotect_client.remote.dto.Device

class MainActivity : AppCompatActivity() {
    companion object {
         var deviceModels: ArrayList<String> = ArrayList<String>()
    }
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val devices = intent.extras?.getParcelableArrayList<Device>("Devices")
        Log.d("MAINACTIVITY",devices.toString())
        if (devices != null) {
            for(i in devices.indices) {
                if(i!=0) {
                    deviceModels.add(devices.get(i).model)
                }
            }
        }

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_notifications, R.id.navigation_settings
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
}