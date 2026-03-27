package com.autozen.trip

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autozen.data.trip.TripDao
import com.autozen.data.trip.TripEntity
import com.autozen.obd.ObdDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TripViewModel @Inject constructor(
    private val tripDao: TripDao,
    private val obdDataSource: ObdDataSource
) : ViewModel() {

    val trips = tripDao.getAllTrips()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Auto-trip tracking state
    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording

    private var tripStartTime: Long = 0L
    private var totalDistanceKm: Float = 0f
    private var speedSamples = mutableListOf<Float>()
    private var fuelSamples = mutableListOf<Float>()
    private var lastSpeed: Float = 0f
    private var lastSampleTime: Long = 0L

    init {
        viewModelScope.launch {
            obdDataSource.dataFlow.collect { data ->
                val now = System.currentTimeMillis()
                if (data.isEngineOn && data.speedKmh > 2f) {
                    if (!_isRecording.value) {
                        // Engine started — begin recording
                        _isRecording.value = true
                        tripStartTime = now
                        totalDistanceKm = 0f
                        speedSamples.clear()
                        fuelSamples.clear()
                        lastSampleTime = now
                        lastSpeed = data.speedKmh
                    } else {
                        // Accumulate distance: speed(km/h) * elapsed(h)
                        val elapsedH = (now - lastSampleTime) / 3_600_000f
                        totalDistanceKm += data.speedKmh * elapsedH
                        speedSamples.add(data.speedKmh)
                        fuelSamples.add(data.fuelPercent)
                        lastSampleTime = now
                        lastSpeed = data.speedKmh
                    }
                } else if (_isRecording.value && data.speedKmh <= 2f) {
                    // Car stopped — save trip if meaningful (>0.1 km)
                    if (totalDistanceKm > 0.1f) {
                        val avgSpeed = if (speedSamples.isNotEmpty()) speedSamples.average().toFloat() else 0f
                        val fuelDrop = if (fuelSamples.size >= 2) fuelSamples.first() - fuelSamples.last() else 0f
                        // Estimate L/100km: fuelDrop% of ~50L tank / distanceKm * 100
                        val fuelConsumption = if (totalDistanceKm > 0) (fuelDrop * 0.5f / totalDistanceKm * 100f) else 0f
                        tripDao.insertTrip(
                            TripEntity(
                                startTime = tripStartTime,
                                endTime = now,
                                distanceKm = String.format("%.1f", totalDistanceKm).toFloat(),
                                avgSpeedKmh = avgSpeed,
                                avgFuelConsumption = String.format("%.1f", fuelConsumption.coerceIn(0f, 30f)).toFloat()
                            )
                        )
                    }
                    _isRecording.value = false
                }
            }
        }
    }

    fun addSampleTrip() {
        viewModelScope.launch {
            tripDao.insertTrip(
                TripEntity(
                    startTime = System.currentTimeMillis() - 3600_000,
                    endTime = System.currentTimeMillis(),
                    distanceKm = 35.6f,
                    avgSpeedKmh = 62f,
                    avgFuelConsumption = 7.2f,
                    startAddress = "出发地",
                    endAddress = "目的地"
                )
            )
        }
    }

    fun deleteTrip(trip: TripEntity) {
        viewModelScope.launch { tripDao.deleteTrip(trip) }
    }
}
