package com.haris.sensors.interactors

import com.haris.sensors.repositories.SensorsRepository
import javax.inject.Inject

internal class GetSensorsInteractor @Inject constructor(
    private val sensorsRepository: SensorsRepository
) {

    val data = sensorsRepository.data

    suspend operator fun invoke() {
        sensorsRepository.getSensors()
    }
}