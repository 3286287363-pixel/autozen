package com.autozen.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// AAOS prefers dark theme for driving safety
private val AutoZenDarkColors = darkColorScheme(
    primary = Color(0xFF00E5FF),        // Cyan accent
    onPrimary = Color(0xFF001F24),
    secondary = Color(0xFFFF6D00),      // Orange for warnings
    background = Color(0xFF0A0A0F),     // Near-black background
    surface = Color(0xFF12121A),
    onBackground = Color(0xFFE0E0E0),
    onSurface = Color(0xFFE0E0E0),
)

@Composable
fun AutoZenTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AutoZenDarkColors,
        typography = AutoZenTypography,
        content = content
    )
}
