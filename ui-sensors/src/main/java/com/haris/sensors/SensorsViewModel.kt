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
    private val getSensorsInteractor: GetSensorsInteractor,
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
                SensorsViewState.Loading(
                    sensors = it.data
                )
            }

            is NetworkResult.Error -> {
                SensorsViewState.Error(
                    message = it.message ?: "",
                    sensors = it.data
                )
            }

            is NetworkResult.None -> SensorsViewState.Empty
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = SensorsViewState.Empty
    )

    fun retry() {
        viewModelScope.launch {
            getSensorsInteractor()
        }
    }
}

@Immutable
internal sealed interface SensorsViewState {

    data class Success(
        val sensors: List<SensorEntity>
    ) : SensorsViewState

    data class Error(
        val message: String, val sensors: List<SensorEntity>?
    ) : SensorsViewState

    data class Loading(
        val sensors: List<SensorEntity>?
    ) : SensorsViewState

    data object Empty : SensorsViewState
}

