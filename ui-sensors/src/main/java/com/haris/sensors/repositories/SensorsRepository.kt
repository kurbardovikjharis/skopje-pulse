package com.haris.sensors.repositories

import com.haris.sensors.data.SensorsDto
import kotlinx.coroutines.flow.Flow

internal interface SensorsRepository {

    val data: Flow<List<SensorsDto>>
    suspend fun getSensors()
}