package com.digi.fieryapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.digi.fieryapp.ui.screens.AppViewModel
import com.digi.fieryapp.ui.screens.GoogleAuthUiClient
import com.digi.fieryapp.ui.screens.HomeScreen
import com.digi.fieryapp.ui.screens.LoginScreen
import com.digi.fieryapp.ui.screens.LoginStatus
import com.digi.fieryapp.ui.theme.FieryAppTheme
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val googleSingInClient = GoogleAuthUiClient(
            context = this,
            oneTapClient = Identity.getSignInClient(this)
        )
        enableEdgeToEdge()
        setContent {
            FieryAppTheme {
                val vm = viewModel<AppViewModel>()
                val state by vm.appState.collectAsStateWithLifecycle()
                val nc = rememberNavController()
                val context = LocalContext.current
                NavHost(navController = nc, startDestination = Screen.Login.route) {
                    composable(Screen.Login.route) {
                        val launcher = rememberLauncherForActivityResult(
                            contract = ActivityResultContracts.StartIntentSenderForResult()
                        ) { result ->
                            if (result.resultCode == RESULT_OK) {
                                lifecycleScope.launch {
                                    val signInResult = googleSingInClient.signInWithIntent(
                                        intent = result.data ?: return@launch // if there is no result, just return
                                    )
                                    vm.onSignInResult(signInResult)
                                }
                            }
                        }

                        LaunchedEffect(key1 = state.loginStatus) {
                            when (state.loginStatus) {
                                LoginStatus.LOGGED_IN -> {
                                    Toast.makeText(context, "Logged in", Toast.LENGTH_SHORT).show()
                                    nc.navigate(Screen.Home.route)
                                }
                                LoginStatus.LOGGED_OUT -> {
                                    Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
                                }
                                LoginStatus.IN_PROGRESS -> {
                                    Toast.makeText(context, "In progress", Toast.LENGTH_SHORT)
                                        .show()
                                }
                                LoginStatus.FAILED->{
                                    Toast.makeText(context, "Failed", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }
                        }
                        LoginScreen(
                            modifier = Modifier,
                            state = state
                        ) {
                            vm.setSignInInProgress()
                            lifecycleScope.launch {
                                val signInIntentSender = googleSingInClient.signIn()
                                launcher.launch(
                                    IntentSenderRequest.Builder(
                                        signInIntentSender ?: return@launch
                                    ).build()
                                )
                            }
                        }
                    }
                    composable(Screen.Home.route) {
                        HomeScreen(
                            modifier = Modifier,
                            state = state,
                            userData = googleSingInClient.getSignedInUser()
                        ){
                            lifecycleScope.launch {
                                googleSingInClient.signOut()
                                vm.resetState()
                                nc.navigate(Screen.Login.route)
                            }
                        }
                    }
                }
            }
        }
    }
}

enum class Screen(val route: String) {
    Home("home"),
    Login("login"),
}