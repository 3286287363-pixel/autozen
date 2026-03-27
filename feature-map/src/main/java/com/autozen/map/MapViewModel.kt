package com.autozen.map

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationListener
import android.location.LocationManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MapUiState(
    val currentLocation: LatLng = LatLng(39.9042, 116.4074),
    val isLocating: Boolean = false,
    val locationName: String = "北京",
    val hasGps: Boolean = false
)

@HiltViewModel
class MapViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState

    private val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private val locationListener = LocationListener { location ->
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                currentLocation = LatLng(location.latitude, location.longitude),
                isLocating = false,
                hasGps = true
            )
        }
    }

    init { startLocationUpdates() }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        _uiState.value = _uiState.value.copy(isLocating = true)
        try {
            val provider = when {
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ->
                    LocationManager.GPS_PROVIDER
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ->
                    LocationManager.NETWORK_PROVIDER
                else -> null
            }
            provider?.let {
                locationManager.requestLocationUpdates(it, 3000L, 10f, locationListener)
                // Use last known immediately
                locationManager.getLastKnownLocation(it)?.let { loc ->
                    _uiState.value = _uiState.value.copy(
                        currentLocation = LatLng(loc.latitude, loc.longitude),
                        isLocating = false,
                        hasGps = true
                    )
                }
            } ?: run {
                _uiState.value = _uiState.value.copy(isLocating = false)
            }
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(isLocating = false)
        }
    }

    fun updateLocation(lat: Double, lon: Double, name: String = "") {
        _uiState.value = _uiState.value.copy(
            currentLocation = LatLng(lat, lon),
            locationName = name.ifEmpty { _uiState.value.locationName }
        )
    }

    override fun onCleared() {
        super.onCleared()
        runCatching { locationManager.removeUpdates(locationListener) }
    }
}
