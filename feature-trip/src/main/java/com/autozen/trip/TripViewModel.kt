package com.autozen.trip

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autozen.data.trip.TripDao
import com.autozen.data.trip.TripEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TripViewModel @Inject constructor(
    private val tripDao: TripDao
) : ViewModel() {

    val trips = tripDao.getAllTrips()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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
