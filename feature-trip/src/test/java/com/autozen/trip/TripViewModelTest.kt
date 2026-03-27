package com.autozen.trip

import app.cash.turbine.test
import com.autozen.data.trip.TripDao
import com.autozen.data.trip.TripEntity
import com.autozen.obd.ObdDataSource
import com.autozen.obd.model.VehicleData
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TripViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var tripDao: TripDao
    private lateinit var obdDataSource: ObdDataSource

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        tripDao = mockk(relaxed = true)
        obdDataSource = mockk()
        every { tripDao.getAllTrips() } returns flowOf(emptyList())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has empty trips and not recording`() = runTest {
        every { obdDataSource.dataFlow } returns flowOf(VehicleData())
        val vm = TripViewModel(tripDao, obdDataSource)
        assertFalse(vm.isRecording.value)
        assertTrue(vm.trips.value.isEmpty())
    }

    @Test
    fun `engine off does not start recording`() = runTest {
        every { obdDataSource.dataFlow } returns flowOf(
            VehicleData(isEngineOn = false, speedKmh = 0f)
        )
        val vm = TripViewModel(tripDao, obdDataSource)
        assertFalse(vm.isRecording.value)
    }

    @Test
    fun `engine on with speed starts recording`() = runTest {
        every { obdDataSource.dataFlow } returns flowOf(
            VehicleData(isEngineOn = true, speedKmh = 50f)
        )
        val vm = TripViewModel(tripDao, obdDataSource)
        assertTrue(vm.isRecording.value)
    }

    @Test
    fun `addSampleTrip calls dao insertTrip`() = runTest {
        every { obdDataSource.dataFlow } returns flowOf(VehicleData())
        val vm = TripViewModel(tripDao, obdDataSource)
        vm.addSampleTrip()
        coVerify { tripDao.insertTrip(any()) }
    }

    @Test
    fun `deleteTrip calls dao deleteTrip`() = runTest {
        every { obdDataSource.dataFlow } returns flowOf(VehicleData())
        val vm = TripViewModel(tripDao, obdDataSource)
        val trip = TripEntity(
            id = 1L, startTime = 0L, endTime = 1000L,
            distanceKm = 1f, avgSpeedKmh = 30f, avgFuelConsumption = 7f
        )
        vm.deleteTrip(trip)
        coVerify { tripDao.deleteTrip(trip) }
    }

    @Test
    fun `isRecording flow emits false initially`() = runTest {
        every { obdDataSource.dataFlow } returns flowOf(VehicleData())
        val vm = TripViewModel(tripDao, obdDataSource)
        vm.isRecording.test {
            assertFalse(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
