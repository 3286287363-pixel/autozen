package com.autozen.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autozen.network.weather.WeatherRepository
import com.autozen.network.weather.WeatherState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// Re-export WeatherState as WeatherUiState alias for backward compat
typealias WeatherUiState = WeatherState

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    val uiState: StateFlow<WeatherState> = weatherRepository.state

    fun fetchWeather(lat: Double = 39.9042, lon: Double = 116.4074) {
        viewModelScope.launch {
            weatherRepository.fetchWeather(
                lat = lat,
                lon = lon,
                apiKey = BuildConfig.WEATHER_API_KEY
            )
        }
    }

    init { fetchWeather() }
}
