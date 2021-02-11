package com.extended.rweather.support

object Common {
    private const val BASE_URL = "https://api.openweathermap.org/"
    val retrofitService: RetrofitServices get() = RetrofitClient.getClient(BASE_URL).create(RetrofitServices::class.java)
}