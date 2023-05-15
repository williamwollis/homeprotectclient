package com.example.homeprotect_client.ui.auth

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.homeprotect_client.MainActivity
import com.example.homeprotect_client.R
import com.example.homeprotect_client.data.local.PreferenceDataStoreConstants
import com.example.homeprotect_client.data.local.PreferenceDataStoreConstants.EMAIL_KEY
import com.example.homeprotect_client.data.local.PreferenceDataStoreConstants.IS_DEVICE
import com.example.homeprotect_client.data.local.PreferenceDataStoreHelper
import com.example.homeprotect_client.remote.Constant

import com.example.homeprotect_client.remote.RestApiService
import com.example.homeprotect_client.remote.dto.Device
import com.example.homeprotect_client.remote.dto.User
import kotlinx.coroutines.launch

class RegistrationFragment: Fragment() {
    private lateinit var signUpButton: Button
    private lateinit var emailText: EditText
    private lateinit var passwordText: EditText
    private lateinit var loginText: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewOfLayout = inflater.inflate(R.layout.fragment_registration, container, false)
        signUpButton = viewOfLayout.findViewById(R.id.registerButton)
        emailText = viewOfLayout.findViewById(R.id.emailRegister)
        passwordText = viewOfLayout.findViewById(R.id.passwordRegister)
        loginText = viewOfLayout.findViewById(R.id.signupText)
        val preferenceDataStoreHelper = PreferenceDataStoreHelper(activity?.applicationContext)
        val apiService = RestApiService()
        signUpButton.setOnClickListener {
            var user = User(email = emailText.text.toString(),
                password = passwordText.text.toString())
            lifecycleScope.launch {
                val IP_ADDRESS = preferenceDataStoreHelper.getFirstPreference(
                    PreferenceDataStoreConstants.IP_KEY, Constant.DEFAULT_IP+":8080")
                val CAMERA_DEVICE = preferenceDataStoreHelper.getFirstPreference(IS_DEVICE, false)


              //  Log.d("RegistrationFragment", "$IP_ADDRESS | $FIRST_TIME | $CAMERA_DEVICE")
                val areFieldsBlank = user.email.isBlank() || user.password.isBlank()
                val isPasswordShort = user.password.length < 8
                val isEmailCorrect = user.email.contains("@")
                if (areFieldsBlank) {
                    Toast.makeText(activity, "Fields are blank", Toast.LENGTH_LONG).show()
                } else if (isPasswordShort) {
                    Toast.makeText(
                        activity,
                        "Password must be at least 8 characters in length",
                        Toast.LENGTH_LONG
                    ).show()
                } else if (!isEmailCorrect) {
                    Toast.makeText(activity, "Invalid email format", Toast.LENGTH_LONG).show()
                } else {
                    apiService.signUp(IP_ADDRESS,user) {
                        val warnings = ArrayList<String>()
                        warnings.add("test")
                        lifecycleScope.launch {
                            Log.d("signUp start", emailText.text.toString() + " " + passwordText + " " + IP_ADDRESS)
                                preferenceDataStoreHelper.putPreference(EMAIL_KEY, emailText.text.toString())
                            Log.d("email",emailText.text.toString())
                            Log.d("email",IP_ADDRESS)
                            apiService.signIn(IP_ADDRESS, user) {
                                lifecycleScope.launch {
                                    if (it != null) {
                                        preferenceDataStoreHelper.putPreference(
                                            PreferenceDataStoreConstants.TOKEN_KEY,
                                            it.token
                                        )


                                        if (CAMERA_DEVICE) {
                                            val uuid = preferenceDataStoreHelper.getFirstPreference(
                                                PreferenceDataStoreConstants.UUID_KEY, ""
                                            )

                                            apiService.addDevice(
                                                IP_ADDRESS,
                                                it.token,
                                                Device(uuid = uuid, streamKey = "", Build.MODEL,warnings)
                                            ) {
                                                var devices = ArrayList<Device>()
                                                devices.add(
                                                    Device(
                                                        uuid = uuid,
                                                        streamKey = "",
                                                        Build.MODEL,
                                                        warnings
                                                    )
                                                )
                                                val intent =
                                                    Intent(activity, MainActivity::class.java)
                                                intent.putExtra(
                                                    "Devices",
                                                    devices as ArrayList<Device>
                                                )
                                                activity?.startActivity(intent)
                                                activity?.finish()
                                            }
                                        } else {
                                            apiService.getDevices(IP_ADDRESS, it.token) {

                                                val intent =
                                                    Intent(activity, MainActivity::class.java)
                                                intent.putExtra("Devices", it as ArrayList<Device>)
                                                activity?.startActivity(intent)
                                                activity?.finish()

                                            }
                                        }

                                    }
                                }
                            }

                        }
                    }
                }
            }
        }
        loginText.setOnClickListener {
            var fragmentManager = parentFragmentManager.beginTransaction()
            fragmentManager.replace(R.id.fragmentContainer,LoginFragment())
            fragmentManager.commit()
        }

        return viewOfLayout
    }
}