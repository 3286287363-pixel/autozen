package com.autozen.network.weather

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "zh_cn"
    ): WeatherResponse
}

data class WeatherResponse(
    val name: String,
    val main: Main,
    val weather: List<Weather>,
    val wind: Wind
) {
    data class Main(val temp: Float, val feels_like: Float, val humidity: Int)
    data class Weather(val description: String, val icon: String)
    data class Wind(val speed: Float)
}
