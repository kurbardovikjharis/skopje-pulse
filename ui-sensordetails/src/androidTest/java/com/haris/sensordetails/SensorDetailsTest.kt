package com.haris.sensordetails

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

class SensorDetailsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun sensorDetailsEmptyTest() {
        composeTestRule.setContent {
            SuccessView(
                state = SensorDetailsViewState.Success(
                    isPM10Checked = false,
                    isPM25Checked = false,
                    avg6h = "0",
                    avg12h = "0",
                    avg24h = "0"
                ),
                onPM10Checked = {},
                onPM25Checked = {}
            )
        }

        val avg6 = "Average data for the past 6 hours: 0"
        val avg12 = "Average data for the past 12 hours: 0"
        val avg24 = "Average data for the past 24 hours: 0"

        composeTestRule.onNodeWithText("pm10").assertIsDisplayed()
        composeTestRule.onNodeWithText("pm25").assertIsDisplayed()
        composeTestRule.onNodeWithText(avg6).assertIsDisplayed()
        composeTestRule.onNodeWithText(avg12).assertIsDisplayed()
        composeTestRule.onNodeWithText(avg24).assertIsDisplayed()
    }

    @Test
    fun sensorDetails10pmCheckedTest() {
        composeTestRule.setContent {
            SuccessView(
                state = SensorDetailsViewState.Success(
                    isPM10Checked = true,
                    isPM25Checked = false,
                    avg6h = "1",
                    avg12h = "2",
                    avg24h = "3"
                ),
                onPM10Checked = {},
                onPM25Checked = {}
            )
        }

        val avg6 = "Average data for the past 6 hours: 1"
        val avg12 = "Average data for the past 12 hours: 2"
        val avg24 = "Average data for the past 24 hours: 3"

        composeTestRule.onNodeWithText("pm10").assertIsDisplayed()
        composeTestRule.onNodeWithText("pm25").assertIsDisplayed()
        composeTestRule.onNodeWithText(avg6).assertIsDisplayed()
        composeTestRule.onNodeWithText(avg12).assertIsDisplayed()
        composeTestRule.onNodeWithText(avg24).assertIsDisplayed()
    }
}