package com.autozen.dashboard.model

data class VehicleHealth(
    val overallScore: Int = 100,
    val alerts: List<HealthAlert> = emptyList()
)

data class HealthAlert(
    val code: String,
    val title: String,
    val description: String,
    val severity: Severity
)

enum class Severity { INFO, WARNING, CRITICAL }

fun VehicleData.toHealthReport(): VehicleHealth {
    val alerts = mutableListOf<HealthAlert>()
    var score = 100

    if (fuelPercent < 10f) {
        alerts.add(HealthAlert("P0087", "燃油压力低", "油量不足 ${fuelPercent.toInt()}%，请立即加油", Severity.CRITICAL))
        score -= 20
    } else if (fuelPercent < 20f) {
        alerts.add(HealthAlert("P0087", "油量偏低", "剩余油量 ${fuelPercent.toInt()}%，建议加油", Severity.WARNING))
        score -= 10
    }

    if (coolantTempC > 110f) {
        alerts.add(HealthAlert("P0217", "发动机过热", "冷却液温度 ${coolantTempC.toInt()}°C，请立即停车检查", Severity.CRITICAL))
        score -= 30
    } else if (coolantTempC > 100f) {
        alerts.add(HealthAlert("P0217", "水温偏高", "冷却液温度 ${coolantTempC.toInt()}°C，注意散热", Severity.WARNING))
        score -= 15
    }

    if (rpm > 7000f) {
        alerts.add(HealthAlert("P0219", "转速过高", "发动机转速 ${rpm.toInt()} RPM，超过正常范围", Severity.WARNING))
        score -= 10
    }

    return VehicleHealth(overallScore = score.coerceAtLeast(0), alerts = alerts)
}
