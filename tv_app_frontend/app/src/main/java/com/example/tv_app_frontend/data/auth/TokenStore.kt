package com.example.tv_app_frontend.data.auth

import android.content.SharedPreferences

/**
 * PUBLIC_INTERFACE
 * Stores and retrieves auth tokens securely using EncryptedSharedPreferences.
 */
object TokenStore {
    private const val KEY_ACCESS = "access_token"
    private const val KEY_REFRESH = "refresh_token"
    private var prefs: SharedPreferences? = null

    // PUBLIC_INTERFACE
    fun initialize(sharedPrefs: SharedPreferences) {
        /** Initialize token store with secure shared preferences. */
        prefs = sharedPrefs
    }

    // PUBLIC_INTERFACE
    fun setTokens(access: String?, refresh: String?) {
        /** Save access and refresh tokens. Pass null to clear respective token. */
        prefs?.edit()?.apply {
            if (access == null) remove(KEY_ACCESS) else putString(KEY_ACCESS, access)
            if (refresh == null) remove(KEY_REFRESH) else putString(KEY_REFRESH, refresh)
        }?.apply()
    }

    // PUBLIC_INTERFACE
    fun getAccessToken(): String? {
        /** Retrieve current access token, if any. */
        return prefs?.getString(KEY_ACCESS, null)
    }

    // PUBLIC_INTERFACE
    fun getRefreshToken(): String? {
        /** Retrieve current refresh token, if any. */
        return prefs?.getString(KEY_REFRESH, null)
    }

    // PUBLIC_INTERFACE
    fun clear() {
        /** Clears all stored tokens. */
        prefs?.edit()?.clear()?.apply()
    }
}
