package com.example.tv_app_frontend.ui.auth

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tv_app_frontend.data.auth.TokenStore
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

/**
 * ViewModel to handle Google Sign-In for TV compatible flow.
 */
class SignInViewModel : ViewModel() {

    private val _signedIn = MutableLiveData(false)
    val signedIn: LiveData<Boolean> = _signedIn

    // PUBLIC_INTERFACE
    fun startGoogleSignIn(activity: Activity) {
        /** Start Google Sign-In with basic profile request. */
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("REPLACE_WITH_WEB_CLIENT_ID")
            .requestEmail()
            .build()
        val client = GoogleSignIn.getClient(activity, gso)
        val intent = client.signInIntent
        activity.startActivityForResult(intent, RC_SIGN_IN)
    }

    // PUBLIC_INTERFACE
    fun handleSignIn(account: GoogleSignInAccount?) {
        /** Store token and update UI. In production exchange idToken with backend for app tokens. */
        val idToken = account?.idToken
        if (!idToken.isNullOrBlank()) {
            // This is where the app would exchange token with backend.
            TokenStore.setTokens(access = idToken, refresh = null)
            _signedIn.postValue(true)
        } else {
            _signedIn.postValue(false)
        }
    }

    companion object {
        const val RC_SIGN_IN = 9001
    }
}
