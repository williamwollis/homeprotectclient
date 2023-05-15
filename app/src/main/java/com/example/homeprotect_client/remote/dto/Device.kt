package com.example.homeprotect_client.remote.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Device(
    val uuid: String,
    val streamKey: String,
    val model: String,
    val warnings: ArrayList<String>
) : Parcelable
