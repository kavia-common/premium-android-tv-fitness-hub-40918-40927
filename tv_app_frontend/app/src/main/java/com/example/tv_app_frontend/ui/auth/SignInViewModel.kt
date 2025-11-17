package com.example.tv_app_frontend.ui.auth

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tv_app_frontend.data.auth.TokenStore

/**
 * ViewModel to handle a stubbed sign-in flow for offline demo builds.
 * No Google services or network interaction occur here.
 */
class SignInViewModel : ViewModel() {

    private val _signedIn = MutableLiveData(false)
    val signedIn: LiveData<Boolean> = _signedIn

    // PUBLIC_INTERFACE
    fun startGoogleSignIn(@Suppress("UNUSED_PARAMETER") activity: Activity) {
        /**
         * Offline demo: immediately consider user as "signed in" with a local mock token.
         * No intents or Google services are invoked.
         */
        TokenStore.setTokens(access = "LOCAL_DEMO_TOKEN", refresh = null)
        _signedIn.postValue(true)
    }

    // PUBLIC_INTERFACE
    fun handleSignIn(@Suppress("UNUSED_PARAMETER") account: Any? = null) {
        /**
         * Offline demo: already signed in via startGoogleSignIn; keep the state.
         */
        if (TokenStore.getAccessToken().isNullOrBlank()) {
            TokenStore.setTokens(access = "LOCAL_DEMO_TOKEN", refresh = null)
        }
        _signedIn.postValue(true)
    }

    companion object {
        const val RC_SIGN_IN = 9001
    }
}
