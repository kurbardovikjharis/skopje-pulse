package com.haris.sensordetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haris.data.network.NetworkResult
import com.haris.sensordetails.data.SensorDetailsEntity
import com.haris.sensordetails.interactors.GetSensorDetailsInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.annotation.concurrent.Immutable
import javax.inject.Inject

@HiltViewModel
internal class SensorDetailsViewModel @Inject constructor(
    getSensorDetailsInteractor: GetSensorDetailsInteractor
) : ViewModel() {

    init {
        viewModelScope.launch {
            getSensorDetailsInteractor()
        }
    }

    val state: StateFlow<SensorDetailsViewState> = getSensorDetailsInteractor.flow.map {
        when (it) {
            is NetworkResult.Success -> {
                SensorDetailsViewState.Success(
                    sensors = it.data ?: emptyList()
                )
            }

            is NetworkResult.Loading -> {
                SensorDetailsViewState.Loading
            }

            is NetworkResult.Error -> {
                SensorDetailsViewState.Error(it.message ?: "")
            }

            is NetworkResult.None -> SensorDetailsViewState.Empty
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = SensorDetailsViewState.Empty
    )
}

@Immutable
internal sealed interface SensorDetailsViewState {

    data class Success(
        val sensors: List<SensorDetailsEntity>
    ) : SensorDetailsViewState

    data class Error(val message: String) : SensorDetailsViewState

    data object Loading : SensorDetailsViewState

    data object Empty : SensorDetailsViewState
}