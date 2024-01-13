package com.haris.skopjepulse

import androidx.compose.animation.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.*
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation

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
        enterTransition = { defaultEnterTransition(initialState, targetState) },
        exitTransition = { defaultExitTransition(initialState, targetState) },
        popEnterTransition = { defaultPopEnterTransition() },
        popExitTransition = { defaultPopExitTransition() },
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
//        Home(navigateToCreate = {
//            val value = it ?: -1L
//            navController.navigate(LeafScreen.Create.createRoute(root, value))
//        })
    }
}

@ExperimentalAnimationApi
private fun NavGraphBuilder.addSensorDetails(
    navController: NavController,
    root: Screen
) {
    composable(
        route = LeafScreen.SensorDetails.createRoute(root),
//        arguments = listOf(
//            navArgument("id") { type = NavType.LongType },
//        ),
    ) {
//        SensorDetails(navigateUp = navController::navigateUp)
    }
}
