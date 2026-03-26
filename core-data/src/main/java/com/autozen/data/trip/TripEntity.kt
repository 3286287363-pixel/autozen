package com.autozen.data.trip

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trips")
data class TripEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val startTime: Long,
    val endTime: Long,
    val distanceKm: Float,
    val avgSpeedKmh: Float,
    val avgFuelConsumption: Float, // L/100km
    val startAddress: String = "",
    val endAddress: String = ""
)
