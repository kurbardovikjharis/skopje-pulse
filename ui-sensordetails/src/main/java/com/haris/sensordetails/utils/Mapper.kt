package com.haris.sensordetails.utils

import com.haris.sensordetails.data.SensorDetailsDto
import com.haris.sensordetails.data.SensorDetailsEntity
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

private val now = LocalDateTime.now()
private val minus6h = now.minus(6, ChronoUnit.HOURS)
private val minus12h = now.minus(12, ChronoUnit.HOURS)

fun List<SensorDetailsDto>.toSensorEntityList(): SensorDetailsEntity {
    var totalValue = 0f
    var minus6hValue = 0f
    var minus12hValue = 0f

    var minus6Counter = 0
    var minus12Counter = 0

    for (item in this) {
        val localDate = LocalDateTime.parse(
            item.stamp, DateTimeFormatter.ISO_ZONED_DATE_TIME
        )

        totalValue += item.value?.toFloat() ?: 0f
        if (minus6h.isBefore(localDate)) {
            minus6hValue += item.value?.toFloat() ?: 0f
            minus6Counter++
        }
        if (minus12h.isBefore(localDate)) {
            minus12hValue += item.value?.toFloat() ?: 0f
            minus12Counter++
        }
    }

    return SensorDetailsEntity(
        average6h = (minus6hValue / minus6Counter).toString(),
        average12h = (minus12hValue / minus12Counter).toString(),
        average24h = (totalValue / this.size).toString()
    )
}
