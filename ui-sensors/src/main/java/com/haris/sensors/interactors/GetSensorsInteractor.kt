package com.haris.sensors.interactors

import com.haris.sensors.repositories.SensorsRepository
import javax.inject.Inject

internal class GetSensorsInteractor @Inject constructor(
    private val sensorsRepository: SensorsRepository
) {

    val flow = sensorsRepository.data

    suspend operator fun invoke() {
        sensorsRepository.getSensors()
    }
}