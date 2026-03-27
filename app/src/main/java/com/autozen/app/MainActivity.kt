package com.autozen.app

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.content.ContextCompat
import com.autozen.app.navigation.AutoZenNavHost
import com.autozen.app.service.DrivingNotificationService
import com.autozen.ui.theme.AutoZenTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startDrivingService()
        setContent {
            AutoZenTheme {
                AutoZenNavHost()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(this, DrivingNotificationService::class.java))
    }

    private fun startDrivingService() {
        val intent = Intent(this, DrivingNotificationService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(this, intent)
        } else {
            startService(intent)
        }
    }
}
