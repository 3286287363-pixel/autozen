package com.autozen.network.weather

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

sealed class WeatherState {
    object Loading : WeatherState()
    data class Success(val data: WeatherResponse) : WeatherState()
    data class Error(val message: String) : WeatherState()
}

@Singleton
class WeatherRepository @Inject constructor(
    private val weatherApi: WeatherApi
) {
    private val _state = MutableStateFlow<WeatherState>(WeatherState.Loading)
    val state: StateFlow<WeatherState> = _state

    private var lastFetchTime = 0L
    private val cacheDurationMs = 5 * 60 * 1000L // 5 minutes

    suspend fun fetchWeather(
        lat: Double = 39.9042,
        lon: Double = 116.4074,
        apiKey: String
    ) {
        val now = System.currentTimeMillis()
        if (_state.value is WeatherState.Success && now - lastFetchTime < cacheDurationMs) return
        _state.value = WeatherState.Loading
        try {
            val result = weatherApi.getCurrentWeather(lat = lat, lon = lon, apiKey = apiKey)
            _state.value = WeatherState.Success(result)
            lastFetchTime = now
        } catch (e: Exception) {
            _state.value = WeatherState.Error(e.message ?: "网络错误")
        }
    }
}
