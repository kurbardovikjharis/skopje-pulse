package com.haris.sensors

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haris.sensors.data.SensorsDto
import com.haris.sensors.interactors.GetSensorsInteractor
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.annotation.concurrent.Immutable
import javax.inject.Inject

internal class SensorsViewModel @Inject constructor(
    getSensorsInteractor: GetSensorsInteractor
) : ViewModel() {

    init {
        viewModelScope.launch {
            getSensorsInteractor()
        }
    }

    val state: StateFlow<SensorsViewState> =
        combine(
            flowOf(1),
            flowOf(2),
            getSensorsInteractor.data
        ) { one, two, sensors ->
            SensorsViewState(
                title = "title",
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
    val title: String = "",
    val sensors: List<SensorsDto> = emptyList()
) {

    companion object {
        val Empty = SensorsViewState()
    }
}