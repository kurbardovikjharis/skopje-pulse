package com.haris.sensordetails.utils

import com.haris.sensordetails.data.SensorDetailsDto
import com.haris.sensordetails.data.SensorDetailsEntity

fun List<SensorDetailsDto>.toSensorEntityList(): List<SensorDetailsEntity> {
    val list = mutableListOf<SensorDetailsEntity>()

    for (item in this) {
        list.add(item.toSensorEntity())
    }

    return list
}

fun SensorDetailsDto.toSensorEntity(): SensorDetailsEntity {
    return SensorDetailsEntity(
        sensorId = this.sensorId ?: "",
        stamp = this.stamp ?: "",
        type = this.type ?: "",
        position = this.position ?: "",
        value = value ?: ""
    )
}