package com.autozen.settings

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsState(
    val useRealObd: Boolean = false,
    val useMetric: Boolean = true,
    val pairedObdDevices: List<BluetoothDevice> = emptyList(),
    val selectedDeviceAddress: String = "",
    val weatherApiKey: String = ""
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val bluetoothAdapter: BluetoothAdapter?,
    private val prefs: SharedPreferences
) : ViewModel() {

    private val _state = MutableStateFlow(
        SettingsState(
            useRealObd = prefs.getBoolean("use_real_obd", false),
            useMetric = prefs.getBoolean("use_metric", true),
            selectedDeviceAddress = prefs.getString("obd_device_address", "") ?: "",
            weatherApiKey = prefs.getString("weather_api_key", "") ?: ""
        )
    )
    val state: StateFlow<SettingsState> = _state

    init { loadPairedDevices() }

    @Suppress("MissingPermission")
    private fun loadPairedDevices() {
        viewModelScope.launch {
            val devices = bluetoothAdapter?.bondedDevices
                ?.filter {
                    it.name?.contains("OBD", true) == true ||
                    it.name?.contains("ELM", true) == true ||
                    it.name?.contains("LINK", true) == true
                } ?: emptyList()
            _state.value = _state.value.copy(pairedObdDevices = devices)
        }
    }

    fun setUseRealObd(enabled: Boolean) {
        prefs.edit().putBoolean("use_real_obd", enabled).apply()
        _state.value = _state.value.copy(useRealObd = enabled)
    }

    fun setUseMetric(metric: Boolean) {
        prefs.edit().putBoolean("use_metric", metric).apply()
        _state.value = _state.value.copy(useMetric = metric)
    }

    fun selectObdDevice(address: String) {
        prefs.edit().putString("obd_device_address", address).apply()
        _state.value = _state.value.copy(selectedDeviceAddress = address)
    }

    fun setWeatherApiKey(key: String) {
        prefs.edit().putString("weather_api_key", key).apply()
        _state.value = _state.value.copy(weatherApiKey = key)
    }
}
