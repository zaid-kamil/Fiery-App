package com.digi.fieryapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    state: AppState,
    userData: UserData?,
    onEvent: (AppEvent) -> Unit = {},
    onLogoutClicked: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Fiery App") },
                actions = {
                    IconButton(onClick = onLogoutClicked) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Logout"
                        )
                    }
                }
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = state.message,
                    onValueChange = { onEvent(AppEvent.OnUpdateMessage(it)) },
                    label = { Text("Type your message!") },
                    modifier = Modifier.weight(8f),
                    trailingIcon = {
                        when (state.chatSendStatus) {
                            ChatSendStatus.SENDING -> {
                                CircularProgressIndicator()
                            }
                            else -> {
                                IconButton(
                                    onClick = {
                                        userData?.let {
                                            onEvent(AppEvent.OnSendEvent(userData))
                                        }
                                    },
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Default.Send,
                                        contentDescription = "send"
                                    )
                                }
                            }
                        }
                    }
                )
            }
        }
    ) {
        Column(
            modifier = modifier.padding(it)
        ) {
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    HomeScreen(
        state = AppState(),
        userData = UserData("John Doe", "John@gmail.com", "https://picsum.photos/200"),
        onLogoutClicked = {}
    )
}