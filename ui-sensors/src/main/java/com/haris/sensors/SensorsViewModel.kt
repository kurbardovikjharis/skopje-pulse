package com.haris.sensors

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haris.data.network.NetworkResult
import com.haris.sensors.data.SensorEntity
import com.haris.sensors.interactors.GetSensorsInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.annotation.concurrent.Immutable
import javax.inject.Inject

@HiltViewModel
internal class SensorsViewModel @Inject constructor(
    getSensorsInteractor: GetSensorsInteractor,
) : ViewModel() {

    init {
        viewModelScope.launch {
            getSensorsInteractor()
        }
    }

    val state: StateFlow<SensorsViewState> = getSensorsInteractor.flow.map {
        when (it) {
            is NetworkResult.Success -> {
                SensorsViewState.Success(
                    sensors = it.data ?: emptyList()
                )
            }

            is NetworkResult.Loading -> {
                SensorsViewState.Loading
            }

            is NetworkResult.Error -> {
                SensorsViewState.Error(it.message ?: "")
            }

            is NetworkResult.None -> SensorsViewState.Empty
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = SensorsViewState.Empty
    )
}

@Immutable
internal sealed interface SensorsViewState {

    data class Success(
        val sensors: List<SensorEntity>
    ) : SensorsViewState

    data class Error(val message: String) : SensorsViewState

    data object Loading : SensorsViewState

    data object Empty : SensorsViewState
}

