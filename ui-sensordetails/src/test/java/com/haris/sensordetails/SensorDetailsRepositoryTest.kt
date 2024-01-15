package com.haris.sensordetails

import com.haris.sensordetails.data.SensorDetailsDto
import com.haris.sensordetails.utils.DataSource
import org.junit.Test
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class SensorDetailsRepositoryTest {

    private val base = SensorDetailsDto(
        sensorId = "1",
        stamp = null,
        year = null,
        type = null,
        position = null,
        value = null
    )

    // make sure stamps are up to date
    private val sensorValues = listOf(
        base.copy(stamp = "2024-01-15T21:30:00+01:00", type = "pm10", value = "1"),
        base.copy(stamp = "2024-01-15T21:30:00+01:00", type = "pm10", value = "2"),
        base.copy(stamp = "2024-01-15T21:30:00+01:00", type = "pm10", value = "3"),
        base.copy(stamp = "2024-01-15T12:30:00+01:00", type = "pm10", value = "6"),
        base.copy(stamp = "2024-01-15T12:30:00+01:00", type = "pm10", value = "6"),
        base.copy(stamp = "2024-01-15T12:30:00+01:00", type = "pm10", value = "6"),
        base.copy(stamp = "2024-01-15T00:30:00+01:00", type = "pm10", value = "6"),
        base.copy(stamp = "2024-01-15T00:30:00+01:00", type = "pm10", value = "13"),
        base.copy(stamp = "2024-01-15T00:30:00+01:00", type = "pm10", value = "2"),

        base.copy(stamp = "2024-01-15T21:30:00+01:00", type = "pm25", value = "1"),
        base.copy(stamp = "2024-01-15T21:30:00+01:00", type = "pm25", value = "2"),
        base.copy(stamp = "2024-01-15T21:30:00+01:00", type = "pm25", value = "3"),
        base.copy(stamp = "2024-01-15T12:30:00+01:00", type = "pm25", value = "6"),
        base.copy(stamp = "2024-01-15T12:30:00+01:00", type = "pm25", value = "6"),
        base.copy(stamp = "2024-01-15T12:30:00+01:00", type = "pm25", value = "6"),
        base.copy(stamp = "2024-01-15T00:30:00+01:00", type = "pm25", value = "6"),
        base.copy(stamp = "2024-01-15T00:30:00+01:00", type = "pm25", value = "13"),
        base.copy(stamp = "2024-01-15T00:30:00+01:00", type = "pm25", value = "2"),
    )

    private val dataSource = DataSource()

    @Test
    fun testDataSource() {
        val values = dataSource.map(sensorValues, "1")

        assert(values.avg6h10PM == 2.0)
        assert(values.avg12h10PM == 4.0)
        assert(values.avg24h10PM == 5.0)

        assert(values.avg6h25PM == 2.0)
        assert(values.avg12h25PM == 4.0)
        assert(values.avg24h25PM == 5.0)
    }

    @Test
    fun testDataSourceCache() {
        dataSource.map(sensorValues, "1")

        val sensorValues = listOf(
            base.copy(stamp = "2024-01-15T23:51:00+01:00", type = "pm10", value = "6"),
        )

        // modify lastUpdate to be equal to 'sensorValues' latest item
        dataSource.lastUpdate = LocalDateTime.parse(
            "2024-01-15T21:30:00+01:00",
            DateTimeFormatter.ISO_ZONED_DATE_TIME
        )

        val values2 = dataSource.map(sensorValues, "1")

        assert(values2.avg6h10PM == 3.0)
    }
}