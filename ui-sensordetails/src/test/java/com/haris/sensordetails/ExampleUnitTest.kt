package com.haris.sensordetails

import com.haris.sensordetails.data.SensorDetailsDto
import com.haris.sensordetails.utils.Mapper
import io.mockk.coEvery
import io.mockk.every
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ExampleUnitTest {

    private val sensorValues = listOf(
        SensorDetailsDto(
            sensorId = null,
            stamp = "2024-01-14T16:46:30+01:00",
            year = null,
            type = "pm10",
            position = null,
            value = "1",
        ),
        SensorDetailsDto(
            sensorId = null,
            stamp = "2024-01-14T16:46:31+01:00",
            year = null,
            type = "pm10",
            position = null,
            value = "2",
        ),
        SensorDetailsDto(
            sensorId = null,
            stamp = "2024-01-14T16:46:32+01:00",
            year = null,
            type = "pm10",
            position = null,
            value = "3",
        ),
        SensorDetailsDto(
            sensorId = null,
            stamp = "2024-01-14T16:46:33+01:00",
            year = null,
            type = "pm25",
            position = null,
            value = "4",
        ),
        SensorDetailsDto(
            sensorId = null,
            stamp = "2024-01-14T16:46:34+01:00",
            year = null,
            type = "pm25",
            position = null,
            value = "5",
        ),
        SensorDetailsDto(
            sensorId = null,
            stamp = "2024-01-14T16:46:35+01:00",
            year = null,
            type = "pm25",
            position = null,
            value = "6",
        )
    )
    private val mapper = Mapper()

    @Test
    fun first_mapping() {
        val entity = mapper.map(sensorValues)
        assertEquals("2.0", entity.avg6h10PM)
        assertEquals("5.0", entity.avg6h25PM)
    }

    @Test
    fun second_mapping_same_values() {
        val entity = mapper.map(sensorValues)
        assertEquals("2.0", entity.avg6h10PM)
        assertEquals("5.0", entity.avg6h25PM)

        val entity2 = mapper.map(sensorValues)
        assertEquals("2.0", entity2.avg6h10PM)
        assertEquals("5.0", entity2.avg6h25PM)
    }
}