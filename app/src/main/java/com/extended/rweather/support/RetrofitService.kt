package com.extended.rweather.support

import com.extended.rweather.models.one_call.OneCallModel
import com.extended.rweather.models.one_call.Weather
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitServices {
    // Запрос "Все за вызов" -> "data/2.5/onecall"
    @GET("data/2.5/onecall")
    fun getOneCall(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appId") appId: String = "584e606af8ea8f46ae432da5f8acf817", // мой appId
        @Query("lang") lang: String = "ru"
    ): Call<OneCallModel>

    // Запрос погоды -> "data/2.5/weather"
    @GET("data/2.5/weather")
    fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appId") appId: String = "584e606af8ea8f46ae432da5f8acf817", // мой appId
        @Query("lang") lang: String = "ru"
    ): Call<Weather>
}