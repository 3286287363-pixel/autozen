package com.autozen.obd.model

data class VehicleData(
    val speedKmh: Float = 0f,
    val rpm: Float = 0f,
    val fuelPercent: Float = 80f,
    val coolantTempC: Float = 90f,
    val driveMode: DriveMode = DriveMode.COMFORT,
    val isEngineOn: Boolean = false
)

enum class DriveMode(val label: String) {
    SPORT("运动"),
    COMFORT("舒适"),
    ECO("节能")
}
