package com.haris.sensordetails.repositories

import com.haris.data.network.NetworkResult
import com.haris.sensordetails.data.SensorDetailsDto
import com.haris.sensordetails.data.SensorDetailsEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.inject.Inject

private const val pm10 = "pm10"
private const val pm25 = "pm25"

internal class SensorDetailsRepositoryImpl @Inject constructor(
    private val api: SensorDetailsApi
) : SensorDetailsRepository {

    private var entity: SensorDetailsEntity? = null

    private val _data: MutableStateFlow<NetworkResult<SensorDetailsEntity>> =
        MutableStateFlow(NetworkResult.None())

    override val data: Flow<NetworkResult<SensorDetailsEntity>>
        get() = _data

    override suspend fun getSensors() {
        val now = LocalDateTime.now()
        val minus1h = now.minus(1, ChronoUnit.HOURS)

        val entity = entity
        if (minus1h.isBefore(now) && entity != null) {
            _data.value = NetworkResult.Success(entity)
            return
        }

        _data.value = NetworkResult.Loading()
        try {
            val response = api.getSensors()
            val body = response.body()
            if (response.isSuccessful && body != null) {
                _data.value = NetworkResult.Success(map(body, now))
            } else {
                _data.value = NetworkResult.Error(response.message())
            }
        } catch (e: Exception) {
            _data.value = NetworkResult.Error(e.message)
            e.printStackTrace()
        }
    }

    private fun map(data: List<SensorDetailsDto>, now: LocalDateTime): SensorDetailsEntity {
        val minus6h = now.minus(6, ChronoUnit.HOURS)
        val minus12h = now.minus(12, ChronoUnit.HOURS)

        var minus6h10pm = 0f
        var minus12h10pm = 0f
        var minus24h10pm = 0f

        var minus6h25pm = 0f
        var minus12h25pm = 0f
        var minus24h25pm = 0f

        var minus6h10pmCounter = 0
        var minus12h10pmCounter = 0
        var minus24h10pmCounter = 0

        var minus6h25pmCounter = 0
        var minus12h25pmCounter = 0
        var minus24h25pmCounter = 0

        for (item in data) {
            if (item.type != pm10 && item.type != pm25) continue

            val localDate = LocalDateTime.parse(
                item.stamp, DateTimeFormatter.ISO_ZONED_DATE_TIME
            )

            if (minus6h.isBefore(localDate)) {
                if (item.type == pm10) {
                    minus6h10pm += item.value?.toFloat() ?: 0f
                    minus6h10pmCounter++
                } else {
                    minus6h25pm += item.value?.toFloat() ?: 0f
                    minus6h25pmCounter++
                }
            }
            if (minus12h.isBefore(localDate)) {
                if (item.type == pm10) {
                    minus12h10pm += item.value?.toFloat() ?: 0f
                    minus12h10pmCounter++
                } else {
                    minus12h25pm += item.value?.toFloat() ?: 0f
                    minus12h25pmCounter++
                }
            }

            if (item.type == pm10) {
                minus24h10pm += item.value?.toFloat() ?: 0f
                minus24h10pmCounter++
            } else {
                minus24h25pm += item.value?.toFloat() ?: 0f
                minus24h25pmCounter++
            }
        }

        val entity = SensorDetailsEntity(
            avg6h10PM = (minus6h10pm / minus6h10pmCounter).toString(),
            avg12h10PM = (minus12h10pm / minus12h10pmCounter).toString(),
            avg24h10PM = (minus24h10pm / minus24h10pmCounter).toString(),
            avg6h25PM = (minus6h25pm / minus6h25pmCounter).toString(),
            avg12h25PM = (minus12h25pm / minus12h25pmCounter).toString(),
            avg24h25PM = (minus24h25pm / minus24h25pmCounter).toString(),
        )

        this.entity = entity

        return entity
    }
}
