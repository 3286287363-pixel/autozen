package com.autozen.settings

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
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
    private val bluetoothAdapter: BluetoothAdapter?
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state

    init { loadPairedDevices() }

    @Suppress("MissingPermission")
    private fun loadPairedDevices() {
        viewModelScope.launch {
            val devices = bluetoothAdapter?.bondedDevices
                ?.filter { it.name?.contains("OBD", true) == true ||
                            it.name?.contains("ELM", true) == true } ?: emptyList()
            _state.value = _state.value.copy(pairedObdDevices = devices)
        }
    }

    fun setUseRealObd(enabled: Boolean) {
        _state.value = _state.value.copy(useRealObd = enabled)
    }

    fun setUseMetric(metric: Boolean) {
        _state.value = _state.value.copy(useMetric = metric)
    }

    fun selectObdDevice(address: String) {
        _state.value = _state.value.copy(selectedDeviceAddress = address)
    }

    fun setWeatherApiKey(key: String) {
        _state.value = _state.value.copy(weatherApiKey = key)
    }
}
