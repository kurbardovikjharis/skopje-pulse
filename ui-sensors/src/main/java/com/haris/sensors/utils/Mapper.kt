package com.haris.sensors.utils

import com.haris.sensors.data.SensorDto
import com.haris.sensors.data.SensorEntity

fun List<SensorDto>.toSensorEntityList(): List<SensorEntity> {
    val list = mutableListOf<SensorEntity>()

    for (item in this) {
        list.add(item.toSensorEntity())
    }

    return list
}

fun SensorDto.toSensorEntity(): SensorEntity {
    return SensorEntity(
        sensorId = this.sensorId ?: "",
        position = this.position ?: "",
        type = this.type ?: "",
        description = description ?: "",
        comments = comments ?: "",
        status = status ?: ""
    )
}