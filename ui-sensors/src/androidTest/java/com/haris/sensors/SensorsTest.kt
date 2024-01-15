package com.haris.sensors

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.haris.sensors.data.SensorEntity
import org.junit.Rule
import org.junit.Test

class SensorsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loginTest() {
        composeTestRule.setContent {
            SuccessView(
                sensors = listOf(
                    SensorEntity(
                        sensorId = "1",
                        description = "description"
                    )
                )
            ) {

            }
        }
    }
}