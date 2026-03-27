package com.autozen.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.autozen.app.R
import kotlinx.coroutines.delay

@Composable
fun FocusScreen(
    navController: NavController,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val data by viewModel.vehicleData.collectAsState()
    var showExitButton by remember { mutableStateOf(false) }

    // Show exit button briefly on entry, then hide after 3s
    LaunchedEffect(Unit) {
        showExitButton = true
        delay(3000)
        showExitButton = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0F))
    ) {
        // Main focus content
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.focus_mode_title),
                fontSize = 22.sp,
                color = Color(0xFF00E5FF).copy(alpha = 0.7f),
                letterSpacing = 4.sp
            )
            Spacer(Modifier.height(32.dp))

            // Big speed display
            Text(
                text = data.speedKmh.toInt().toString(),
                fontSize = 128.sp,
                fontWeight = FontWeight.Thin,
                color = Color.White,
                lineHeight = 128.sp
            )
            Text(
                text = "km/h",
                fontSize = 24.sp,
                color = Color.Gray
            )

            Spacer(Modifier.height(48.dp))

            // Secondary info row
            Row(
                horizontalArrangement = Arrangement.spacedBy(64.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FocusStat(
                    label = stringResource(R.string.dashboard_rpm),
                    value = "${data.rpm.toInt()}",
                    unit = "RPM"
                )
                FocusStat(
                    label = stringResource(R.string.dashboard_fuel),
                    value = "${data.fuelPercent.toInt()}",
                    unit = "%",
                    warning = data.fuelPercent < 15f
                )
                FocusStat(
                    label = stringResource(R.string.dashboard_coolant),
                    value = "${data.coolantTempC.toInt()}",
                    unit = "°C",
                    warning = data.coolantTempC > 105f
                )
            }

            Spacer(Modifier.height(24.dp))
            Text(
                text = stringResource(R.string.focus_mode_subtitle),
                fontSize = 14.sp,
                color = Color(0xFF444466),
                letterSpacing = 2.sp
            )
        }

        // Exit button — fades in on tap area touch, auto-hides
        AnimatedVisibility(
            visible = showExitButton,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp)
        ) {
            OutlinedButton(
                onClick = { navController.popBackStack() },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Gray)
            ) {
                Text(stringResource(R.string.focus_exit), fontSize = 16.sp)
            }
        }

        // Re-show exit button on any interaction
        if (!showExitButton) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center),
                contentAlignment = Alignment.BottomCenter
            ) {
                TextButton(
                    onClick = {
                        showExitButton = true
                    },
                    modifier = Modifier.fillMaxSize()
                ) {}
            }
        }
    }
}

@Composable
fun FocusStat(label: String, value: String, unit: String, warning: Boolean = false) {
    val color = if (warning) Color(0xFFFF6D00) else Color(0xFF888899)
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 13.sp, color = color.copy(alpha = 0.7f))
        Row(verticalAlignment = Alignment.Bottom) {
            Text(value, fontSize = 36.sp, fontWeight = FontWeight.Light, color = color)
            Spacer(Modifier.width(3.dp))
            Text(unit, fontSize = 13.sp, color = color.copy(alpha = 0.6f),
                modifier = Modifier.padding(bottom = 6.dp))
        }
    }
}
