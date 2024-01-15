package com.haris.sensors.repositories

import com.haris.data.network.NetworkResult
import com.haris.sensors.data.SensorEntity
import com.haris.sensors.utils.toSensorEntityList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import timber.log.Timber
import javax.inject.Inject

internal class SensorsRepositoryImpl @Inject constructor(
    private val api: SensorsApi
) : SensorsRepository {

    private val _data: MutableStateFlow<NetworkResult<List<SensorEntity>>> =
        MutableStateFlow(NetworkResult.None())

    override val data: Flow<NetworkResult<List<SensorEntity>>>
        get() = _data

    override suspend fun getSensors() {
        val cachedData = _data.value.data
        _data.value = NetworkResult.Loading(cachedData)
        try {
            val response = api.getSensors()
            val body = response.body()
            if (response.isSuccessful && body != null) {
                _data.value = NetworkResult.Success(body.toSensorEntityList())
            } else {
                _data.value = NetworkResult.Error(
                    message = response.message(),
                    data = cachedData
                )
            }
        } catch (exception: Exception) {
            Timber.e(exception)
            _data.value = NetworkResult.Error(
                message = exception.message,
                data = cachedData
            )
        }
    }
}
