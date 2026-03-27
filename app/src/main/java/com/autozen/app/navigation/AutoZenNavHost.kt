package com.autozen.app.navigation

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.autozen.dashboard.DashboardScreen
import com.autozen.dashboard.FocusScreen
import com.autozen.dashboard.HealthScreen
import com.autozen.map.MapScreen
import com.autozen.settings.SettingsScreen
import com.autozen.trip.TripDetailScreen
import com.autozen.trip.TripScreen
import com.autozen.trip.TripStatsScreen
import com.autozen.weather.WeatherScreen

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "仪表盘", Icons.Default.DirectionsCar)
    object Trip     : Screen("trip",      "行程",   Icons.Default.History)
    object Weather  : Screen("weather",   "天气",   Icons.Default.WbSunny)
    object Map      : Screen("map",       "地图",   Icons.Default.Map)
    object Health   : Screen("health",    "健康",   Icons.Default.Favorite)
    object Settings : Screen("settings",  "设置",   Icons.Default.Settings)
}

const val FOCUS_ROUTE = "focus"
const val STATS_ROUTE = "stats"

val railScreens = listOf(
    Screen.Dashboard, Screen.Trip, Screen.Weather,
    Screen.Map, Screen.Health, Screen.Settings
)

@Composable
fun AutoZenNavHost() {
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route
    val showRail = currentRoute != FOCUS_ROUTE

    Row(modifier = Modifier.fillMaxSize()) {
        // Left NavigationRail — matches AAOS landscape layout convention
        if (showRail) {
            NavigationRail {
                railScreens.forEach { screen ->
                    NavigationRailItem(
                        selected = currentRoute == screen.route,
                        onClick = {
                            if (currentRoute != screen.route) {
                                navController.navigate(screen.route) {
                                    popUpTo(Screen.Dashboard.route) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) }
                    )
                }
            }
        }

        // Main content area
        Scaffold { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Dashboard.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Screen.Dashboard.route) { DashboardScreen(navController) }
                composable(Screen.Trip.route)      { TripScreen(navController) }
                composable(Screen.Weather.route)   { WeatherScreen(navController) }
                composable(Screen.Map.route)       { MapScreen(navController) }
                composable(Screen.Health.route)    { HealthScreen(navController) }
                composable(Screen.Settings.route)  { SettingsScreen(navController) }
                composable(FOCUS_ROUTE)            { FocusScreen(navController) }
                composable(STATS_ROUTE)            { TripStatsScreen(navController) }
                composable("trip_detail/{tripId}") { back ->
                    val tripId = back.arguments?.getString("tripId")?.toLongOrNull() ?: 0L
                    TripDetailScreen(tripId = tripId, navController = navController)
                }
            }
        }
    }
}
