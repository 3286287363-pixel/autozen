package com.autozen.obd

import com.autozen.dashboard.model.VehicleData
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.sin

@Singleton
class SimulatedObdDataSource @Inject constructor() : ObdDataSource {

    override val dataFlow: Flow<VehicleData> = flow {
        var tick = 0
        while (true) {
            val t = tick * 0.05f
            // Simulate a car accelerating and cruising
            val speed = (60f + 40f * sin(t)).coerceIn(0f, 240f)
            val rpm = (2000f + 1500f * sin(t * 1.2f)).coerceIn(700f, 8000f)
            val fuel = (80f - tick * 0.005f).coerceIn(0f, 100f)
            val temp = (85f + 10f * sin(t * 0.3f)).coerceIn(60f, 120f)
            emit(
                VehicleData(
                    speedKmh = speed,
                    rpm = rpm,
                    fuelPercent = fuel,
                    coolantTempC = temp,
                    isEngineOn = true
                )
            )
            tick++
            delay(100) // 10 FPS data update
        }
    }
}
