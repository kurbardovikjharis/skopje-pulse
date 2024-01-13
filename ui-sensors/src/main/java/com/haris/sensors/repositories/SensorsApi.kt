package com.haris.sensors.repositories

import com.haris.sensors.data.SensorsDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers

internal interface SensorsApi {

    @Headers("accept:application/json")
    @GET("sensor")
    suspend fun getSensors(): Response<List<SensorsDto>>
}