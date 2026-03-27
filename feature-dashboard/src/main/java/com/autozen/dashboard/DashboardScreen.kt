package com.autozen.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.autozen.dashboard.model.DriveMode
import com.autozen.dashboard.model.VehicleData
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val data by viewModel.vehicleData.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        // Safety alert banner at top
        SafetyAlertBanner(vehicleData = data)

        Spacer(Modifier.height(8.dp))

        // Gauges row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // RPM gauge (left)
            GaugeWidget(
                label = "转速",
                value = data.rpm,
                maxValue = 8000f,
                unit = "RPM",
                accentColor = Color(0xFFFF6D00)
            )

            // Center info panel
            CenterPanel(
                data = data,
                onModeChange = viewModel::setDriveMode,
                onFocusClick = { navController.navigate("focus") }
            )

            // Speed gauge (right)
            GaugeWidget(
                label = "车速",
                value = data.speedKmh,
                maxValue = 240f,
                unit = "km/h",
                accentColor = Color(0xFF00E5FF)
            )
        }
    }
}

@Composable
fun GaugeWidget(
    label: String,
    value: Float,
    maxValue: Float,
    unit: String,
    accentColor: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 18.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(8.dp))
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(200.dp)) {
            ArcGauge(value = value, maxValue = maxValue, accentColor = accentColor)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = value.toInt().toString(),
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(unit, fontSize = 14.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun ArcGauge(value: Float, maxValue: Float, accentColor: Color) {
    val progress = (value / maxValue).coerceIn(0f, 1f)
    Canvas(modifier = Modifier.fillMaxSize()) {
        val strokeWidth = 16f
        val startAngle = 135f
        val sweepAngle = 270f
        // Background arc
        drawArc(
            color = Color(0xFF2A2A3A),
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
        // Value arc
        drawArc(
            color = accentColor,
            startAngle = startAngle,
            sweepAngle = sweepAngle * progress,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
    }
}

@Composable
fun CenterPanel(data: VehicleData, onModeChange: (DriveMode) -> Unit, onFocusClick: () -> Unit = {}) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Fuel & Temperature
        Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
            StatusItem(
                label = "油量",
                value = "${data.fuelPercent.toInt()}%",
                color = if (data.fuelPercent < 15f) Color(0xFFFF5252) else Color(0xFF4CAF50)
            )
            StatusItem(
                label = "水温",
                value = "${data.coolantTempC.toInt()}°C",
                color = if (data.coolantTempC > 105f) Color(0xFFFF5252) else Color(0xFFFF9800)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        // Drive mode selector
        Text("驾驶模式", fontSize = 14.sp, color = Color.Gray)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DriveMode.entries.forEach { mode ->
                FilterChip(
                    selected = data.driveMode == mode,
                    onClick = { onModeChange(mode) },
                    label = { Text(mode.label, fontSize = 14.sp) }
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        // Focus mode entry
        OutlinedButton(
            onClick = onFocusClick,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF00E5FF))
        ) {
            Text("专注驾驶", fontSize = 14.sp)
        }
    }
}

@Composable
fun StatusItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 14.sp, color = Color.Gray)
        Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = color)
    }
}
