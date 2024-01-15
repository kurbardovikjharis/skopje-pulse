package com.haris.sensordetails.interactors

import com.haris.sensordetails.repositories.SensorDetailsRepository
import javax.inject.Inject

internal class GetSensorDetailsInteractor @Inject constructor(
    private val repository: SensorDetailsRepository
) {

    val flow = repository.data

    suspend operator fun invoke(id: String) {
        repository.getSensorDetails(id)
    }
}