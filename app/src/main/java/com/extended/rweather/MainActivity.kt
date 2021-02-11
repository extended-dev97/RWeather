package com.extended.rweather

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.extended.rweather.models.one_call.OneCallModel
import com.extended.rweather.models.one_call.Weather
import com.extended.rweather.support.Common
import com.extended.rweather.support.RetrofitServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path

class MainActivity : AppCompatActivity() {
    lateinit var service: RetrofitServices
    lateinit var infoTextView: TextView
    lateinit var weatherImage: ImageView
    lateinit var tempTextView: TextView
    lateinit var precipitationTextView: TextView
    lateinit var precipitationTextView2: TextView
    lateinit var precipitationTextView3: TextView
    lateinit var feelsTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        infoTextView = findViewById(R.id.info)
        weatherImage = findViewById(R.id.weather_image)
        tempTextView = findViewById(R.id.temp_text_view)
        precipitationTextView = findViewById(R.id.precipitation_text_view_1)
        precipitationTextView2 = findViewById(R.id.precipitation_text_view_2)
        precipitationTextView3 = findViewById(R.id.precipitation_text_view_3)
        feelsTextView = findViewById(R.id.feels_text_view)

        service = Common.retrofitService

        getWeather(lat = 51.7132, lon = 54.8015)
        getOneCall(lat = 51.7132, lon = 54.8015)
    }

    private fun getWeather(lat: Double, lon: Double) {
        service.getWeather(lat = lat, lon = lon).enqueue(object : Callback<Weather> {
            override fun onFailure(call: Call<Weather>, t: Throwable) {
                println("error!!")
                println(t.message)
            }

            override fun onResponse(call: Call<Weather>, response: Response<Weather>) {
                println("success")
                println(response)
                infoTextView.text = response.body()?.name
            }
        })
    }

    private fun getOneCall(lat: Double, lon: Double) {
        service.getOneCall(lat = lat, lon = lon).enqueue(object : Callback<OneCallModel> {
            override fun onFailure(call: Call<OneCallModel>, t: Throwable) {
                println("error!!")
                println(t.message)
            }

            @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
            override fun onResponse(call: Call<OneCallModel>, response: Response<OneCallModel>) {
                println("success")
                val weather = response.body()

                val drawable: Drawable = resources.getDrawable(resources.getIdentifier(weather?.current?.weather?.get(0)?.weatherImage(), "drawable", packageName))
                weatherImage.setImageDrawable(drawable)

                tempTextView.text = weather?.current?.getTemperature(weather.current.temp).toString() + "°"
                precipitationTextView.text = weather?.current?.weather?.get(0)?.description
                precipitationTextView3.text = weather?.current?.notCensureDescription()?.description
                weather?.current?.notCensureDescription()?.color?.let { precipitationTextView3.setTextColor(it) }
                feelsTextView.text = "Ощущается как " + weather?.current?.getTemperature(weather.current.feelsLike).toString()
            }
        })
    }
}