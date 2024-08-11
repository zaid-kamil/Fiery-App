package com.digi.fieryapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.digi.fieryapp.ui.AppNavigation
import com.digi.fieryapp.ui.theme.FieryAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FieryAppTheme {
                AppNavigation()
            }
        }
    }
}
