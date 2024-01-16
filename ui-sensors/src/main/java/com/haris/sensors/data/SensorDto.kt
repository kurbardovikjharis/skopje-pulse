package com.haris.sensors.data

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SensorDto(
    val sensorId: String?,
    val position: String?,
    val type: String?,
    val description: String?,
    val comments: String?,
    val status: String?
)