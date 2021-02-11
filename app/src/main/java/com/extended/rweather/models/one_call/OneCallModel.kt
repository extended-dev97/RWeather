package com.extended.rweather.models.one_call

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Range
import com.extended.rweather.R
import com.google.gson.annotations.SerializedName
import java.util.*

data class NonCensureDescription(val description: String, val color: Int)

// Тут все модели
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

enum class DayTime { morning, noon, afternoon, evening, night }

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

        val notCensureDescription: NonCensureDescription get() {
                when {
                    Range(-100, -35).contains(getTemperature(feelsLike)) -> {
                            return NonCensureDescription("Ебучая морозилка", Color.parseColor("#0D9DE3"))
                    }
                    Range(-35, -20).contains(getTemperature(feelsLike)) -> {
                            return NonCensureDescription("Холодно пиздец", Color.parseColor("#0D9DE3"))
                    }
                    Range(-20, -5).contains(getTemperature(feelsLike)) -> {
                            return NonCensureDescription("Немного морозит", Color.parseColor("#6ac6f9"))
                    }
                    Range(-5, 15).contains(getTemperature(feelsLike)) -> {
                            return NonCensureDescription("Заебись прохладно", Color.parseColor("#6ac6f9"))
                    }
                    Range(15, 25).contains(getTemperature(feelsLike)) -> {
                            return NonCensureDescription("Заебись тепло", Color.parseColor("#9ACD32"))
                    }
                    Range(25, 40).contains(getTemperature(feelsLike)) -> {
                            return NonCensureDescription("Жарко пиздеца", Color.parseColor("#ffb748"))
                    }
                    Range(40, 100).contains(getTemperature(feelsLike)) -> {
                            return NonCensureDescription("Пекло ебучее", Color.parseColor("#f0341f"))
                    }
                    else -> {
                            return NonCensureDescription("Хуй знает что щас", Color.parseColor("#0D9DE3"))
                    }
                }
        }
}

data class WeatherElement(
        val id: Long,
        val main: String,
        val description: String,
        val icon: String
) {
        fun weatherImage(): Int {
                when (main) {
                        "Clouds" -> {
                                return when {
                                        description.contains("пасмурно") -> { R.drawable.nt_cloudy }
                                        description.contains("облачно с прояснениями") -> { R.drawable.partlysunny }
                                        else -> { R.drawable.partlycloudy }
                                }
                        }
                        "Snow" -> {
                                return if (description.contains("небольшой снег")) { R.drawable.nt_chancesnow }
                                else { R.drawable.snow }
                        }
                        "Clear" -> {
                                return if (state() == DayTime.night) { R.drawable.nt_clear }
                                else { R.drawable.clear }
                        }
                        "Mist" -> {
                                return if (state() == DayTime.night) { R.drawable.nt_fog }
                                else { R.drawable.fog }
                        }
                        "Rain" -> {
                                return when {
                                    description.contains("небольшой дождь") -> { R.drawable.nt_chancerain }
                                    description.contains("снег с дождём") -> { R.drawable.sleet }
                                    else -> { R.drawable.rain }
                                }
                        }
                        else -> { return R.drawable.unknown }
                }
        }
}

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

data class FeelsLike(
        val day: Double,
        val night: Double,
        val eve: Double,
        val morn: Double
) { fun getTemperature(doubleTemp: Double): Int { return (doubleTemp - 273.15).toInt() } }

data class Temp(
        val day: Double,
        val min: Double,
        val max: Double,
        val night: Double,
        val eve: Double,
        val morn: Double
) { fun getTemperature(doubleTemp: Double): Int { return (doubleTemp - 273.15).toInt() } }

data class Minutely(
        val dt: Double,
        val precipitation: Double
)