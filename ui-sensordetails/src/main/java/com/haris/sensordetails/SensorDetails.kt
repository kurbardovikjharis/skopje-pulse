package com.haris.sensordetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
                title = { Text(text = stringResource(id = R.string.sensor_details)) },
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
            HandleState(
                state = state,
                onPM10Checked = { checked -> viewModel.onPM10Checked(checked) },
                onPM25Checked = { checked -> viewModel.onPM25Checked(checked) }
            )
        }
    }
}

@Composable
internal fun HandleState(
    state: SensorDetailsViewState,
    onPM10Checked: (Boolean) -> Unit,
    onPM25Checked: (Boolean) -> Unit
) {
    when (state) {
        is SensorDetailsViewState.Success -> {
            Success(
                state = state,
                onPM10Checked = onPM10Checked,
                onPM25Checked = onPM25Checked
            )
        }

        is SensorDetailsViewState.Error -> {
            Error(
                state = state,
                onPM10Checked = onPM10Checked,
                onPM25Checked = onPM25Checked
            )
        }

        is SensorDetailsViewState.Loading -> {
            Loading(
                state = state,
                onPM10Checked = onPM10Checked,
                onPM25Checked = onPM25Checked
            )
        }

        is SensorDetailsViewState.Empty -> {}
    }
}

@Composable
private fun Success(
    state: SensorDetailsViewState.Success,
    onPM10Checked: (Boolean) -> Unit,
    onPM25Checked: (Boolean) -> Unit
) {
    DetailsData(
        data = state.data,
        onPM10Checked = onPM10Checked,
        onPM25Checked = onPM25Checked
    )
}

@Composable
private fun Error(
    state: SensorDetailsViewState.Error,
    onPM10Checked: (Boolean) -> Unit,
    onPM25Checked: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (state.data.isEmpty()) {
            Spacer(modifier = Modifier.height(32.dp))
        } else {
            DetailsData(
                data = state.data,
                onPM10Checked = onPM10Checked,
                onPM25Checked = onPM25Checked
            )
        }
        Text(
            modifier = Modifier.padding(16.dp),
            text = state.message
        )
    }
}

@Composable
private fun Loading(
    state: SensorDetailsViewState.Loading,
    onPM10Checked: (Boolean) -> Unit,
    onPM25Checked: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (state.data.isEmpty()) {
            Spacer(modifier = Modifier.height(32.dp))
        } else {
            DetailsData(
                data = state.data,
                onPM10Checked = onPM10Checked,
                onPM25Checked = onPM25Checked
            )
        }
        CircularProgressIndicator(
            modifier = Modifier.size(50.dp)
        )
    }
}

@Composable
private fun DetailsData(
    data: DetailsData,
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
                Switch(checked = data.isPM10Checked, onCheckedChange = onPM10Checked)
                Text(text = stringResource(id = R.string.pm10))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Switch(checked = data.isPM25Checked, onCheckedChange = onPM25Checked)
                Text(text = stringResource(id = R.string.pm25))
            }
        }

        Text(text = stringResource(id = R.string.avg_data, "6", data.avg6h))
        Text(text = stringResource(id = R.string.avg_data, "12", data.avg12h))
        Text(text = stringResource(id = R.string.avg_data, "24", data.avg24h))
    }
}
