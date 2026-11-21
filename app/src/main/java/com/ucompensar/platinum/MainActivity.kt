package com.ucompensar.platinum

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.FragmentActivity
import com.ucompensar.platinum.ui.navigation.AppNavigation
import com.ucompensar.platinum.ui.theme.PlatinumTheme

class MainActivity : FragmentActivity() {
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
