package com.haris.skopjepulse

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
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

    data object SensorDetails : LeafScreen("sensor_details/{sensorId}") {
        fun createRoute(root: Screen, sensorId: String?): String {
            return "${root.route}/sensor_details/$sensorId"
        }
    }
}

@ExperimentalAnimationApi
@Composable
internal fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController,
) {
    NavHost(
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
            navArgument("sensorId") { type = NavType.StringType },
        ),
    ) {
        SensorDetails(navController::navigateUp)
    }
}

@ExperimentalAnimationApi
private fun AnimatedContentTransitionScope<*>.defaultEnterTransition(
    initial: NavBackStackEntry,
    target: NavBackStackEntry,
): EnterTransition {
    val initialNavGraph = initial.destination.hostNavGraph
    val targetNavGraph = target.destination.hostNavGraph
    // If we're crossing nav graphs (bottom navigation graphs), we crossfade
    if (initialNavGraph.id != targetNavGraph.id) {
        return fadeIn()
    }
    // Otherwise we're in the same nav graph, we can imply a direction
    return fadeIn() + slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start)
}

@ExperimentalAnimationApi
private fun AnimatedContentTransitionScope<*>.defaultExitTransition(
    initial: NavBackStackEntry,
    target: NavBackStackEntry,
): ExitTransition {
    val initialNavGraph = initial.destination.hostNavGraph
    val targetNavGraph = target.destination.hostNavGraph
    // If we're crossing nav graphs (bottom navigation graphs), we crossfade
    if (initialNavGraph.id != targetNavGraph.id) {
        return fadeOut()
    }
    // Otherwise we're in the same nav graph, we can imply a direction
    return fadeOut() + slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start)
}

private val NavDestination.hostNavGraph: NavGraph
    get() = hierarchy.first { it is NavGraph } as NavGraph

@ExperimentalAnimationApi
private fun AnimatedContentTransitionScope<*>.defaultPopEnterTransition(): EnterTransition {
    return fadeIn() + slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End)
}

@ExperimentalAnimationApi
private fun AnimatedContentTransitionScope<*>.defaultPopExitTransition(): ExitTransition {
    return fadeOut() + slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End)
}
