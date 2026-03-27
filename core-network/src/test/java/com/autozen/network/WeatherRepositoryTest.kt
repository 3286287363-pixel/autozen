package com.autozen.network

import com.autozen.network.weather.WeatherApi
import com.autozen.network.weather.WeatherRepository
import com.autozen.network.weather.WeatherResponse
import com.autozen.network.weather.WeatherState
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherRepositoryTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var api: WeatherApi
    private lateinit var repo: WeatherRepository

    private val fakeWeather = WeatherResponse(
        name = "Beijing",
        main = WeatherResponse.Main(temp = 25f, feels_like = 23f, humidity = 60),
        weather = listOf(WeatherResponse.Weather(description = "clear sky", icon = "01d")),
        wind = WeatherResponse.Wind(speed = 3.5f)
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        api = mockk()
        repo = WeatherRepository(api)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Loading`() {
        assertEquals(WeatherState.Loading, repo.state.value)
    }

    @Test
    fun `successful fetch emits Success`() = runTest {
        coEvery { api.getCurrentWeather(any(), any(), any(), any(), any()) } returns fakeWeather
        repo.fetchWeather(apiKey = "test_key")
        val state = repo.state.value
        assertTrue(state is WeatherState.Success)
        assertEquals("Beijing", (state as WeatherState.Success).data.name)
    }

    @Test
    fun `failed fetch emits Error`() = runTest {
        coEvery {
            api.getCurrentWeather(any(), any(), any(), any(), any())
        } throws RuntimeException("Network error")
        repo.fetchWeather(apiKey = "test_key")
        val state = repo.state.value
        assertTrue(state is WeatherState.Error)
        assertTrue((state as WeatherState.Error).message.contains("Network error"))
    }

    @Test
    fun `cache prevents double fetch within 5 minutes`() = runTest {
        coEvery { api.getCurrentWeather(any(), any(), any(), any(), any()) } returns fakeWeather
        repo.fetchWeather(apiKey = "test_key")
        repo.fetchWeather(apiKey = "test_key")  // second call within cache window
        coVerify(exactly = 1) { api.getCurrentWeather(any(), any(), any(), any(), any()) }
    }

    @Test
    fun `temperature is correctly mapped`() = runTest {
        coEvery { api.getCurrentWeather(any(), any(), any(), any(), any()) } returns fakeWeather
        repo.fetchWeather(apiKey = "test_key")
        val state = repo.state.value as WeatherState.Success
        assertEquals(25f, state.data.main.temp)
        assertEquals(60, state.data.main.humidity)
    }
}
