package com.autozen.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autozen.network.weather.WeatherApi
import com.autozen.network.weather.WeatherResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class WeatherUiState {
    object Loading : WeatherUiState()
    data class Success(val data: WeatherResponse) : WeatherUiState()
    data class Error(val message: String) : WeatherUiState()
}

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherApi: WeatherApi
) : ViewModel() {

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val uiState: StateFlow<WeatherUiState> = _uiState

    // Default: Beijing coordinates for demo
    fun fetchWeather(lat: Double = 39.9042, lon: Double = 116.4074) {
        viewModelScope.launch {
            _uiState.value = WeatherUiState.Loading
            try {
                val result = weatherApi.getCurrentWeather(
                    lat = lat, lon = lon,
                    apiKey = BuildConfig.WEATHER_API_KEY
                )
                _uiState.value = WeatherUiState.Success(result)
            } catch (e: Exception) {
                _uiState.value = WeatherUiState.Error(e.message ?: "网络错误")
            }
        }
    }

    init { fetchWeather() }
}
