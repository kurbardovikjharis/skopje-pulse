package com.haris.sensordetails.datasource

import com.haris.data.network.NetworkResult
import com.haris.sensordetails.data.SensorDetailsDto
import com.haris.sensordetails.data.SensorDetailsEntity
import com.haris.sensordetails.repositories.SensorDetailsApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

/*
The first time, the DataSource simply filters the data and creates an instance of SensorDetailsEntity.
The second time, once the data is cached, it only iterates until the last update.

EG: if the user fetches new data after a minute and there are 3 new entries, we only iterate
through the 3 new entries, instead of around 25k.

removePast24HourValues() is also called since we have to remove the data which is stale - older
than 24h - this means additional 1 minute (if called 1 minute after the latest call)
of data iteration (few entries).
*/

private const val pm10 = "pm10"
private const val pm25 = "pm25"

@Singleton
internal class RemoteDataSource @Inject constructor(
    private val api: SensorDetailsApi
) {

    private var cachedSensorValues = emptyList<SensorDetailsDto>()
    private var sensorValueCounterMap = mutableMapOf<String, SensorValueCounter>()
    var localDateTimeMap = mutableMapOf<String, Long>()

    suspend fun getSensors(id: String) = flow {
        val cachedData = sensorValueCounterMap[id]?.toSensorDetailsEntity()

        emit(NetworkResult.Loading(cachedData))
        try {
            val response = api.getSensorDetails()
            val body = response.body()
            if (response.isSuccessful && body != null) {
                emit(NetworkResult.Success(map(body, id)))
            } else {
                emit(error(message = response.message(), cachedData = cachedData))
            }
        } catch (exception: Exception) {
            Timber.e(exception)
            emit(error(message = exception.message ?: "", cachedData = cachedData))
        }
    }

    private fun error(
        message: String,
        cachedData: SensorDetailsEntity?
    ): NetworkResult.Error<SensorDetailsEntity> {
        return NetworkResult.Error(
            message = message,
            data = cachedData
        )
    }

    suspend fun map(sensorValues: List<SensorDetailsDto>, id: String): SensorDetailsEntity =
        withContext(Dispatchers.IO) {
            val now = LocalDateTime.now()
            val minus6h = now.minus(6, ChronoUnit.HOURS)
            val minus12h = now.minus(12, ChronoUnit.HOURS)

            val epochSecond = localDateTimeMap[id]
            val lastUpdate =
                if (epochSecond != null)
                    LocalDateTime.ofEpochSecond(epochSecond, 0, ZoneOffset.UTC)
                else null

            var value6h10pm = 0.0
            var value12h10pm = 0.0
            var value24h10pm = 0.0

            var value6h25pm = 0.0
            var value12h25pm = 0.0
            var value24h25pm = 0.0

            var counter6h10pm = 0
            var counter12h10pm = 0
            var counter24h10pm = 0

            var counter6h25pm = 0
            var counter12h25pm = 0
            var counter24h25pm = 0

            var counter = 0
            for (i in (sensorValues.lastIndex downTo 0)) {
                counter++

                val item = sensorValues[i]

                val localDate = LocalDateTime.parse(
                    item.stamp, DateTimeFormatter.ISO_ZONED_DATE_TIME
                )

                if (lastUpdate?.isAfter(localDate) == true) break

                if (item.type != pm10 && item.type != pm25) continue
                if (item.sensorId != id) continue

                if (minus6h.isBefore(localDate)) {
                    if (item.type == pm10) {
                        value6h10pm += item.value?.toFloat() ?: 0f
                        counter6h10pm++
                    } else {
                        value6h25pm += item.value?.toFloat() ?: 0f
                        counter6h25pm++
                    }
                }
                if (minus12h.isBefore(localDate)) {
                    if (item.type == pm10) {
                        value12h10pm += item.value?.toFloat() ?: 0f
                        counter12h10pm++
                    } else {
                        value12h25pm += item.value?.toFloat() ?: 0f
                        counter12h25pm++
                    }
                }

                if (item.type == pm10) {
                    value24h10pm += item.value?.toFloat() ?: 0f
                    counter24h10pm++
                } else {
                    value24h25pm += item.value?.toFloat() ?: 0f
                    counter24h25pm++
                }
            }

            println("iteration count: $counter")

            removePast24HourValues(now, id)

            val cachedSensorValueCounter = sensorValueCounterMap[id]
            val sensorValueCounter = SensorValueCounter(
                value6h10pm = (cachedSensorValueCounter?.value6h10pm ?: 0.0) + value6h10pm,
                value12h10pm = (cachedSensorValueCounter?.value12h10pm ?: 0.0) + value12h10pm,
                value24h10pm = (cachedSensorValueCounter?.value24h10pm ?: 0.0) + value24h10pm,
                value6h25pm = (cachedSensorValueCounter?.value6h25pm ?: 0.0) + value6h25pm,
                value12h25pm = (cachedSensorValueCounter?.value12h25pm ?: 0.0) + value12h25pm,
                value24h25pm = (cachedSensorValueCounter?.value24h25pm ?: 0.0) + value24h25pm,
                counter6h10pm = (cachedSensorValueCounter?.counter6h10pm ?: 0) + counter6h10pm,
                counter12h10pm = (cachedSensorValueCounter?.counter12h10pm ?: 0) + counter12h10pm,
                counter24h10pm = (cachedSensorValueCounter?.counter24h10pm ?: 0) + counter24h10pm,
                counter6h25pm = (cachedSensorValueCounter?.counter6h25pm ?: 0) + counter6h25pm,
                counter12h25pm = (cachedSensorValueCounter?.counter12h25pm ?: 0) + counter12h25pm,
                counter24h25pm = (cachedSensorValueCounter?.counter24h25pm ?: 0) + counter24h25pm,
            )

            sensorValueCounterMap[id] = sensorValueCounter
            localDateTimeMap[id] = now.toEpochSecond(ZoneOffset.UTC)
            cachedSensorValues = sensorValues

            return@withContext sensorValueCounter.toSensorDetailsEntity()
        }

    private fun removePast24HourValues(now: LocalDateTime, id: String) {
        val sensorValueCounter = sensorValueCounterMap[id] ?: return
        val minus24h = now.minus(24, ChronoUnit.HOURS)

        var value24h10pm = 0.0
        var value24h25pm = 0.0

        var counter24h10pm = 0
        var counter24h25pm = 0

        var counter = 0
        for (item in cachedSensorValues) {
            counter++
            val localDate = LocalDateTime.parse(
                item.stamp, DateTimeFormatter.ISO_ZONED_DATE_TIME
            )

            if (minus24h.isBefore(localDate)) break

            if (item.sensorId != id) continue

            if (item.type == pm10) {
                value24h10pm += item.value?.toFloat() ?: 0f
                counter24h10pm++
            } else {
                value24h25pm += item.value?.toFloat() ?: 0f
                counter24h25pm++
            }
        }

        println("iteration count for removing stale entries: $counter")

        sensorValueCounterMap[id] = sensorValueCounter.copy(
            value24h10pm = sensorValueCounter.value24h10pm - value24h10pm,
            value24h25pm = sensorValueCounter.value24h25pm - value24h25pm,
            counter24h10pm = sensorValueCounter.counter24h10pm - counter24h10pm,
            counter24h25pm = sensorValueCounter.counter24h25pm - counter24h25pm,
        )
    }
}

