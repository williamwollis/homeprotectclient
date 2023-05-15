package com.example.homeprotect_client.remote

import com.example.homeprotect_client.remote.dto.Device
import com.example.homeprotect_client.remote.dto.User
import com.example.homeprotect_client.remote.response.AuthResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface UserAPI {

    @GET("/test")
    suspend fun getTest(): User

    @POST("signup")
    fun signUp(@Body user: User): Call<User>

    @POST("signin")
    fun signIn(@Body user: User): Call<AuthResponse>

    @GET("verify")
    fun verify(@Header("Authorization") token: String): Call<String>

    @GET("devices")
    fun getDevices(@Header("Authorization") token: String): Call<List<Device>>

    @POST("addDevice")
    fun addDevice(@Header("Authorization") token: String, @Body device: Device): Call<String>

    @POST("updateStreamKey")
    fun updateStreamKey(@Header("Authorization") token: String, @Body device: Device): Call<Device>
    @POST("updateWarnings")
    fun updateWarnings(@Header("Authorization") token: String, @Body device: Device): Call<Device>
}