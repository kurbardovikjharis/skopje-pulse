package com.haris.sensordetails

import com.haris.data.network.NetworkResult
import com.haris.sensordetails.interactors.GetSensorDetailsInteractor
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

class SensorsViewModelTest {
    @ExperimentalCoroutinesApi
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: SensorDetailsViewModel

    private val mockedGetSensorDetailsInteractor = mockk<GetSensorDetailsInteractor>()

    @Before
    fun setup() {
        every { runBlocking { mockedGetSensorDetailsInteractor() } } returns Unit
        every { mockedGetSensorDetailsInteractor.flow } answers { MutableStateFlow(NetworkResult.None()) }

        viewModel = SensorDetailsViewModel(mockedGetSensorDetailsInteractor)
    }

    @Test
    fun `UiState - When view model is created the ui state is correct`() =
        runTest {
            val state = viewModel.state.value
            assert(state is SensorDetailsViewState.Empty)
        }
}

@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    private val testDispatcher: TestDispatcher = StandardTestDispatcher()
) : TestWatcher() {

    override fun starting(description: Description?) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description?) {
        Dispatchers.resetMain()
    }
}
