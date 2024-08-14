package com.digi.fieryapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.digi.fieryapp.ui.screens.AppViewModel
import com.digi.fieryapp.ui.screens.GoogleAuthUiClient
import com.digi.fieryapp.ui.screens.LoginScreen
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
                val vm: AppViewModel = viewModel()
                val nc = rememberNavController()
                val state = vm.appState.collectAsState().value
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
                        LoginScreen(
                            modifier = Modifier,
                            state = state
                        ) {
                            lifecycleScope.launch {
                                val intent = googleSingInClient.signIn()
//                                launcher.launch(signIn)
                            }
                        }
                    }
                    composable(Screen.Home.route) {
                        //HomeScreen()
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