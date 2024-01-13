package com.haris.sensors.repositories

import com.haris.data.network.NetworkResult
import com.haris.sensors.data.SensorEntity
import kotlinx.coroutines.flow.Flow

internal interface SensorsRepository {

    val data: Flow<NetworkResult<List<SensorEntity>>>

    suspend fun getSensors()
}