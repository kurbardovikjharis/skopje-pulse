package com.haris.sensors.utils

import com.haris.sensors.data.SensorDto
import com.haris.sensors.data.SensorEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun List<SensorDto>.toSensorEntityList(): List<SensorEntity> =
    withContext(Dispatchers.IO) {
        return@withContext map { it.toSensorEntity() }
    }

fun SensorDto.toSensorEntity(): SensorEntity {
    return SensorEntity(
        sensorId = sensorId ?: "",
        description = description ?: ""
    )
}