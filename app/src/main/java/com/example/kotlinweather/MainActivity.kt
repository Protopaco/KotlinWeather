package com.example.kotlinweather

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlinweather.openweatherresponse.OpenWeatherResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getWeatherData()
    }

    fun updateUI(responseBody: OpenWeatherResponse?) {
        if (responseBody != null) {
            val main = responseBody.main
            val sys = responseBody.sys
            val wind = responseBody.wind
            val weather = responseBody.weather
            val dt = responseBody.dt
            val location = responseBody.name

            findViewById<TextView>(R.id.temperature).text = "${main.temp.toInt()}°F"
            findViewById<TextView>(R.id.temp_min).text = "Min Temp: ${main.temp_min.toInt()}°F"
            findViewById<TextView>(R.id.temp_max).text = "Max Temp: ${main.temp_max.toInt()}°F"
            findViewById<TextView>(R.id.humidity).text = main.humidity.toString()
            findViewById<TextView>(R.id.wind).text = wind.speed.toString()
            findViewById<TextView>(R.id.pressure).text = main.pressure.toString()

            findViewById<TextView>(R.id.sunrise).text = formatTime(sys.sunrise)
            findViewById<TextView>(R.id.sunset).text = formatTime(sys.sunset)
            findViewById<TextView>(R.id.address).text = "$location, ${sys.country}"
            findViewById<TextView>(R.id.updated_at).text = "updated at: ${formatDate(dt)}"
            findViewById<TextView>(R.id.status).text =
                weather[0].description.replaceFirstChar(Char::titlecase)

            findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
            findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.VISIBLE
        }
    }

    private fun getWeatherData() {
        val apiKey: String = BuildConfig.API_KEY
        val weatherUrl: String = BuildConfig.WEATHER_URL

        val retrofitBuilder = Retrofit.Builder()
            .baseUrl(weatherUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)

        val retrofitData = retrofitBuilder.getWeatherData("portland,or,us", apiKey, "imperial")

        retrofitData.enqueue(object : Callback<OpenWeatherResponse?> {
            override fun onResponse(
                call: Call<OpenWeatherResponse?>,
                response: Response<OpenWeatherResponse?>
            ) {
                findViewById<ProgressBar>(R.id.loader).visibility = View.VISIBLE
                findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.GONE

                val responseBody: OpenWeatherResponse? = response.body()
                updateUI(responseBody)
            }

            override fun onFailure(call: Call<OpenWeatherResponse?>, t: Throwable) {
                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<TextView>(R.id.errortext).visibility = View.VISIBLE
            }
        })


    }

    fun formatDate(date: Int): String {
        return SimpleDateFormat(
            "MM/dd/yyyy hh:mm a",
            Locale.ENGLISH
        ).format(Date((date.toLong() * 1000)))
    }

    fun formatTime(time: Int): String {
        return SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(time.toLong() * 1000))
    }
}

