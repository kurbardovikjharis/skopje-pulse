package com.haris.sensordetails.repositories

import com.haris.data.network.NetworkResult
import com.haris.sensordetails.data.SensorDetailsEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

@Singleton
internal interface SensorDetailsRepository {

    val data: Flow<NetworkResult<SensorDetailsEntity>>

    suspend fun getSensorDetails(id: String)
}