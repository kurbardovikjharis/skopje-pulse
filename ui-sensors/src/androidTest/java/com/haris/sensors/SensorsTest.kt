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
    fun sensorsSuccessTest() {
        composeTestRule.setContent {
            HandleState(
                state = SensorsViewState.Success(
                    sensors = listOf(
                        SensorEntity(
                            sensorId = "1",
                            description = "description"
                        )
                    )
                ),
                navigate = {},
                retry = {}
            )
        }

        composeTestRule.onNodeWithText("description").assertIsDisplayed()
    }

    @Test
    fun sensorsErrorTest() {
        composeTestRule.setContent {
            HandleState(
                state = SensorsViewState.Error(
                    message = "error",
                    sensors = null,
                ),
                navigate = {},
                retry = {}
            )
        }

        composeTestRule.onNodeWithText("error").assertIsDisplayed()
    }
}