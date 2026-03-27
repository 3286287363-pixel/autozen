package com.autozen.trip

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.autozen.data.trip.TripEntity

@Composable
fun TripStatsScreen(
    navController: NavController,
    viewModel: TripViewModel = hiltViewModel()
) {
    val trips by viewModel.trips.collectAsState()
    val recent = trips.takeLast(7)

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text("行程统计", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)

        // Summary cards
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SummaryStatCard(
                label = "总里程",
                value = "${String.format("%.1f", trips.sumOf { it.distanceKm.toDouble() })} km",
                modifier = Modifier.weight(1f),
                color = Color(0xFF00E5FF)
            )
            SummaryStatCard(
                label = "行程次数",
                value = "${trips.size} 次",
                modifier = Modifier.weight(1f),
                color = Color(0xFF7C4DFF)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SummaryStatCard(
                label = "平均均速",
                value = if (trips.isEmpty()) "-- km/h"
                        else "${trips.map { it.avgSpeedKmh }.average().toInt()} km/h",
                modifier = Modifier.weight(1f),
                color = Color(0xFFFF6D00)
            )
            SummaryStatCard(
                label = "平均油耗",
                value = if (trips.isEmpty()) "-- L"
                        else "${String.format("%.1f", trips.map { it.avgFuelConsumption }.average())} L/100",
                modifier = Modifier.weight(1f),
                color = Color(0xFF4CAF50)
            )
        }

        // Distance chart
        if (recent.isNotEmpty()) {
            Text("近期里程", fontSize = 15.sp, color = Color(0xFF00E5FF),
                fontWeight = FontWeight.SemiBold)
            Card(
                modifier = Modifier.fillMaxWidth().height(180.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A2E)),
                shape = RoundedCornerShape(16.dp)
            ) {
                LineChart(
                    data = recent.map { it.distanceKm },
                    color = Color(0xFF00E5FF),
                    modifier = Modifier.fillMaxSize().padding(16.dp)
                )
            }

            // Speed chart
            Text("近期均速", fontSize = 15.sp, color = Color(0xFFFF6D00),
                fontWeight = FontWeight.SemiBold)
            Card(
                modifier = Modifier.fillMaxWidth().height(180.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A2E)),
                shape = RoundedCornerShape(16.dp)
            ) {
                LineChart(
                    data = recent.map { it.avgSpeedKmh },
                    color = Color(0xFFFF6D00),
                    modifier = Modifier.fillMaxSize().padding(16.dp)
                )
            }
        } else {
            Box(Modifier.fillMaxWidth().height(120.dp), contentAlignment = Alignment.Center) {
                Text("暂无行程数据", color = Color.Gray, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun SummaryStatCard(label: String, value: String, modifier: Modifier, color: Color) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A2E)),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, fontSize = 12.sp, color = Color.Gray)
            Spacer(Modifier.height(4.dp))
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
fun LineChart(data: List<Float>, color: Color, modifier: Modifier = Modifier) {
    if (data.size < 2) return
    val maxVal = data.max().coerceAtLeast(1f)
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val step = w / (data.size - 1)
        val points = data.mapIndexed { i, v ->
            Offset(i * step, h - (v / maxVal) * h)
        }
        // Fill path
        val fillPath = Path().apply {
            moveTo(points.first().x, h)
            points.forEach { lineTo(it.x, it.y) }
            lineTo(points.last().x, h)
            close()
        }
        drawPath(fillPath, color.copy(alpha = 0.15f))
        // Line
        for (i in 0 until points.size - 1) {
            drawLine(color, points[i], points[i + 1],
                strokeWidth = 3f, cap = StrokeCap.Round)
        }
        // Dots
        points.forEach { p ->
            drawCircle(color, radius = 5f, center = p)
            drawCircle(Color(0xFF1A1A2E), radius = 3f, center = p)
        }
    }
}
