package com.haris.skopjepulse

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Main() {
    val navController = rememberNavController()

    AppNavigation(
        modifier = Modifier.fillMaxHeight(),
        navController = navController,
    )
}