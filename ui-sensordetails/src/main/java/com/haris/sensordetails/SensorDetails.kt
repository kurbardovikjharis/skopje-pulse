package com.haris.sensordetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
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
import com.haris.resources.R

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
                            painter = painterResource(R.drawable.baseline_arrow_back_24),
                            contentDescription = stringResource(id = R.string.back)
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
                    SuccessView(
                        state = state,
                        onPM10Checked = { checked -> viewModel.onPM10Checked(checked) },
                        onPM25Checked = { checked -> viewModel.onPM25Checked(checked) }
                    )
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
private fun SuccessView(
    state: SensorDetailsViewState.Success,
    onPM10Checked: (Boolean) -> Unit,
    onPM25Checked: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Switch(checked = state.isPM10Checked, onCheckedChange = onPM10Checked)
                Text(text = stringResource(id = R.string.pm10))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Switch(checked = state.isPM25Checked, onCheckedChange = onPM25Checked)
                Text(text = stringResource(id = R.string.pm25))
            }
        }

        val avg6 = if (state.isPM10Checked) state.avg6h10PM else state.avg6h25PM
        val avg12 = if (state.isPM10Checked) state.avg12h10PM else state.avg12h25PM
        val avg24 = if (state.isPM10Checked) state.avg24h10PM else state.avg24h25PM

        Text(text = stringResource(id = R.string.avg_data, "6", avg6))
        Text(text = stringResource(id = R.string.avg_data, "12", avg12))
        Text(text = stringResource(id = R.string.avg_data, "24", avg24))
    }
}
