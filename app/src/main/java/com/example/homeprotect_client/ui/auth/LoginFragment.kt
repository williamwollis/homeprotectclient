package com.example.homeprotect_client.ui.auth

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.homeprotect_client.MainActivity
import com.example.homeprotect_client.R
import com.example.homeprotect_client.data.local.PreferenceDataStoreConstants
import com.example.homeprotect_client.data.local.PreferenceDataStoreConstants.IS_DEVICE
import com.example.homeprotect_client.data.local.PreferenceDataStoreConstants.TOKEN_KEY
import com.example.homeprotect_client.data.local.PreferenceDataStoreConstants.UUID_KEY
import com.example.homeprotect_client.data.local.PreferenceDataStoreHelper
import com.example.homeprotect_client.remote.Constant
import com.example.homeprotect_client.remote.RestApiService
import com.example.homeprotect_client.remote.dto.Device
import com.example.homeprotect_client.remote.dto.User
import kotlinx.coroutines.launch


class LoginFragment : Fragment() {

    private lateinit var loginButton: Button
    private lateinit var emailText: EditText
    private lateinit var passwordText: EditText
    private lateinit var signUpText: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var viewOfLayout = inflater!!.inflate(R.layout.fragment_login, container, false)
        loginButton = viewOfLayout.findViewById(R.id.loginButton)
        emailText = viewOfLayout.findViewById(R.id.email)
        passwordText = viewOfLayout.findViewById(R.id.password)
        signUpText = viewOfLayout.findViewById(R.id.signupText)
        val preferenceDataStoreHelper = PreferenceDataStoreHelper(activity?.applicationContext)
        val apiService = RestApiService()
        signUpText.setOnClickListener {
            var fragmentManager = parentFragmentManager.beginTransaction()
            fragmentManager.replace(R.id.fragmentContainer,RegistrationFragment())
            fragmentManager.commit()
        }
        loginButton.setOnClickListener {
            var user = User(email = emailText.text.toString(),
                            password = passwordText.text.toString())
            lifecycleScope.launch {
                val IP_ADDRESS = preferenceDataStoreHelper.getFirstPreference(
                    PreferenceDataStoreConstants.IP_KEY,Constant.DEFAULT_IP+":8080")
                Log.d("LOGIN", IP_ADDRESS)
                val CAMERA_DEVICE = preferenceDataStoreHelper.getFirstPreference(IS_DEVICE, false)
                apiService.signIn(IP_ADDRESS,user) {
                    if (it != null) {
                        if (it.token == "Incorrect email or password.") {

                            Toast.makeText(
                                activity,
                                "Incorrect email of password",
                                Toast.LENGTH_LONG
                            ).show()
                            emailText.text.clear()
                            passwordText.text.clear()
                        } else {
                            lifecycleScope.launch {
                                preferenceDataStoreHelper.putPreference(TOKEN_KEY,it.token)
                                if(CAMERA_DEVICE) {
                                    val uuid =  preferenceDataStoreHelper.getFirstPreference(UUID_KEY,"")
                                    preferenceDataStoreHelper.putPreference(PreferenceDataStoreConstants.EMAIL_KEY, emailText.text.toString())
                                    Log.d("LOGIN",emailText.text.toString()  )
                                    val warnings = ArrayList<String>()
                                    warnings.add("test")
                                    apiService.addDevice(IP_ADDRESS, it.token,Device(uuid = uuid, streamKey = "",Build.MODEL, warnings)) {
                                        var devices = ArrayList<Device>()
                                        devices.add(Device(uuid = uuid, streamKey = "", Build.MODEL,warnings))
                                        val intent = Intent(activity, MainActivity::class.java)
                                        intent.putExtra("Devices", devices as ArrayList<Device>)
                                        activity?.startActivity(intent)
                                        activity?.finish()
                                    }
                                } else {
                                    preferenceDataStoreHelper.putPreference(
                                        PreferenceDataStoreConstants.EMAIL_KEY, emailText.text.toString())
                                    apiService.getDevices(IP_ADDRESS, it.token) {

                                        val intent = Intent(activity, MainActivity::class.java)
                                        intent.putExtra("Devices", it as ArrayList<Device>)
                                        activity?.startActivity(intent)
                                        activity?.finish()

                                    }
                                }
                            }


                        }
                    } else {
                        Toast.makeText(
                            activity,
                            "Something went wrong. Cant connect to server",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }


        }
        return viewOfLayout
    }


}