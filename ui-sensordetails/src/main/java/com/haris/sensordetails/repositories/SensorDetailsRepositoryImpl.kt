package com.haris.sensordetails.repositories

import com.haris.data.network.NetworkResult
import com.haris.sensordetails.data.SensorDetailsEntity
import com.haris.sensordetails.utils.toSensorEntityList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

internal class SensorDetailsRepositoryImpl @Inject constructor(
    private val api: SensorDetailsApi
) : SensorDetailsRepository {

    private val _data: MutableStateFlow<NetworkResult<List<SensorDetailsEntity>>> =
        MutableStateFlow(NetworkResult.None())

    override val data: Flow<NetworkResult<List<SensorDetailsEntity>>>
        get() = _data

    override suspend fun getSensors() {
        _data.value = NetworkResult.Loading()
        try {
            val response = api.getSensors()
            val body = response.body()
            if (response.isSuccessful && body != null) {
                _data.value = NetworkResult.Success(body.toSensorEntityList())
            } else {
                _data.value = NetworkResult.Error(
                    response.message(),
                    body?.toSensorEntityList()
                )
            }
        } catch (e: Exception) {
            _data.value = NetworkResult.Error(e.message)
            e.printStackTrace()
        }
    }
}
