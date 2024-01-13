package com.haris.sensors.repositories

import com.haris.sensors.data.SensorDto
import retrofit2.Response
import retrofit2.http.GET

internal interface SensorsApi {

    @GET("sensor")
    suspend fun getSensors(): Response<List<SensorDto>>
}