package com.digi.fieryapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.digi.fieryapp.R

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    state: AppState,
    onLoginClicked: () -> Unit = {},
) {
    Scaffold {
        Box(
            modifier = modifier
                .padding(it)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.gem1),
                    contentDescription = null,
                    modifier = Modifier.size(70.dp)
                )
                Spacer(modifier = Modifier.padding(16.dp))
                Text(
                    text = "Welcome to FieryApp",
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.padding(16.dp))

                if (state.loginStatus == LoginStatus.IN_PROGRESS) {
                    CircularProgressIndicator()
                } else {
                    OutlinedButton(
                        onClick = onLoginClicked,
                        modifier = Modifier.width(200.dp)
                    ) {
                        Text(text = "Login with Google")
                        Spacer(modifier = Modifier.width(8.dp))
                        Image(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                        )
                    }
                }
            }
            Image(
                painter = painterResource(id = R.drawable.kitty),
                contentDescription = null,
                modifier = Modifier.align(Alignment.BottomStart)
            )
        }
    }
}

@Preview
@Composable
private fun LoginScreenPreview() {
    LoginScreen(
        state = AppState()
    )
}