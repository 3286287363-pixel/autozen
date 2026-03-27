package com.autozen.obd

import com.autozen.obd.model.VehicleData
import kotlinx.coroutines.flow.Flow

interface ObdDataSource {
    val dataFlow: Flow<VehicleData>
}
