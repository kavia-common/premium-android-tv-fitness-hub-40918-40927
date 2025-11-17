package com.example.tv_app_frontend.core

import android.app.Application
import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.tv_app_frontend.data.auth.TokenStore
import com.example.tv_app_frontend.data.remote.ApiClient

/**
 * Application class to initialize app-wide singletons like secure storage and Retrofit.
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize EncryptedSharedPreferences backed TokenStore
        val masterKey = MasterKey.Builder(this)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        val securePrefs = EncryptedSharedPreferences.create(
            this,
            TOKEN_PREFS,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        TokenStore.initialize(securePrefs)

        // Initialize API client with Auth Interceptor
        ApiClient.initialize(this)
    }

    companion object {
        private const val TOKEN_PREFS = "secure_token_prefs"

        // PUBLIC_INTERFACE
        fun appContext(context: Context): Context {
            /** Returns a safe application context. */
            return context.applicationContext
        }
    }
}
