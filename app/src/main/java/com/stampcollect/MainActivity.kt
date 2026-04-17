package com.stampcollect

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import dagger.hilt.android.AndroidEntryPoint
import com.stampcollect.ui.navigation.AppNavigation
import com.stampcollect.ui.theme.BgPrimary
import com.stampcollect.ui.theme.StampCollectionTheme

import androidx.compose.ui.Modifier
import androidx.activity.ComponentActivity

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            StampCollectionTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BgPrimary
                ) {
                    AppNavigation()
                }
            }
        }
    }
}
