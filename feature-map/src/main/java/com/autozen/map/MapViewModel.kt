package com.autozen.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MapUiState(
    val currentLocation: LatLng = LatLng(39.9042, 116.4074), // Default: Beijing
    val isLocating: Boolean = false,
    val locationName: String = "北京"
)

@HiltViewModel
class MapViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState

    fun updateLocation(lat: Double, lon: Double, name: String = "") {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                currentLocation = LatLng(lat, lon),
                locationName = name.ifEmpty { _uiState.value.locationName }
            )
        }
    }
}
