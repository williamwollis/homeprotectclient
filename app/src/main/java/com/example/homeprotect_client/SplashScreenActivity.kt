package com.example.homeprotect_client

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.homeprotect_client.data.local.PreferenceDataStoreConstants
import com.example.homeprotect_client.data.local.PreferenceDataStoreHelper
import com.example.homeprotect_client.databinding.ActivityMainBinding
import com.example.homeprotect_client.ui.auth.LoadingFragment
import kotlinx.coroutines.launch


class SplashScreenActivity : AppCompatActivity() {
    val CAMERA_CODE = 100
    val INTERNET_CODE = 101
    val RECORD_AUDIO_CODE = 102
    val WRITE_STORAGE_CODE = 103


    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        //binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_splash_screen)
        val preferenceDataStoreHelper = PreferenceDataStoreHelper(this.applicationContext)
       // this.cacheDir.deleteRecursively()
        //checkPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_STORAGE_CODE)
      // checkPermission(android.Manifest.permission.INTERNET, INTERNET_CODE)
       // checkPermission(android.Manifest.permission.RECORD_AUDIO, RECORD_AUDIO_CODE)
       // checkPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_STORAGE_CODE)
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA,
           android.Manifest.permission.RECORD_AUDIO,android.Manifest.permission.INTERNET), CAMERA_CODE)
        //ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), CAMERA_CODE)
            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, LoadingFragment())
                .commit()



        //getPost()

        /*
        lifecycleScope.launch {
           // val token =  preferenceDataStoreHelper.getFirstPreference(TOKEN_KEY,"")
            // Log.i("TOKEN", token)
        } */



    }


    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
        } else {
            Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show()
        }
    }
}