package com.autozen.trip

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.autozen.data.trip.TripEntity
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TripDetailScreen(
    tripId: Long,
    navController: NavController,
    viewModel: TripViewModel = hiltViewModel()
) {
    val trips by viewModel.trips.collectAsState()
    val trip = trips.find { it.id == tripId }

    if (trip == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("行程不存在", color = Color.Gray)
        }
        return
    }

    val fmt = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    val durationMin = ((trip.endTime - trip.startTime) / 60_000).toInt()

    Column(modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)) {
        // Top bar
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "返回", tint = Color.White)
            }
            Text("行程详情", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        // Time range
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A2E)),
            shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("出发  ${fmt.format(Date(trip.startTime))}",
                    fontSize = 14.sp, color = Color.Gray)
                Text("到达  ${fmt.format(Date(trip.endTime))}",
                    fontSize = 14.sp, color = Color.Gray)
                Text("历时  ${durationMin} 分钟",
                    fontSize = 14.sp, color = Color(0xFF00E5FF))
            }
        }

        // Stats grid
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            TripDetailStat("里程", "${trip.distanceKm} km",
                Color(0xFF00E5FF), Modifier.weight(1f))
            TripDetailStat("均速", "${trip.avgSpeedKmh.toInt()} km/h",
                Color(0xFFFF6D00), Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            TripDetailStat("油耗", "${trip.avgFuelConsumption} L/100",
                Color(0xFF4CAF50), Modifier.weight(1f))
            TripDetailStat("评分", tripScore(trip),
                Color(0xFF7C4DFF), Modifier.weight(1f))
        }

        // Speed simulation chart
        Text("速度曲线（模拟）", fontSize = 15.sp,
            color = Color(0xFF00E5FF), fontWeight = FontWeight.SemiBold)
        Card(
            modifier = Modifier.fillMaxWidth().height(160.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A2E)),
            shape = RoundedCornerShape(14.dp)
        ) {
            SimulatedSpeedChart(trip = trip, modifier = Modifier.fillMaxSize().padding(16.dp))
        }
    }
}

@Composable
fun TripDetailStat(label: String, value: String, color: Color, modifier: Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A2E)),
        shape = RoundedCornerShape(12.dp)
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

private fun tripScore(trip: TripEntity): String {
    var score = 100
    if (trip.avgSpeedKmh > 100) score -= 20
    if (trip.avgFuelConsumption > 10f) score -= 15
    val durationH = (trip.endTime - trip.startTime) / 3_600_000f
    if (durationH > 3f) score -= 10
    return "${score.coerceAtLeast(60)}分"
}

@Composable
fun SimulatedSpeedChart(trip: TripEntity, modifier: Modifier = Modifier) {
    val durationMs = trip.endTime - trip.startTime
    val points = 20
    val data = (0 until points).map { i ->
        val t = i.toFloat() / points
        val speed = when {
            t < 0.1f -> trip.avgSpeedKmh * t * 10f
            t > 0.85f -> trip.avgSpeedKmh * (1f - t) * 6.67f
            else -> trip.avgSpeedKmh * (0.8f + 0.4f * kotlin.math.sin(t * 12f))
        }.coerceIn(0f, 200f)
        speed
    }
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val maxVal = data.max().coerceAtLeast(1f)
        val step = w / (data.size - 1)
        val pts = data.mapIndexed { i, v ->
            Offset(i * step, h - (v / maxVal) * h)
        }
        val fill = Path().apply {
            moveTo(pts.first().x, h)
            pts.forEach { lineTo(it.x, it.y) }
            lineTo(pts.last().x, h)
            close()
        }
        drawPath(fill, Color(0xFF00E5FF).copy(alpha = 0.12f))
        for (i in 0 until pts.size - 1)
            drawLine(Color(0xFF00E5FF), pts[i], pts[i+1], 3f, cap = StrokeCap.Round)
        pts.forEach {
            drawCircle(Color(0xFF00E5FF), 4f, it)
            drawCircle(Color(0xFF1A1A2E), 2f, it)
        }
    }
}
