package com.digi.fieryapp.ui.screens

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import androidx.lifecycle.ViewModel
import com.digi.fieryapp.R
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

enum class LoginStatus {
    LOGGED_IN,
    IN_PROGRESS,
    LOGGED_OUT,
    FAILED,
}

data class AppState(
    val loginStatus: LoginStatus = LoginStatus.LOGGED_OUT,
    val errorMessage: String = "",
)

class AppViewModel() : ViewModel() {
    private val _appState = MutableStateFlow(AppState())
    val appState = _appState.asStateFlow()

    fun onSignInResult(signInResult: SignInResult) {
        if (signInResult.error != null) {
            _appState.value = AppState(
                loginStatus = LoginStatus.FAILED,
                errorMessage = signInResult.error.message ?: "Unknown error"
            )
        } else {
            _appState.value = AppState(
                loginStatus = LoginStatus.LOGGED_IN,
                errorMessage = ""
            )
        }
    }

    fun resetState(){
        _appState.value = AppState()
    }

    fun setSignInInProgress() {
        _appState.value = AppState(
            loginStatus = LoginStatus.IN_PROGRESS,
            errorMessage = ""
        )
    }
}

data class SignInResult(
    val data: UserData?,
    val error: Exception?,
)

data class UserData(
    val uid: String,
    val email: String?,
    val photoUrl: String?,
)

class GoogleAuthUiClient(
    private val context: Context,
    private val oneTapClient: SignInClient,
) {
    private val auth = Firebase.auth

    suspend fun signIn(): IntentSender? {
        val result = try {
            oneTapClient.beginSignIn(
                BeginSignInRequest.Builder()
                    .setGoogleIdTokenRequestOptions(
                        BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                            .setSupported(true)
                            .setFilterByAuthorizedAccounts(false)
                            .setServerClientId(context.getString(R.string.default_web_client_id))
                            .build()
                    )
                    .setAutoSelectEnabled(true)
                    .build()
            ).await()
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            null
        }
        return result?.pendingIntent?.intentSender
    }

    suspend fun signInWithIntent(intent: Intent): SignInResult {
        val credential = oneTapClient.getSignInCredentialFromIntent(intent)
        val googleIdToken = credential.googleIdToken
        val googleCredentials = GoogleAuthProvider.getCredential(googleIdToken, null)
        return try {
            val user = auth.signInWithCredential(googleCredentials).await().user
            SignInResult(
                data = user?.let {
                    UserData(
                        uid = it.uid,
                        email = user.email,
                        photoUrl = user.photoUrl.toString()
                    )
                },
                error = null
            )
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            SignInResult(
                data = null,
                error = e
            )
        }
    }

    suspend fun signOut() {
        try {
            oneTapClient.signOut().await()
            auth.signOut()
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
        }
    }

    fun getSignedInUser(): UserData?{
        val user = auth.currentUser
        return user?.let {
            UserData(
                uid = it.uid,
                email = user.email,
                photoUrl = user.photoUrl.toString()
            )
        }
    }
}
