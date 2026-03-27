package com.autozen.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autozen.dashboard.model.DriveMode
import com.autozen.dashboard.model.VehicleData
import com.autozen.network.weather.WeatherRepository
import com.autozen.network.weather.WeatherState
import com.autozen.obd.ObdDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val obdDataSource: ObdDataSource,
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    private val _vehicleData = MutableStateFlow(VehicleData())
    val vehicleData: StateFlow<VehicleData> = _vehicleData

    val weatherState: StateFlow<WeatherState> = weatherRepository.state

    init {
        obdDataSource.dataFlow
            .onEach { _vehicleData.value = it }
            .launchIn(viewModelScope)

        // Fetch weather on init (uses BuildConfig key via repo)
        viewModelScope.launch {
            weatherRepository.fetchWeather(
                apiKey = com.autozen.dashboard.BuildConfig.WEATHER_API_KEY
            )
        }
    }

    fun setDriveMode(mode: DriveMode) {
        _vehicleData.value = _vehicleData.value.copy(driveMode = mode)
    }
}
