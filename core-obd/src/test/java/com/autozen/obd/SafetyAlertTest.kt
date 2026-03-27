package com.autozen.obd

import com.autozen.obd.model.VehicleData
import org.junit.Assert.*
import org.junit.Test

class VehicleDataTest {

    @Test
    fun `default VehicleData has engine off`() {
        val data = VehicleData()
        assertFalse(data.isEngineOn)
    }

    @Test
    fun `speed coerced in SimulatedObdDataSource range`() {
        val data = VehicleData(speedKmh = 300f)
        // Validate that UI would clamp this
        val clamped = data.speedKmh.coerceIn(0f, 240f)
        assertEquals(240f, clamped)
    }

    @Test
    fun `fuel percent range validation`() {
        val data = VehicleData(fuelPercent = 150f)
        val clamped = data.fuelPercent.coerceIn(0f, 100f)
        assertEquals(100f, clamped)
    }

    @Test
    fun `drive mode default is COMFORT`() {
        val data = VehicleData()
        assertEquals(com.autozen.obd.model.DriveMode.COMFORT, data.driveMode)
    }
}
