package com.autozen.trip

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.autozen.data.trip.TripEntity
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TripScreen(
    navController: NavController,
    viewModel: TripViewModel = hiltViewModel()
) {
    val trips by viewModel.trips.collectAsState()
    val isRecording by viewModel.isRecording.collectAsState()

    Scaffold(
        floatingActionButton = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                FloatingActionButton(
                    onClick = { navController.navigate("stats") },
                    containerColor = androidx.compose.material3.MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Icon(Icons.Default.BarChart, contentDescription = "统计")
                }
                FloatingActionButton(onClick = viewModel::addSampleTrip) {
                    Icon(Icons.Default.Add, contentDescription = "添加行程")
                }
            }
        },
        topBar = {
            if (isRecording) {
                Surface(color = Color(0xFF1B2A1B)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.FiberManualRecord,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("行程记录中…", fontSize = 14.sp, color = Color(0xFF4CAF50))
                    }
                }
            }
        }
    ) { padding ->
        if (trips.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("暂无行程记录", fontSize = 20.sp, color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(trips) { trip ->
                    TripCard(trip = trip, onDelete = { viewModel.deleteTrip(trip) })
                }
            }
        }
    }
}

@Composable
fun TripCard(trip: TripEntity, onDelete: () -> Unit) {
    val fmt = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "${fmt.format(Date(trip.startTime))} → ${fmt.format(Date(trip.endTime))}",
                    fontSize = 14.sp, color = Color.Gray
                )
                Spacer(Modifier.height(4.dp))
                Text("${trip.distanceKm} km", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("均速 ${trip.avgSpeedKmh.toInt()} km/h", fontSize = 14.sp)
                    Text("油耗 ${trip.avgFuelConsumption} L/100km", fontSize = 14.sp)
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "删除", tint = Color.Gray)
            }
        }
    }
}
