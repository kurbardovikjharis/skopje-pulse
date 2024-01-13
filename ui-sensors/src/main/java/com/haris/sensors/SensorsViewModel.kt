package com.haris.sensors

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haris.domain.ObservableLoadingCounter
import com.haris.domain.collectStatus
import com.haris.sensors.data.SensorsDto
import com.haris.sensors.interactors.GetSensorsInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.annotation.concurrent.Immutable
import javax.inject.Inject

@HiltViewModel
internal class SensorsViewModel @Inject constructor(
    getSensorsInteractor: GetSensorsInteractor
) : ViewModel() {

    private val counter = ObservableLoadingCounter()

    init {
        viewModelScope.launch {
            getSensorsInteractor(Unit).collectStatus(counter = counter)
        }
    }

    val state: StateFlow<SensorsViewState> =
        combine(
            getSensorsInteractor.data,
            counter.observable
        ) { sensors, isLoading ->
            SensorsViewState(
                isLoading = isLoading,
                sensors = sensors
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = SensorsViewState.Empty
        )
}

@Immutable
internal data class SensorsViewState(
    val isLoading: Boolean = false,
    val sensors: List<SensorsDto> = emptyList()
) {

    companion object {
        val Empty = SensorsViewState()
    }
}