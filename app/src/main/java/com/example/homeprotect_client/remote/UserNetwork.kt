package com.example.homeprotect_client.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UserNetwork(ip: String) {

   val retrofit by lazy {
       Retrofit.Builder()
           .baseUrl("http://$ip")
           .addConverterFactory(GsonConverterFactory.create())
           .build()
           .create(UserAPI::class.java)
   }

}