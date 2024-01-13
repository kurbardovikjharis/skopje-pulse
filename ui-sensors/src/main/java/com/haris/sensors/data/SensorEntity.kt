package com.haris.sensors.data

data class SensorEntity(
    val sensorId: String,
    val position: String,
    val type: String,
    val description: String,
    val comments: String,
    val status: String
)