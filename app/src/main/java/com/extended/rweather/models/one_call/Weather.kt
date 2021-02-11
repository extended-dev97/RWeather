package com.extended.rweather.models.one_call

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.*

@Serializable
data class Weather (
    val coord: Coord,
    val weather: List<WeatherElement>,
    val base: String,
    val main: Main,
    val visibility: Double,
    val wind: Wind,
    val snow: Snow,
    val clouds: Clouds,
    val dt: Double,
    val sys: Sys,
    val timezone: Double,
    val id: Double,
    val name: String,
    val cod: Double
)

@Serializable
data class Clouds (
    val all: Double
)

@Serializable
data class Coord (
    val lon: Double,
    val lat: Double
)

@Serializable
data class Main (
    val temp: Double,

    @SerializedName("feels_like") val feelsLike: Double,

    @SerializedName("temp_min") val tempMin: Double,

    @SerializedName("temp_max") val tempMax: Double,

    val pressure: Double,
    val humidity: Double
) { fun getTemperature(doubleTemp: Double): Int { return (doubleTemp - 273.15).toInt() } }

@Serializable
data class Snow (
    @SerializedName("1h") val the1H: Double
)

@Serializable
data class Sys (
    val type: Double,
    val id: Double,
    val country: String,
    val sunrise: Double,
    val sunset: Double
)

@Serializable
data class Wind (
    val speed: Double,
    val deg: Double
)