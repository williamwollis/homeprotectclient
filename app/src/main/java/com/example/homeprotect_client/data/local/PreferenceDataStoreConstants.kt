package com.example.homeprotect_client.data.local

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferenceDataStoreConstants {
    val TOKEN_KEY = stringPreferencesKey("TOKEN_KEY")
    val IP_KEY  = stringPreferencesKey("IP_KEY")
    val IS_DEVICE = booleanPreferencesKey("IS_DEVICE")
    val LOGIN_AUTOMATICALLY = booleanPreferencesKey("LOGIN_AUTO")
    val FIRST_TIME = booleanPreferencesKey("FIRST_TIME")
    val UUID_KEY = stringPreferencesKey("UUID_KEY")
    val STREAM_KEY = stringPreferencesKey("STREAM_KEY")
    val EMAIL_KEY = stringPreferencesKey("EMAIL_KEY")
    //settings

}