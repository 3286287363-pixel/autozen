package com.autozen.app.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Map
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
import com.autozen.map.MapScreen
import com.autozen.trip.TripScreen
import com.autozen.weather.WeatherScreen

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "仪表盘", Icons.Default.DirectionsCar)
    object Trip : Screen("trip", "行程", Icons.Default.History)
    object Weather : Screen("weather", "天气", Icons.Default.WbSunny)
    object Map : Screen("map", "地图", Icons.Default.Map)
}

const val FOCUS_ROUTE = "focus"

val bottomNavScreens = listOf(Screen.Dashboard, Screen.Trip, Screen.Weather, Screen.Map)

@Composable
fun AutoZenNavHost() {
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route
    val showBottomBar = currentRoute != FOCUS_ROUTE

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavScreens.forEach { screen ->
                        NavigationBarItem(
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
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) { DashboardScreen(navController) }
            composable(Screen.Trip.route) { TripScreen(navController) }
            composable(Screen.Weather.route) { WeatherScreen(navController) }
            composable(Screen.Map.route) { MapScreen(navController) }
            composable(FOCUS_ROUTE) { FocusScreen(navController) }
        }
    }
}
