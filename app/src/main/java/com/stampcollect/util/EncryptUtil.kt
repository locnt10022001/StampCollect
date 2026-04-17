package com.stampcollect.util

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object EncryptUtil {
    private const val SHARED_PREF_NAME = "StampCollectEncryptedPrefs"
    
    const val KEY_APP_LOCKED = "KEY_APP_LOCKED"
    const val KEY_THEME_PREFERENCE = "KEY_THEME_PREFERENCE"

    private var pref: SharedPreferences? = null

    private fun getMasterPref(context: Context): SharedPreferences {
        return pref ?: synchronized(this) {
            pref ?: createEncryptedSharedPref(context.applicationContext).also { pref = it }
        }
    }

    private fun createEncryptedSharedPref(context: Context): SharedPreferences {
        val masterKeyBuilder = MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && context.packageManager.hasSystemFeature("android.hardware.strongbox_keystore")) {
            masterKeyBuilder.setRequestStrongBoxBacked(true)
        }

        val masterKey = masterKeyBuilder.build()

        return EncryptedSharedPreferences.create(
            context,
            SHARED_PREF_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun saveString(context: Context, key: String, value: String) {
        getMasterPref(context).edit().putString(key, value).apply()
    }

    fun getString(context: Context, key: String, defaultValue: String? = null): String? {
        return getMasterPref(context).getString(key, defaultValue)
    }

    fun saveBoolean(context: Context, key: String, value: Boolean) {
        getMasterPref(context).edit().putBoolean(key, value).apply()
    }

    fun getBoolean(context: Context, key: String, defaultValue: Boolean = false): Boolean {
        return getMasterPref(context).getBoolean(key, defaultValue)
    }

    fun remove(context: Context, key: String) {
        getMasterPref(context).edit().remove(key).apply()
    }

    fun clear(context: Context) {
        getMasterPref(context).edit().clear().apply()
    }
}
