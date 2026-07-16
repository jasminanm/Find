package com.example.find.ui.map

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull

data class LocationUiState(
    val currentLocation: LatLng? = null,
    val accuracyMeters: Float? = null,
    val isLoading: Boolean = false,
    val shouldCenterCamera: Boolean = false,
    val error: String? = null
)

class LocationViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LocationUiState())
    val uiState: StateFlow<LocationUiState> = _uiState.asStateFlow()

    @SuppressLint("MissingPermission")
    fun fetchCurrentLocation(context: Context, forceRefresh: Boolean = false) {
        if (_uiState.value.isLoading && !forceRefresh) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val fused = LocationServices.getFusedLocationProviderClient(context)

                if (!forceRefresh) {
                    val cached = fused.lastLocation.await()
                    if (cached != null && isUsable(cached)) {
                        applyLocation(cached, centerCamera = _uiState.value.currentLocation == null)
                    }
                }

                val accurate = withTimeoutOrNull(15_000) {
                    fused.getCurrentLocation(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        CancellationTokenSource().token
                    ).await()
                }

                when {
                    accurate != null && isBetterThanCurrent(accurate) -> {
                        applyLocation(
                            location = accurate,
                            centerCamera = forceRefresh || _uiState.value.currentLocation == null || isSignificantImprovement(accurate)
                        )
                    }
                    _uiState.value.currentLocation == null -> {
                        _uiState.value = LocationUiState(
                            currentLocation = LatLng(38.7223, -9.1393),
                            accuracyMeters = null,
                            isLoading = false,
                            shouldCenterCamera = true,
                            error = "Localização indisponível. A usar posição por defeito."
                        )
                    }
                    else -> {
                        _uiState.value = _uiState.value.copy(isLoading = false)
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    currentLocation = _uiState.value.currentLocation ?: LatLng(38.7223, -9.1393),
                    shouldCenterCamera = _uiState.value.currentLocation == null,
                    error = e.message
                )
            }
        }
    }

    fun onCameraCentered() {
        _uiState.value = _uiState.value.copy(shouldCenterCamera = false)
    }

    private fun applyLocation(location: Location, centerCamera: Boolean) {
        _uiState.value = LocationUiState(
            currentLocation = LatLng(location.latitude, location.longitude),
            accuracyMeters = location.accuracy,
            isLoading = false,
            shouldCenterCamera = centerCamera,
            error = null
        )
    }

    private fun isUsable(location: Location): Boolean {
        val maxAgeMs = 2 * 60 * 1000L
        val isRecent = System.currentTimeMillis() - location.time <= maxAgeMs
        val isAccurateEnough = !location.hasAccuracy() || location.accuracy <= 100f
        return isRecent && isAccurateEnough
    }

    private fun isBetterThanCurrent(location: Location): Boolean {
        val currentAccuracy = _uiState.value.accuracyMeters ?: return true
        return !location.hasAccuracy() || location.accuracy < currentAccuracy
    }

    private fun isSignificantImprovement(location: Location): Boolean {
        val currentAccuracy = _uiState.value.accuracyMeters ?: return true
        return location.hasAccuracy() && location.accuracy + 15f < currentAccuracy
    }
}
