package com.extended.rweather

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.extended.rweather.models.one_call.OneCallModel
import com.extended.rweather.models.one_call.Weather
import com.extended.rweather.support.Common
import com.extended.rweather.support.RetrofitServices
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    companion object {
        private const val PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 100
    }

    // Сервис с запросами
    private lateinit var service: RetrofitServices

    // Компоненты UI
    private lateinit var infoTextView: TextView
    private lateinit var weatherImage: ImageView
    private lateinit var tempTextView: TextView
    private lateinit var precipitationTextView: TextView
    private lateinit var precipitationTextView2: TextView
    private lateinit var precipitationTextView3: TextView
    private lateinit var feelsTextView: TextView
    private lateinit var parentLayout: View

    // Менеджер локации
    private var locationManager : LocationManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        parentLayout = findViewById(android.R.id.content)

        infoTextView = findViewById(R.id.info)
        weatherImage = findViewById(R.id.weather_image)
        tempTextView = findViewById(R.id.temp_text_view)
        precipitationTextView = findViewById(R.id.precipitation_text_view_1)
        precipitationTextView2 = findViewById(R.id.precipitation_text_view_2)
        precipitationTextView3 = findViewById(R.id.precipitation_text_view_3)
        feelsTextView = findViewById(R.id.feels_text_view)

        // Инициализация сервиса
        service = Common.retrofitService

        getLocation()
    }

    // Срабатывает при запросе разрешений
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_ACCESS_FINE_LOCATION) {
            when (grantResults[0]) {
                PackageManager.PERMISSION_GRANTED -> getLocation()
                PackageManager.PERMISSION_DENIED -> parentLayout.snack("Не выданы разрешения")
            }
        }
    }

    // Получить локацию
    private fun getLocation() {
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?

        // Если разрешения не выданы
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_REQUEST_ACCESS_FINE_LOCATION
            )
            Log.d("Permission Error", "Not permissions")
            return
        } else { // Если выданы
            // Пробуем запросить локацию
            try {
                locationManager?.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    300000L,
                    0f,
                    locationListener
                )
            } catch (ex: SecurityException) { // Ошибка запроса локации
                parentLayout.snack(ex.message.toString())
                Log.d("Location Error", "Security Exception, no location available")
            }
        }
    }

    // Слушатель изменения локации и ее статуса
    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            Log.d("Location Data", "${location.latitude}, ${location.longitude}")
            parentLayout.snack("Location updated!!: \n${location.latitude}, ${location.longitude}")
            getWeather(lat = location.latitude, lon = location.longitude)
            getOneCall(lat = location.latitude, lon = location.longitude)
        }
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    // Запрос погоды (чисто для названия местности)
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

    // Запрос "Все за один вызов" (погода на 10 дней, на часы, и так далее)
    private fun getOneCall(lat: Double, lon: Double) {
        service.getOneCall(lat = lat, lon = lon).enqueue(object : Callback<OneCallModel> {
            override fun onFailure(call: Call<OneCallModel>, t: Throwable) {
                println("error!!")
                println(t.message)
            }

            @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
            override fun onResponse(call: Call<OneCallModel>, response: Response<OneCallModel>) {
                println("success")
                val weather: OneCallModel = response.body() ?: return

                weather.current.weather[0].weatherImage().let {
                    weatherImage.setImageDrawable(ContextCompat.getDrawable(applicationContext, it))
                }

                tempTextView.text = weather.current.getTemperature(weather.current.temp).toString() + "°"
                precipitationTextView.text = weather.current.weather[0].description.capitalizeFirstLetter()
                precipitationTextView3.text = weather.current.notCensureDescription.description
                weather.current.notCensureDescription.color.let {
                    precipitationTextView3.setTextColor(it)
                }
                if (weather.current.getTemperature(weather.current.feelsLike) > 0) {
                    feelsTextView.text =
                        "Ощущается как +" + weather.current.getTemperature(weather.current.feelsLike).toString()
                } else {
                    feelsTextView.text =
                        "Ощущается как " + weather.current.getTemperature(weather.current.feelsLike).toString()
                }
            }
        })
    }

    // Показать нижний снэк
    fun View.snack(message: String, duration: Int = Snackbar.LENGTH_LONG) {
        Snackbar.make(this, message, duration).show()
    }

    // Функция - расширение. Заменяет первый символ строки на большой ("в" на "В")
    fun String.capitalizeFirstLetter(): String {
        return replaceFirst(this[0], this[0].toUpperCase())
    }
}