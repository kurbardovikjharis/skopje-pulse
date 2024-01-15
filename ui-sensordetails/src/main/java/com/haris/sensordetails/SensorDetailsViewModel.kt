package com.haris.sensordetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haris.data.network.NetworkResult
import com.haris.sensordetails.interactors.GetSensorDetailsInteractor
import com.haris.sensordetails.utils.averageToString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.annotation.concurrent.Immutable
import javax.inject.Inject

const val SENSOR_ID = "sensorId"

@HiltViewModel
internal class SensorDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getSensorDetailsInteractor: GetSensorDetailsInteractor
) : ViewModel() {

    private val id: String? = savedStateHandle.get<String>(SENSOR_ID)

    private val isPM10Checked = MutableStateFlow(true)
    private val isPM25Checked = MutableStateFlow(false)

    init {
        if (id != null) {
            viewModelScope.launch {
                getSensorDetailsInteractor(id)
            }
        }
    }

    val state: StateFlow<SensorDetailsViewState> = combine(
        getSensorDetailsInteractor.flow,
        isPM10Checked,
        isPM25Checked
    ) { sensorDetailsResult, isPM10Checked, isPM25Checked ->
        val data = sensorDetailsResult.data
        val detailsData = DetailsData(
            isPM10Checked = isPM10Checked,
            isPM25Checked = isPM25Checked,
            avg6h =
            if (isPM10Checked) averageToString(data?.avg6h10PM)
            else averageToString(data?.avg6h25PM),
            avg12h =
            if (isPM10Checked) averageToString(data?.avg12h10PM)
            else averageToString(data?.avg12h25PM),
            avg24h =
            if (isPM10Checked) averageToString(data?.avg24h10PM)
            else averageToString(data?.avg24h25PM)
        )
        when (sensorDetailsResult) {
            is NetworkResult.Success -> {
                SensorDetailsViewState.Success(detailsData)
            }

            is NetworkResult.Loading -> {
                SensorDetailsViewState.Loading(detailsData)
            }

            is NetworkResult.Error -> {
                SensorDetailsViewState.Error(
                    sensorDetailsResult.message ?: "",
                    detailsData
                )
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
        val data: DetailsData
    ) : SensorDetailsViewState

    data class Error(
        val message: String, val data: DetailsData
    ) : SensorDetailsViewState

    data class Loading(val data: DetailsData) : SensorDetailsViewState

    data object Empty : SensorDetailsViewState
}

@Immutable
internal data class DetailsData(
    val isPM10Checked: Boolean,
    val isPM25Checked: Boolean,
    val avg6h: String,
    val avg12h: String,
    val avg24h: String
) {
    fun isEmpty() = avg6h.isEmpty() && avg12h.isEmpty() && avg24h.isEmpty()
}