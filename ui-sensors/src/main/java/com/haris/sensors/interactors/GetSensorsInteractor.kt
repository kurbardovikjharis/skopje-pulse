package com.haris.sensors.interactors

import com.haris.sensors.repositories.SensorsRepository
import javax.inject.Inject

internal class GetSensorsInteractor @Inject constructor(
    private val repository: SensorsRepository
) {

    val flow = repository.data

    suspend operator fun invoke() {
        repository.getSensors()
    }
}