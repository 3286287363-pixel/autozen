package com.autozen.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autozen.dashboard.model.DriveMode
import com.autozen.dashboard.model.VehicleData
import com.autozen.obd.ObdDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val obdDataSource: ObdDataSource
) : ViewModel() {

    private val _vehicleData = MutableStateFlow(VehicleData())
    val vehicleData: StateFlow<VehicleData> = _vehicleData

    init {
        obdDataSource.dataFlow
            .onEach { _vehicleData.value = it }
            .launchIn(viewModelScope)
    }

    fun setDriveMode(mode: DriveMode) {
        _vehicleData.value = _vehicleData.value.copy(driveMode = mode)
    }
}
