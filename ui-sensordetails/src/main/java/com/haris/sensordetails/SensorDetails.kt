package com.haris.sensordetails

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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.haris.sensordetails.data.SensorDetailsEntity

@Composable
fun SensorDetails(navigateUp: () -> Unit) {
    SensorDetails(hiltViewModel(), navigateUp)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SensorDetails(viewModel: SensorDetailsViewModel, navigateUp: () -> Unit) {
    val state = viewModel.state.collectAsState().value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Sensor details") },
                navigationIcon = {
                    IconButton(onClick = navigateUp) {
                        Icon(
                            painter = painterResource(com.haris.resources.R.drawable.baseline_arrow_back_24),
                            contentDescription = stringResource(id = com.haris.resources.R.string.back)
                        )
                    }
                }
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            when (state) {
                is SensorDetailsViewState.Success -> {
                    SuccessView(sensors = state.sensors)
                }

                is SensorDetailsViewState.Error -> {
                    Text(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        text = state.message
                    )
                }

                is SensorDetailsViewState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(50.dp)
                            .align(Alignment.Center)
                    )
                }

                is SensorDetailsViewState.Empty -> {}
            }
        }
    }
}

@Composable
private fun SuccessView(sensors: List<SensorDetailsEntity>) {
    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
        items(sensors) {
            Item(item = it)
        }
    }
}

@Composable
private fun Item(item: SensorDetailsEntity) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            if (item.sensorId.isNotEmpty()) {
                Text(text = "sensorId: ${item.sensorId}")
            }
            if (item.stamp.isNotEmpty()) {
                Text(text = "description: ${item.stamp}")
            }
            if (item.type.isNotEmpty()) {
                Text(text = "comments: ${item.type}")
            }
            if (item.position.isNotEmpty()) {
                Text(text = "type: ${item.position}")
            }
            if (item.value.isNotEmpty()) {
                Text(text = "status: ${item.value}")
            }
        }
    }
}