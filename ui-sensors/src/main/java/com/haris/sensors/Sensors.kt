package com.haris.sensors

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.haris.sensors.data.SensorsDto

@Composable
fun Sensors(navigate: (String) -> Unit) {
    Sensors(viewModel = hiltViewModel(), navigate = navigate)
}

@Composable
private fun Sensors(viewModel: SensorsViewModel, navigate: (String) -> Unit) {
    val state = viewModel.state.collectAsState().value

    Scaffold {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(50.dp)
                        .align(Alignment.Center)
                )
            } else {
                SuccessView(sensors = state.sensors, navigate = navigate)
            }
        }
    }
}

@Composable
private fun SuccessView(sensors: List<SensorsDto>, navigate: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
        items(sensors) {
            Item(item = it, navigate = navigate)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Item(item: SensorsDto, navigate: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { navigate(item.sensorId) }
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            if (item.sensorId.isNotEmpty()) {
                Text(text = "sensorId: ${item.sensorId}")
            }
            if (item.description.isNotEmpty()) {
                Text(text = "description: ${item.description}")
            }
            if (item.comments.isNotEmpty()) {
                Text(text = "comments: ${item.comments}")
            }
            if (item.type.isNotEmpty()) {
                Text(text = "type: ${item.type}")
            }
            if (item.status.isNotEmpty()) {
                Text(text = "status: ${item.status}")
            }
            if (item.position.isNotEmpty()) {
                Text(text = "position: ${item.position}")
            }
        }
    }
}