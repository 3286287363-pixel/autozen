package com.autozen.weather

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.autozen.network.weather.WeatherResponse
import com.autozen.network.weather.WeatherState

@Composable
fun WeatherScreen(
    navController: NavController,
    viewModel: WeatherViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (val s = state) {
            is WeatherState.Loading -> CircularProgressIndicator()
            is WeatherState.Error -> Text(s.message, color = Color.Red, fontSize = 20.sp)
            is WeatherState.Success -> WeatherContent(s.data)
        }
    }
}

@Composable
fun WeatherContent(data: WeatherResponse) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(32.dp)
    ) {
        Text(data.name, fontSize = 28.sp, color = Color.Gray)
        Text(
            "${data.main.temp.toInt()}°C",
            fontSize = 96.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            data.weather.firstOrNull()?.description ?: "",
            fontSize = 24.sp,
            color = Color(0xFF00E5FF)
        )
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(48.dp)) {
            WeatherStat("体感", "${data.main.feels_like.toInt()}°C")
            WeatherStat("湿度", "${data.main.humidity}%")
            WeatherStat("风速", "${data.wind.speed} m/s")
        }
    }
}

@Composable
fun WeatherStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 16.sp, color = Color.Gray)
        Text(value, fontSize = 22.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
    }
}
