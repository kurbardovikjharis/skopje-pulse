package com.haris.sensordetails.repositories

import com.haris.sensordetails.data.SensorDetailsDto
import retrofit2.Response
import retrofit2.http.GET

internal interface SensorDetailsApi {

    @GET("data24h")
    suspend fun getSensors(): Response<List<SensorDetailsDto>>
}