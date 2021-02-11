package com.extended.rweather.models.one_call

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.Range
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.*
import java.util.*

data class NonCensureDescription(val description: String, val color: Int)

@SuppressLint("SimpleDateFormat")
fun state(): DayTime {
        val calendar = Calendar.getInstance()
        calendar.time = Date()

        return when (calendar[Calendar.HOUR_OF_DAY]) {
                in 6..11 -> DayTime.morning
                12 -> DayTime.noon
                in 13..17 -> DayTime.afternoon
                in 18..21 -> DayTime.evening
                else -> DayTime.night
        }
}

enum class DayTime {
        morning, noon, afternoon, evening, night
}

@Serializable
data class OneCallModel(
        val lat: Double,
        val lon: Double,
        val timezone: String,

        @SerializedName("timezone_offset") val timezoneOffset: Long,

        val current: Current,
        val minutely: List<Minutely>,
        val hourly: List<Current>,
        val daily: List<Daily>
)

@Serializable
data class Current(
        val dt: Long,
        val sunrise: Long? = null,
        val sunset: Long? = null,
        val temp: Double,

        @SerializedName("feels_like") val feelsLike: Double,

        val pressure: Long,
        val humidity: Long,

        @SerializedName("dew_point") val dewPoint: Double,

        val uvi: Double,
        val clouds: Long,
        val visibility: Long,

        @SerializedName("wind_speed") val windSpeed: Double,

        @SerializedName("wind_deg") val windDeg: Long,

        val weather: List<WeatherElement>,
        val pop: Double? = null
) {
        fun getTemperature(doubleTemp: Double): Int { return (doubleTemp - 273.15).toInt() }

        fun notCensureDescription(): NonCensureDescription {
                if (Range(-100, -25).contains(getTemperature(feelsLike))) {
                        return NonCensureDescription("Ебучая морозилка", Color.parseColor("#0D9DE3"))
                } else if (Range(-25, -10).contains(getTemperature(feelsLike))) {
                        return NonCensureDescription("Холодно пиздец", Color.parseColor("#0D9DE3"))
                } else if (Range(-10, 0).contains(getTemperature(feelsLike))) {
                        return NonCensureDescription("Немного морозит", Color.parseColor("#6ac6f9"))
                } else if (Range(0, 15).contains(getTemperature(feelsLike))) {
                        return NonCensureDescription("Заебись прохладно", Color.parseColor("#6ac6f9"))
                } else if (Range(15, 25).contains(getTemperature(feelsLike))) {
                        return NonCensureDescription("Заебись тепло", Color.parseColor("#9ACD32"))
                } else if (Range(25, 40).contains(getTemperature(feelsLike))) {
                        return NonCensureDescription("Жарко пиздеца", Color.parseColor("#ffb748"))
                } else if (Range(40, 100).contains(getTemperature(feelsLike))) {
                        return NonCensureDescription("Пекло ебучее", Color.parseColor("#f0341f"))
                } else {
                        return NonCensureDescription("Хуй знает что щас", Color.parseColor("#0D9DE3"))
                }
        }
}

@Serializable
data class WeatherElement(
        val id: Long,
        val main: String,
        val description: String,
        val icon: String
) {
        fun weatherImage(): String {
                when (main) {
                        "Clouds" -> {
                                if (description.contains("пасмурно")) {
                                        return "nt_cloudy"
                                } else if (description.contains("облачно с прояснениями")) {
                                        return "partlysunny"
                                } else {
                                        return "partlycloudy"
                                }
                        }
                        "Snow" -> {
                                if (description.contains("небольшой снег")) {
                                        return "nt_chancesnow"
                                } else {
                                        return "snow"
                                }
                        }
                        "Clear" -> {
                                if (state() == DayTime.night) {
                                        return "nt_clear"
                                } else {
                                        return "clear"
                                }
                        }
                        "Mist" -> {
                                if (state() == DayTime.night) {
                                        return "nt_fog"
                                } else {
                                        return "fog"
                                }
                        }
                        "Rain" -> {
                                if (description.contains("небольшой дождь")) {
                                        return "nt_chancerain"
                                } else if (description.contains("снег с дождём")) {
                                        return "sleet"
                                } else {
                                        return "rain"
                                }
                        }
                        else -> { return "unknown" }
                }
        }
}

@Serializable
data class Daily(
        val dt: Long,
        val sunrise: Long,
        val sunset: Long,
        val temp: Temp,

        @SerializedName("feels_like") val feelsLike: FeelsLike,

        val pressure: Long,
        val humidity: Long,

        @SerializedName("dew_point") val dewPoint: Double,

        @SerializedName("wind_speed") val windSpeed: Double,

        @SerializedName("wind_deg") val windDeg: Long,

        val weather: List<WeatherElement>,
        val clouds: Long,
        val pop: Double,
        val uvi: Double,
        val rain: Double? = null,
        val snow: Double? = null
)

@Serializable
data class FeelsLike(
        val day: Double,
        val night: Double,
        val eve: Double,
        val morn: Double
) { fun getTemperature(doubleTemp: Double): Int { return (doubleTemp - 273.15).toInt() } }

@Serializable
data class Temp(
        val day: Double,
        val min: Double,
        val max: Double,
        val night: Double,
        val eve: Double,
        val morn: Double
) { fun getTemperature(doubleTemp: Double): Int { return (doubleTemp - 273.15).toInt() } }

@Serializable
data class Minutely(
        val dt: Double,
        val precipitation: Double
)