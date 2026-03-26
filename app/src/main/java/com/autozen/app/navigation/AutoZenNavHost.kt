package com.autozen.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.autozen.dashboard.DashboardScreen
import com.autozen.trip.TripScreen
import com.autozen.weather.WeatherScreen

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object Trip : Screen("trip")
    object Weather : Screen("weather")
}

@Composable
fun AutoZenNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.Dashboard.route) {
        composable(Screen.Dashboard.route) { DashboardScreen(navController) }
        composable(Screen.Trip.route) { TripScreen(navController) }
        composable(Screen.Weather.route) { WeatherScreen(navController) }
    }
}
