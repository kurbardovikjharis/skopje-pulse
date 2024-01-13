package com.haris.sensordetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haris.data.network.NetworkResult
import com.haris.sensordetails.data.SensorDetailsEntity
import com.haris.sensordetails.interactors.GetSensorDetailsInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.annotation.concurrent.Immutable
import javax.inject.Inject

@HiltViewModel
internal class SensorDetailsViewModel @Inject constructor(
    getSensorDetailsInteractor: GetSensorDetailsInteractor
) : ViewModel() {

    private val isPM10Checked = MutableStateFlow(true)
    private val isPM25Checked = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            getSensorDetailsInteractor()
        }
    }

    val state: StateFlow<SensorDetailsViewState> = combine(
        getSensorDetailsInteractor.flow,
        isPM10Checked,
        isPM25Checked
    ) { sensorDetailsResult, isPM10Checked, isPM25Checked ->
        when (sensorDetailsResult) {
            is NetworkResult.Success -> {
                SensorDetailsViewState.Success(
                    isPM10Checked = isPM10Checked,
                    isPM25Checked = isPM25Checked,
                    sensors = sensorDetailsResult.data ?: emptyList()
                )
            }

            is NetworkResult.Loading -> {
                SensorDetailsViewState.Loading
            }

            is NetworkResult.Error -> {
                SensorDetailsViewState.Error(sensorDetailsResult.message ?: "")
            }

            is NetworkResult.None -> SensorDetailsViewState.Empty
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = SensorDetailsViewState.Empty
    )

    fun onPM10Checked(value: Boolean) {
        isPM10Checked.value = value
        isPM25Checked.value = !value
    }

    fun onPM25Checked(value: Boolean) {
        isPM25Checked.value = value
        isPM10Checked.value = !value
    }
}

@Immutable
internal sealed interface SensorDetailsViewState {

    data class Success(
        val isPM10Checked: Boolean,
        val isPM25Checked: Boolean,
        val sensors: List<SensorDetailsEntity>
    ) : SensorDetailsViewState

    data class Error(val message: String) : SensorDetailsViewState

    data object Loading : SensorDetailsViewState

    data object Empty : SensorDetailsViewState
}