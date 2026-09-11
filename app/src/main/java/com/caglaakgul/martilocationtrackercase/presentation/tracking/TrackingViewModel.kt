package com.caglaakgul.martilocationtrackercase.presentation.tracking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caglaakgul.martilocationtrackercase.domain.usecase.GetCurrentLocationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrackingViewModel @Inject constructor(
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TrackingUiState())
    val uiState: StateFlow<TrackingUiState> = _uiState.asStateFlow()

    fun onLocationPermissionResult(isGranted: Boolean) {
        _uiState.update { state ->
            state.copy(hasLocationPermission = isGranted)
        }

        if (isGranted) {
            loadCurrentLocation()
        }
    }

    private fun loadCurrentLocation() {
        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(isLoadingLocation = true)
            }

            val currentLocation = getCurrentLocationUseCase()

            _uiState.update { state ->
                state.copy(
                    currentLocation = currentLocation,
                    isLoadingLocation = false
                )
            }
        }
    }
}