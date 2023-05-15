package com.example.homeprotect_client.remote

import android.util.Log
import com.example.homeprotect_client.remote.dto.Device
import com.example.homeprotect_client.remote.dto.User
import com.example.homeprotect_client.remote.response.AuthResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RestApiService {
    fun signUp(ip:String, userData: User, onResult: (User?) -> Unit) {
        val retrofit = UserNetwork(ip).retrofit
        retrofit.signUp(userData).enqueue(
            object : Callback<User> {
                override fun onFailure(call: Call<User>, t: Throwable) {
                    onResult(null)
                }
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    val addedUser = response.body()
                    onResult(addedUser)
                }
            }
        )
    }
    fun signIn(ip:String, userData: User, onResult: (AuthResponse?) -> Unit) {
        val retrofit = UserNetwork(ip).retrofit
        retrofit.signIn(userData).enqueue(
            object : Callback<AuthResponse> {
                override fun onFailure(call: Call<AuthResponse>, t: Throwable) {

                    onResult(null)
                }
                override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                    val message = response.body()
                    onResult(message)
                }
            }
        )
    }
    fun verify(ip:String, token: String, onResult: (String?) -> Unit) {
        val retrofit = UserNetwork(ip).retrofit
        retrofit.verify(token = "Bearer $token").enqueue(
            object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    val message = response.message()
                    Log.d("bebe",message)
                    onResult(message)
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.d("bebe","veve")
                onResult(null)
                }
            }
        )
    }
    fun getDevices(ip: String, token: String, onResult: (List<Device>?) -> Unit) {
        val retrofit = UserNetwork(ip).retrofit
        retrofit.getDevices(token = "Bearer $token").enqueue(
            object : Callback<List<Device>> {
                override fun onResponse(call: Call<List<Device>>, response: Response<List<Device>>) {
                    val devices = response.body()
                    Log.d("RestApiSer",response.message())
                    Log.d("RestApiSer",devices.toString())
                    onResult(devices)
                }

                override fun onFailure(call: Call<List<Device>>, t: Throwable) {
                    Log.d("RestApiSer",call.toString())
                    onResult(null)
                }
            }
        )
    }
    fun addDevice(ip: String, token: String, device: Device, onResult: (String?) -> Unit) {
        val retrofit = UserNetwork(ip).retrofit
        retrofit.addDevice(token = "Bearer $token", device).enqueue(
            object: Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    Log.d("RestApiService","addDevice - succeed")
                    onResult("Added")
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.d("RestApiService","addDevice - failure")
                    onResult(null)
                }

            }
        )
    }
    fun updateStreamKey(ip: String, token: String, device: Device, onResult: (Device?) -> Unit) {
        val retrofit = UserNetwork(ip).retrofit
        retrofit.updateStreamKey(token = "Bearer $token", device).enqueue(
            object: Callback<Device> {
                override fun onResponse(call: Call<Device>, response: Response<Device>) {
                    Log.d("RestApiService",response.body().toString())
                    onResult(response.body())
                }

                override fun onFailure(call: Call<Device>, t: Throwable) {
                    Log.d("RestApiService","addDevice - failure")
                    onResult(null)
                }

            }
        )
    }
    fun updateWarnings(ip: String, token: String, device: Device, onResult: (Device?) -> Unit) {
        val retrofit = UserNetwork(ip).retrofit
        retrofit.updateWarnings(token = "Bearer $token", device).enqueue(
            object: Callback<Device> {
                override fun onResponse(call: Call<Device>, response: Response<Device>) {
                    Log.d("RestApiService",response.body().toString())
                    onResult(response.body())
                }

                override fun onFailure(call: Call<Device>, t: Throwable) {
                    Log.d("RestApiService","addDevice - failure")
                    onResult(null)
                }

            }
        )
    }
}