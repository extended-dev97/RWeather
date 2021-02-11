package com.extended.rweather.support

object Common {
    // Сервис HTTP запросов
    val retrofitService: RetrofitServices get() = RetrofitClient.retrofitClient.create(RetrofitServices::class.java)
}