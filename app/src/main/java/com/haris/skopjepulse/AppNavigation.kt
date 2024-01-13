package com.haris.skopjepulse

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import com.haris.sensordetails.SensorDetails
import com.haris.sensors.Sensors

internal sealed class Screen(val route: String) {
    data object Sensors : Screen("sensors")
}

private sealed class LeafScreen(
    private val route: String,
) {
    fun createRoute(root: Screen) = "${root.route}/$route"

    data object Sensors : LeafScreen("sensors")

    data object SensorDetails : LeafScreen("sensor_details/{id}") {
        fun createRoute(root: Screen, id: Long?): String {
            return "${root.route}/create/$id"
        }
    }
}

@ExperimentalAnimationApi
@Composable
internal fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController,
) {
    AnimatedNavHost(
        navController = navController,
        startDestination = Screen.Sensors.route,
        modifier = modifier,
    ) {
        addSensorsTopLevel(navController)
    }
}

@ExperimentalAnimationApi
private fun NavGraphBuilder.addSensorsTopLevel(
    navController: NavController
) {
    navigation(
        route = Screen.Sensors.route,
        startDestination = LeafScreen.Sensors.createRoute(Screen.Sensors),
    ) {
        addSensors(navController, Screen.Sensors)
        addSensorDetails(navController, Screen.Sensors)
    }
}

@ExperimentalAnimationApi
private fun NavGraphBuilder.addSensors(
    navController: NavController,
    root: Screen,
) {
    composable(
        route = LeafScreen.Sensors.createRoute(root)
    ) {
        Sensors {
            val value = it
            navController.navigate(LeafScreen.SensorDetails.createRoute(root, value))
        }
    }
}

@ExperimentalAnimationApi
private fun NavGraphBuilder.addSensorDetails(
    navController: NavController,
    root: Screen
) {
    composable(
        route = LeafScreen.SensorDetails.createRoute(root),
        arguments = listOf(
            navArgument("id") { type = NavType.LongType },
        ),
    ) {
        SensorDetails(navController::navigateUp)
    }
}