private data class SensorValueCounter(
    val value6h10pm: Double,
    val value12h10pm: Double,
    val value24h10pm: Double,
    val value6h25pm: Double,
    val value12h25pm: Double,
    val value24h25pm: Double,
    val counter6h10pm: Int,
    val counter12h10pm: Int,
    val counter24h10pm: Int,
    val counter6h25pm: Int,
    val counter12h25pm: Int,
    val counter24h25pm: Int
)

private fun SensorValueCounter.toSensorDetailsEntity(): SensorDetailsEntity {
    return SensorDetailsEntity(
        avg6h10PM =
        if (value6h10pm == 0.0 || counter6h10pm == 0) 0.0
        else value6h10pm / counter6h10pm,
        avg12h10PM =
        if (value12h10pm == 0.0 || counter12h10pm == 0) 0.0
        else value12h10pm / counter12h10pm,
        avg24h10PM =
        if (value24h10pm == 0.0 || counter24h10pm == 0) 0.0
        else value24h10pm / counter24h10pm,
        avg6h25PM =
        if (value6h25pm == 0.0 || counter6h25pm == 0) 0.0
        else value6h25pm / counter6h25pm,
        avg12h25PM =
        if (value12h25pm == 0.0 || counter12h25pm == 0) 0.0
        else value12h25pm / counter12h25pm,
        avg24h25PM =
        if (value24h25pm == 0.0 || counter24h25pm == 0) 0.0
        else value24h25pm / counter24h25pm,
    )
}