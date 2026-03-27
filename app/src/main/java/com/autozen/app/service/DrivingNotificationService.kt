package com.autozen.app.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.autozen.app.MainActivity
import com.autozen.dashboard.model.toHealthReport
import com.autozen.obd.ObdDataSource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class DrivingNotificationService : Service() {

    @Inject lateinit var obdDataSource: ObdDataSource

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    companion object {
        const val CHANNEL_ID = "autozen_driving"
        const val CHANNEL_SAFETY = "autozen_safety"
        const val NOTIF_ID_DRIVING = 1001
        const val NOTIF_ID_SAFETY = 1002
        const val ACTION_STOP = "com.autozen.STOP_SERVICE"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
            return START_NOT_STICKY
        }
        startForeground(NOTIF_ID_DRIVING, buildDrivingNotification(0f, 0f))
        observeVehicleData()
        return START_STICKY
    }

    private fun observeVehicleData() {
        scope.launch {
            obdDataSource.dataFlow.collectLatest { data ->
                // Update foreground notification with live speed
                val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                nm.notify(NOTIF_ID_DRIVING,
                    buildDrivingNotification(data.speedKmh, data.fuelPercent))

                // Safety alerts as separate notifications
                val health = data.toHealthReport()
                if (health.alerts.isNotEmpty()) {
                    val alert = health.alerts.first()
                    nm.notify(NOTIF_ID_SAFETY, buildSafetyNotification(alert.title, alert.description))
                } else {
                    nm.cancel(NOTIF_ID_SAFETY)
                }
            }
        }
    }

    private fun buildDrivingNotification(speed: Float, fuel: Float): Notification {
        val openIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        val stopIntent = PendingIntent.getService(
            this, 0,
            Intent(this, DrivingNotificationService::class.java).apply {
                action = ACTION_STOP
            },
            PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .setContentTitle("AutoZen 行驶中")
            .setContentText("${speed.toInt()} km/h  ·  油量 ${fuel.toInt()}%")
            .setContentIntent(openIntent)
            .addAction(android.R.drawable.ic_delete, "停止", stopIntent)
            .setOngoing(true)
            .setSilent(true)
            .build()
    }

    private fun buildSafetyNotification(title: String, desc: String): Notification {
        val openIntent = PendingIntent.getActivity(
            this, 1,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, CHANNEL_SAFETY)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("⚠ $title")
            .setContentText(desc)
            .setContentIntent(openIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
    }

    private fun createNotificationChannels() {
        val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        nm.createNotificationChannel(
            NotificationChannel(CHANNEL_ID, "行驶状态",
                NotificationManager.IMPORTANCE_LOW).apply {
                description = "显示当前车速和油量"
            }
        )
        nm.createNotificationChannel(
            NotificationChannel(CHANNEL_SAFETY, "安全提醒",
                NotificationManager.IMPORTANCE_HIGH).apply {
                description = "车辆异常状态告警"
            }
        )
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
