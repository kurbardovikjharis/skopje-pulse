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
import com.haris.sensors.data.SensorEntity

@Composable
fun Sensors(navigate: (String) -> Unit) {
    Sensors(viewModel = hiltViewModel(), navigate = navigate)
}

@Composable
internal fun Sensors(viewModel: SensorsViewModel, navigate: (String) -> Unit) {
    val state = viewModel.state.collectAsState().value

    Scaffold {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            when (state) {
                is SensorsViewState.Success -> {
                    SuccessView(sensors = state.sensors, navigate = navigate)
                }

                is SensorsViewState.Error -> {
                    Text(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        text = state.message
                    )
                }

                is SensorsViewState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(50.dp)
                            .align(Alignment.Center)
                    )
                }

                is SensorsViewState.Empty -> {}
            }
        }
    }
}

@Composable
internal fun SuccessView(sensors: List<SensorEntity>, navigate: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
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
internal fun Item(item: SensorEntity, navigate: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { navigate(item.sensorId) }
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            if (item.description.isNotEmpty()) {
                Text(text = item.description)
            }
        }
    }
}