package com.haris.sensordetails.repositories

import com.haris.sensordetails.data.SensorDetailsDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

internal interface SensorDetailsApi {

    @GET
    suspend fun getSensors(
        @Url url: String = "https://skopjepulse.mk/rest/data24h"
    ): Response<List<SensorDetailsDto>>
}