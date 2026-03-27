package com.autozen.dashboard

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.autozen.dashboard.model.VehicleData
import com.autozen.network.weather.WeatherResponse
import kotlinx.coroutines.delay

data class SafetyAlert(
    val message: String,
    val level: AlertLevel
)

enum class AlertLevel { WARNING, DANGER }

fun buildSafetyAlerts(
    vehicleData: VehicleData,
    weather: WeatherResponse?
): List<SafetyAlert> {
    val alerts = mutableListOf<SafetyAlert>()

    if (vehicleData.fuelPercent < 15f)
        alerts.add(SafetyAlert("燃油不足 ${vehicleData.fuelPercent.toInt()}%，请尽快加油", AlertLevel.DANGER))

    if (vehicleData.coolantTempC > 105f)
        alerts.add(SafetyAlert("水温过高 ${vehicleData.coolantTempC.toInt()}°C，请注意冷却", AlertLevel.DANGER))

    if (vehicleData.speedKmh > 120f)
        alerts.add(SafetyAlert("车速 ${vehicleData.speedKmh.toInt()} km/h，请注意安全", AlertLevel.WARNING))

    weather?.let { w ->
        val desc = w.weather.firstOrNull()?.description?.lowercase() ?: ""
        if (desc.contains("rain") || desc.contains("drizzle") || desc.contains("雨"))
            alerts.add(SafetyAlert("雨天路滑，建议减速慢行", AlertLevel.WARNING))
        if (desc.contains("snow") || desc.contains("雪"))
            alerts.add(SafetyAlert("路面积雪，请谨慎驾驶", AlertLevel.DANGER))
        if (desc.contains("fog") || desc.contains("雾"))
            alerts.add(SafetyAlert("能见度低，请开启雾灯", AlertLevel.WARNING))
    }

    return alerts
}

@Composable
fun SafetyAlertBanner(
    vehicleData: VehicleData,
    weather: WeatherResponse? = null
) {
    val alerts = remember(vehicleData, weather) {
        buildSafetyAlerts(vehicleData, weather)
    }
    var currentIndex by remember { mutableIntStateOf(0) }

    // Rotate through alerts every 4 seconds
    LaunchedEffect(alerts) {
        if (alerts.isEmpty()) return@LaunchedEffect
        currentIndex = 0
        while (true) {
            delay(4000)
            currentIndex = (currentIndex + 1) % alerts.size
        }
    }

    AnimatedVisibility(
        visible = alerts.isNotEmpty(),
        enter = slideInVertically { -it } + fadeIn(),
        exit = slideOutVertically { -it } + fadeOut()
    ) {
        if (alerts.isNotEmpty()) {
            val alert = alerts[currentIndex.coerceAtMost(alerts.lastIndex)]
            val bgColor = if (alert.level == AlertLevel.DANGER)
                Color(0xFF3A1A1A) else Color(0xFF2A2A1A)
            val textColor = if (alert.level == AlertLevel.DANGER)
                Color(0xFFFF5252) else Color(0xFFFFD740)

            Surface(
                color = bgColor,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (alert.level == AlertLevel.DANGER) "⚠ " else "！",
                        color = textColor,
                        fontSize = 16.sp
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(alert.message, color = textColor, fontSize = 15.sp)
                    if (alerts.size > 1) {
                        Spacer(Modifier.weight(1f))
                        Text(
                            "${currentIndex + 1}/${alerts.size}",
                            color = textColor.copy(alpha = 0.5f),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}
