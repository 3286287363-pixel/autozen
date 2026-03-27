package com.autozen.dashboard

import com.autozen.dashboard.model.toHealthReport
import com.autozen.obd.model.VehicleData
import com.autozen.dashboard.model.Severity
import org.junit.Assert.*
import org.junit.Test

class VehicleHealthTest {

    @Test
    fun `healthy vehicle has score 100 and no alerts`() {
        val data = VehicleData(
            speedKmh = 60f, rpm = 2000f,
            fuelPercent = 80f, coolantTempC = 90f
        )
        val health = data.toHealthReport()
        assertEquals(100, health.overallScore)
        assertTrue(health.alerts.isEmpty())
    }

    @Test
    fun `critical low fuel triggers DANGER alert`() {
        val data = VehicleData(fuelPercent = 5f, coolantTempC = 90f)
        val health = data.toHealthReport()
        val alert = health.alerts.find { it.code == "P0087" }
        assertNotNull(alert)
        assertEquals(Severity.CRITICAL, alert!!.severity)
        assertTrue(health.overallScore <= 80)
    }

    @Test
    fun `warning low fuel triggers WARNING alert`() {
        val data = VehicleData(fuelPercent = 15f, coolantTempC = 90f)
        val health = data.toHealthReport()
        val alert = health.alerts.find { it.code == "P0087" }
        assertNotNull(alert)
        assertEquals(Severity.WARNING, alert!!.severity)
    }

    @Test
    fun `critical high coolant triggers DANGER alert`() {
        val data = VehicleData(fuelPercent = 80f, coolantTempC = 115f)
        val health = data.toHealthReport()
        val alert = health.alerts.find { it.code == "P0217" }
        assertNotNull(alert)
        assertEquals(Severity.CRITICAL, alert!!.severity)
        assertTrue(health.overallScore <= 70)
    }

    @Test
    fun `high rpm triggers WARNING alert`() {
        val data = VehicleData(fuelPercent = 80f, coolantTempC = 90f, rpm = 7500f)
        val health = data.toHealthReport()
        val alert = health.alerts.find { it.code == "P0219" }
        assertNotNull(alert)
        assertEquals(Severity.WARNING, alert!!.severity)
    }

    @Test
    fun `multiple issues compound score reduction`() {
        val data = VehicleData(fuelPercent = 5f, coolantTempC = 115f, rpm = 7500f)
        val health = data.toHealthReport()
        assertTrue("Score should be heavily reduced", health.overallScore <= 40)
        assertEquals(3, health.alerts.size)
    }

    @Test
    fun `score never goes below zero`() {
        val data = VehicleData(fuelPercent = 1f, coolantTempC = 120f, rpm = 8000f)
        val health = data.toHealthReport()
        assertTrue(health.overallScore >= 0)
    }
}
