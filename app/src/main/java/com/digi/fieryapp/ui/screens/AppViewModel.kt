package com.digi.fieryapp.ui.screens

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class LoginStatus{
    LOGGED_IN,
    IN_PROGRESS,
    LOGGED_OUT
}

data class AppState(
    val loginStatus: LoginStatus = LoginStatus.LOGGED_OUT,
    val errorMessage: String = ""
)

class AppViewModel(
    private val auth: FirebaseAuth = Firebase.auth,
) : ViewModel() {

    private val _appState = MutableStateFlow(AppState())
    val appState = _appState.asStateFlow()

    private fun login(){

    }

}