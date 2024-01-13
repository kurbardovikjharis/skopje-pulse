package com.haris.sensors.repositories

import com.haris.sensors.data.SensorsDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

internal class SensorsRepositoryImpl @Inject constructor(
    private val api: SensorsApi
) : SensorsRepository {

    private val _data: MutableStateFlow<List<SensorsDto>> = MutableStateFlow(emptyList())
    override val data: Flow<List<SensorsDto>>
        get() = _data

    override suspend fun getSensors() {
        try {
            val response = api.getSensors()
            _data.value = response.body() ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
