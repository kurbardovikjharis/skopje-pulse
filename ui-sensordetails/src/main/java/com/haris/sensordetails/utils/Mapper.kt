package com.haris.sensordetails.utils

import com.haris.sensordetails.data.SensorDetailsDto
import com.haris.sensordetails.data.SensorDetailsEntity
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

private const val pm10 = "pm10"
private const val pm25 = "pm25"

/*
The first time, the mapper simply filters the data and creates an instance of SensorDetailsEntity.
The second time, once the data is cached, the mapper only iterates until the last update.

EG: if the user fetches new data after a minute and there are 3 new entries, we only iterate
through the 3 new entries, instead of around 25k.

removePast24HourValues() is also called since we have to remove the data which is stale - older
than 24h - this means additional 1 minute of data iteration (few entries).
*/
@Singleton
internal class Mapper @Inject constructor() {

    private var sensorValues = listOf<SensorDetailsDto>()
    internal var lastUpdate: LocalDateTime? = null
    private var sensorValueCounter: SensorValueCounter? = null

    fun map(sensorValues: List<SensorDetailsDto>): SensorDetailsEntity {
        val now = LocalDateTime.now()
        val minus6h = now.minus(6, ChronoUnit.HOURS)
        val minus12h = now.minus(12, ChronoUnit.HOURS)

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

        for (i in (sensorValues.lastIndex downTo 0)) {
            val item = sensorValues[i]
            if (item.type != pm10 && item.type != pm25) continue

            val localDate = LocalDateTime.parse(
                item.stamp, DateTimeFormatter.ISO_ZONED_DATE_TIME
            )

            if (lastUpdate?.isAfter(localDate) == true) break

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

        removePast24HourValues(now)

        val sensorValueCounter = SensorValueCounter(
            value6h10pm = (sensorValueCounter?.value6h10pm ?: 0.0) + value6h10pm,
            value12h10pm = (sensorValueCounter?.value12h10pm ?: 0.0) + value12h10pm,
            value24h10pm = (sensorValueCounter?.value24h10pm ?: 0.0) + value24h10pm,
            value6h25pm = (sensorValueCounter?.value6h25pm ?: 0.0) + value6h25pm,
            value12h25pm = (sensorValueCounter?.value12h25pm ?: 0.0) + value12h25pm,
            value24h25pm = (sensorValueCounter?.value24h25pm ?: 0.0) + value24h25pm,
            counter6h10pm = (sensorValueCounter?.counter6h10pm ?: 0) + counter6h10pm,
            counter12h10pm = (sensorValueCounter?.counter12h10pm ?: 0) + counter12h10pm,
            counter24h10pm = (sensorValueCounter?.counter24h10pm ?: 0) + counter24h10pm,
            counter6h25pm = (sensorValueCounter?.counter6h25pm ?: 0) + counter6h25pm,
            counter12h25pm = (sensorValueCounter?.counter12h25pm ?: 0) + counter12h25pm,
            counter24h25pm = (sensorValueCounter?.counter24h25pm ?: 0) + counter24h25pm,
        )

        this.sensorValueCounter = sensorValueCounter
        this.sensorValues = sensorValues
        this.lastUpdate = now

        return SensorDetailsEntity(
            avg6h10PM = sensorValueCounter.value6h10pm / sensorValueCounter.counter6h10pm,
            avg12h10PM = sensorValueCounter.value12h10pm / sensorValueCounter.counter12h10pm,
            avg24h10PM = sensorValueCounter.value24h10pm / sensorValueCounter.counter24h10pm,
            avg6h25PM = sensorValueCounter.value6h25pm / sensorValueCounter.counter6h25pm,
            avg12h25PM = sensorValueCounter.value12h25pm / sensorValueCounter.counter12h25pm,
            avg24h25PM = sensorValueCounter.value24h25pm / sensorValueCounter.counter24h25pm
        )
    }

    private fun removePast24HourValues(now: LocalDateTime) {
        val sensorValueCounter = sensorValueCounter ?: return
        val minus24h = now.minus(24, ChronoUnit.HOURS)

        var value24h10pm = 0.0
        var value24h25pm = 0.0

        var counter24h10pm = 0
        var counter24h25pm = 0

        for (item in sensorValues) {
            val localDate = LocalDateTime.parse(
                item.stamp, DateTimeFormatter.ISO_ZONED_DATE_TIME
            )

            if (minus24h.isBefore(localDate)) break

            if (item.type == pm10) {
                value24h10pm += item.value?.toFloat() ?: 0f
                counter24h10pm++
            } else {
                value24h25pm += item.value?.toFloat() ?: 0f
                counter24h25pm++
            }
        }

        this.sensorValueCounter = sensorValueCounter.copy(
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