package com.haris.sensordetails.repositories

import com.haris.data.network.NetworkResult
import com.haris.sensordetails.data.SensorDetailsEntity
import com.haris.sensordetails.utils.Mapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import timber.log.Timber
import javax.inject.Inject

internal class SensorDetailsRepositoryImpl @Inject constructor(
    private val api: SensorDetailsApi,
    private val mapper: Mapper
) : SensorDetailsRepository {

    private val _data: MutableStateFlow<NetworkResult<SensorDetailsEntity>> =
        MutableStateFlow(NetworkResult.None())

    override val data: Flow<NetworkResult<SensorDetailsEntity>>
        get() = _data

    override suspend fun getSensorDetails(id: String) {
        val cachedData = mapper.getCachedData(id)
        _data.value = NetworkResult.Loading(cachedData)

        try {
            val response = api.getSensors()
            val body = response.body()
            if (response.isSuccessful && body != null) {
                _data.value = NetworkResult.Success(mapper.map(body, id))
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
