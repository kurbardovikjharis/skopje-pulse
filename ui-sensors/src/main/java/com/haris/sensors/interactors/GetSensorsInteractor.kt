package com.haris.sensors.interactors

import com.haris.domain.Interactor
import com.haris.sensors.repositories.SensorsRepository
import javax.inject.Inject

internal class GetSensorsInteractor @Inject constructor(
    private val sensorsRepository: SensorsRepository
) : Interactor<Unit>() {

    val data = sensorsRepository.data

    override suspend fun doWork(params: Unit) {
        sensorsRepository.getSensors()
    }
}