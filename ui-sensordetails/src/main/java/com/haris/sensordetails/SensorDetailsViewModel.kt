package com.haris.sensordetails

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.annotation.concurrent.Immutable
import javax.inject.Inject

@HiltViewModel
internal class SensorDetailsViewModel @Inject constructor() : ViewModel() {


}

@Immutable
internal data class SensorDetailsViewState(
    val title: String = "",
) {

    companion object {
        val Empty = SensorDetailsViewState()
    }
}