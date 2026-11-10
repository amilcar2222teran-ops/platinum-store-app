package com.ucompensar.platinum

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ucompensar.platinum.ui.navigation.AppNavigation
import com.ucompensar.platinum.ui.theme.PlatinumTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlatinumTheme {
                AppNavigation()
            }
        }
    }
}
