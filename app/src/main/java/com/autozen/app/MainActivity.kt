package com.autozen.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.autozen.ui.theme.AutoZenTheme
import com.autozen.app.navigation.AutoZenNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AutoZenTheme {
                AutoZenNavHost()
            }
        }
    }
}
