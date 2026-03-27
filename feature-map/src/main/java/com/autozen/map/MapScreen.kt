package com.autozen.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.*

// Dark map style JSON (minimal)
private val DARK_MAP_STYLE = """
[
  {"elementType":"geometry","stylers":[{"color":"#1a1a2e"}]},
  {"elementType":"labels.text.fill","stylers":[{"color":"#8ec3b9"}]},
  {"elementType":"labels.text.stroke","stylers":[{"color":"#1a3646"}]},
  {"featureType":"road","elementType":"geometry","stylers":[{"color":"#16213e"}]},
  {"featureType":"road","elementType":"geometry.stroke","stylers":[{"color":"#0f3460"}]},
  {"featureType":"road.highway","elementType":"geometry","stylers":[{"color":"#0f3460"}]},
  {"featureType":"water","elementType":"geometry","stylers":[{"color":"#0e1626"}]},
  {"featureType":"water","elementType":"labels.text.fill","stylers":[{"color":"#4e6d70"}]}
]
""".trimIndent()

@Composable
fun MapScreen(
    navController: NavController,
    viewModel: MapViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val cameraState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(state.currentLocation, 14f)
    }

    // Keep camera in sync when location updates
    LaunchedEffect(state.currentLocation) {
        cameraState.position = CameraPosition.fromLatLngZoom(state.currentLocation, 14f)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraState,
            properties = MapProperties(
                mapStyleOptions = MapStyleOptions(DARK_MAP_STYLE),
                isMyLocationEnabled = false
            ),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                myLocationButtonEnabled = false,
                mapToolbarEnabled = false
            )
        ) {
            // Current location marker
            Marker(
                state = MarkerState(position = state.currentLocation),
                title = state.locationName
            )
        }

        // Top info overlay
        Surface(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp)
                .widthIn(max = 320.dp),
            color = Color(0xCC0A0A1A),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("📍", fontSize = 16.sp)
                Text(
                    text = state.locationName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF00E5FF)
                )
            }
        }

        // Zoom controls (custom, car-friendly large buttons)
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MapButton("+") {
                val current = cameraState.position
                cameraState.position = CameraPosition.fromLatLngZoom(
                    current.target, (current.zoom + 1f).coerceAtMost(20f)
                )
            }
            MapButton("−") {
                val current = cameraState.position
                cameraState.position = CameraPosition.fromLatLngZoom(
                    current.target, (current.zoom - 1f).coerceAtLeast(3f)
                )
            }
        }
    }
}

@Composable
private fun MapButton(label: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = Color(0xCC1A1A2E),
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(
            modifier = Modifier.size(48.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(label, fontSize = 24.sp, color = Color.White, fontWeight = FontWeight.Light)
        }
    }
}
