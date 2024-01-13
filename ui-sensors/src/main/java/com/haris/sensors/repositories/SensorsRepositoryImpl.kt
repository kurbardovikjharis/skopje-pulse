package com.haris.sensors.repositories

import com.haris.data.network.NetworkResult
import com.haris.sensors.data.SensorEntity
import com.haris.sensors.utils.toSensorEntityList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

internal class SensorsRepositoryImpl @Inject constructor(
    private val api: SensorsApi
) : SensorsRepository {

    private val _data: MutableStateFlow<NetworkResult<List<SensorEntity>>> =
        MutableStateFlow(NetworkResult.None())

    override val data: Flow<NetworkResult<List<SensorEntity>>>
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
