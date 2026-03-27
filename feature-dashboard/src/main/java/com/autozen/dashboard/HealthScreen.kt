package com.autozen.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.autozen.dashboard.model.HealthAlert
import com.autozen.dashboard.model.Severity
import com.autozen.dashboard.model.toHealthReport

@Composable
fun HealthScreen(
    navController: NavController,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val data by viewModel.vehicleData.collectAsState()
    val health = remember(data) { data.toHealthReport() }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("车辆健康", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
        item {
            // Score circle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(160.dp)) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val stroke = 14f
                        val sweep = 270f
                        val start = 135f
                        drawArc(Color(0xFF1A1A2E), start, sweep, false,
                            style = Stroke(stroke, cap = StrokeCap.Round))
                        val scoreColor = when {
                            health.overallScore >= 80 -> Color(0xFF4CAF50)
                            health.overallScore >= 50 -> Color(0xFFFF9800)
                            else -> Color(0xFFFF5252)
                        }
                        drawArc(scoreColor, start, sweep * health.overallScore / 100f, false,
                            style = Stroke(stroke, cap = StrokeCap.Round))
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val scoreColor = when {
                            health.overallScore >= 80 -> Color(0xFF4CAF50)
                            health.overallScore >= 50 -> Color(0xFFFF9800)
                            else -> Color(0xFFFF5252)
                        }
                        Text("${health.overallScore}", fontSize = 48.sp,
                            fontWeight = FontWeight.Bold, color = scoreColor)
                        Text("健康分", fontSize = 13.sp, color = Color.Gray)
                    }
                }
            }
        }
        if (health.alerts.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A2A1A)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("车辆状态良好", fontSize = 18.sp, color = Color(0xFF4CAF50),
                            fontWeight = FontWeight.Medium)
                    }
                }
            }
        } else {
            items(health.alerts) { alert ->
                HealthAlertCard(alert)
            }
        }
        item {
            // Real-time stats grid
            Text("实时参数", fontSize = 16.sp, color = Color(0xFF00E5FF),
                fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                HealthStatCard("车速", "${data.speedKmh.toInt()} km/h", Modifier.weight(1f))
                HealthStatCard("转速", "${data.rpm.toInt()} RPM", Modifier.weight(1f))
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                HealthStatCard("油量", "${data.fuelPercent.toInt()}%",
                    Modifier.weight(1f),
                    if (data.fuelPercent < 20f) Color(0xFFFF9800) else Color(0xFF4CAF50))
                HealthStatCard("水温", "${data.coolantTempC.toInt()}°C",
                    Modifier.weight(1f),
                    if (data.coolantTempC > 100f) Color(0xFFFF5252) else Color(0xFF4CAF50))
            }
        }
    }
}

@Composable
fun HealthAlertCard(alert: HealthAlert) {
    val (bgColor, borderColor, iconText) = when (alert.severity) {
        Severity.CRITICAL -> Triple(Color(0xFF2A1010), Color(0xFFFF5252), "🔴")
        Severity.WARNING  -> Triple(Color(0xFF2A1F0A), Color(0xFFFF9800), "🟡")
        Severity.INFO     -> Triple(Color(0xFF0A1A2A), Color(0xFF00E5FF), "🔵")
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = RoundedCornerShape(16.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            width = 1.dp
        )
    ) {
        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(iconText, fontSize = 24.sp)
            Column {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    Text(alert.title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold,
                        color = Color.White)
                    Text(alert.code, fontSize = 11.sp, color = borderColor,
                        modifier = Modifier.padding(start = 4.dp))
                }
                Spacer(Modifier.height(4.dp))
                Text(alert.description, fontSize = 13.sp, color = Color.Gray, lineHeight = 18.sp)
            }
        }
    }
}

@Composable
fun HealthStatCard(label: String, value: String, modifier: Modifier = Modifier,
                   valueColor: Color = Color.White) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A2E)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, fontSize = 12.sp, color = Color.Gray)
            Spacer(Modifier.height(4.dp))
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = valueColor)
        }
    }
}
