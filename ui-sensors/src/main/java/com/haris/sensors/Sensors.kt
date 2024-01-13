package com.haris.sensors

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun Sensors(navigate: (Long) -> Unit) {
    Sensors(viewModel = hiltViewModel(), navigate = navigate)
}

@Composable
private fun Sensors(viewModel: SensorsViewModel, navigate: (Long) -> Unit) {
    val state = viewModel.state.collectAsState().value

    LazyColumn {
        items(state.sensors) {
            Text(it.description)
        }
    }
}