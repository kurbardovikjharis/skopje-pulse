package com.haris.sensordetails

import com.haris.sensordetails.data.SensorDetailsDto
import com.haris.sensordetails.datasource.RemoteDataSource
import com.haris.sensordetails.repositories.SensorDetailsApi
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class RemoteDataSourceTest {
    @ExperimentalCoroutinesApi
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val api = mockk<SensorDetailsApi>()
    private val dataSource = RemoteDataSource(api)

    private val base = SensorDetailsDto(
        sensorId = "1",
        stamp = null,
        year = null,
        type = null,
        position = null,
        value = null
    )

    private val now = "2024-01-17T22:11:00+01:00"
    private val date6h = "2024-01-17T21:30:00+01:00"
    private val date12h = "2024-01-17T12:30:00+01:00"
    private val date24h = "2024-01-17T01:30:00+01:00"

    // make sure stamps are up to date
    private val sensorValues = listOf(
        base.copy(stamp = date6h, type = "pm10", value = "1"),
        base.copy(stamp = date6h, type = "pm10", value = "2"),
        base.copy(stamp = date6h, type = "pm10", value = "3"),
        base.copy(stamp = date12h, type = "pm10", value = "6"),
        base.copy(stamp = date12h, type = "pm10", value = "6"),
        base.copy(stamp = date12h, type = "pm10", value = "6"),
        base.copy(stamp = date24h, type = "pm10", value = "6"),
        base.copy(stamp = date24h, type = "pm10", value = "13"),
        base.copy(stamp = date24h, type = "pm10", value = "2"),

        base.copy(stamp = date6h, type = "pm25", value = "1"),
        base.copy(stamp = date6h, type = "pm25", value = "2"),
        base.copy(stamp = date6h, type = "pm25", value = "3"),
        base.copy(stamp = date12h, type = "pm25", value = "6"),
        base.copy(stamp = date12h, type = "pm25", value = "6"),
        base.copy(stamp = date12h, type = "pm25", value = "6"),
        base.copy(stamp = date24h, type = "pm25", value = "6"),
        base.copy(stamp = date24h, type = "pm25", value = "13"),
        base.copy(stamp = date24h, type = "pm25", value = "2"),
    )

    @Test
    fun testDataSource() = runTest {
        val values = dataSource.map(sensorValues, "1")

        assert(values.avg6h10PM == 2.0)
        assert(values.avg12h10PM == 4.0)
        assert(values.avg24h10PM == 5.0)

        assert(values.avg6h25PM == 2.0)
        assert(values.avg12h25PM == 4.0)
        assert(values.avg24h25PM == 5.0)
    }

    @Test
    fun testDataSourceCache() = runTest {
        dataSource.map(sensorValues, "1")

        val sensorValues = listOf(
            base.copy(stamp = now, type = "pm10", value = "6"),
        )

        val date = LocalDateTime.parse(
            date6h,
            DateTimeFormatter.ISO_ZONED_DATE_TIME
        )

        dataSource.localDateTimeMap["1"] = date.toEpochSecond(ZoneOffset.UTC)

        val values2 = dataSource.map(sensorValues, "1")

        assert(values2.avg6h10PM == 3.0)
    }
}