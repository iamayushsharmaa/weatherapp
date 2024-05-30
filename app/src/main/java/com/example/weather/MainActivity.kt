package com.example.weather

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.WindowDecorActionBar
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.view.WindowCompat
import com.example.weather.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


//d1ac654a7bcd17873e0ede31a924d0cc

class MainActivity() : AppCompatActivity(){

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        fetchWeatherData("Bhopal")
        SearchCity()


    }

    private fun SearchCity() {
        val searchView: SearchView = binding.searchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Handle changes in the query text
                return true
            }
        })
    }


    private fun fetchWeatherData(cityName:String) {
         val retrofit = Retrofit.Builder()
             .addConverterFactory(GsonConverterFactory.create())
             .baseUrl("https://api.openweathermap.org/data/2.5/")
             .build().create(ApiInterface::class.java)
         val response = retrofit.getWeatherData(cityName, "d1ac654a7bcd17873e0ede31a924d0cc",   "metric")
         response.enqueue(object : Callback<WeatherAppX> {
             override fun onResponse(call: Call<WeatherAppX>, response: Response<WeatherAppX>) {
                 val responseBody = response.body()
                 if (response.isSuccessful && responseBody != null) {
                     val temprature = responseBody.main.temp.toString()
                     val humidity = responseBody.main.humidity
                     val windSpeed = responseBody.wind.speed
                     val sunRise = responseBody.sys.sunrise.toLong()
                     val sunSet = responseBody.sys.sunset.toLong()
                     val seaLevel = responseBody.main.pressure
                     val condition = responseBody.weather.firstOrNull()?.main ?: "unknown"
                     val maxTemp = responseBody.main.temp_max
                     val minTemp = responseBody.main.temp_min


                     binding.todayTemp.text ="$temprature °C"
                     binding.txtsunny.text = condition
                     binding.condition.text = condition
                     binding.maxTemp.text = "$maxTemp°C"
                     binding.minTemp.text = "$minTemp°/"
                     binding.humidity.text = "$humidity %"
                     binding.wind.text = "$windSpeed m/s"
                     binding.sunset.text = "${time(sunSet)}"
                     binding.sunrise.text = "${time(sunRise)}"
                     binding.sea.text = "$seaLevel hPa"
                     binding.Day.text = dayName(System.currentTimeMillis())
                     binding.todayDate.text = date()
                     binding.cityName.text = "$cityName"

                     changeImageAccordingToWeatherCondition(condition)
                 }
             }

             override fun onFailure(call: Call<WeatherAppX>, t: Throwable) {
                 TODO("Not yet implemented")
             }

         })
     }


private fun changeImageAccordingToWeatherCondition(condition:String) {

        when(condition){
            "partly Clouds","Clouds","Mist","Overcast" -> {
                binding.root.setBackgroundResource(R.drawable.clouds1)
                binding.sunriselottie.setAnimation(R.raw.animelottie1)
            }
            "Clear Sky","Sunny","Clear"-> {
                binding.root.setBackgroundResource(R.drawable.clearclouds)
                binding.sunriselottie.setAnimation(R.raw.sunriselottie
                )
            }
            "Heavy Snow","Blizzed" -> {
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.sunriselottie.setAnimation(R.raw.snowlottie2)
            }
            "Light rain","Drizzle","Moderate Rain","Showers" ,"Heavy Rain"-> {
                binding.root.setBackgroundResource(R.drawable.rainbackground)
                binding.sunriselottie.setAnimation(R.raw.rainblacklottie)
            }
            "Fog","Smoke"->{
                binding.root.setBackgroundResource(R.drawable.clouds1)
                binding.sunriselottie.setAnimation(R.raw.foglottie)
            }
            "Light Snow","Moderate Snow" -> {
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.sunriselottie.setAnimation(R.raw.snowlottie)
            }
            else->{
            binding.root.setBackgroundResource(R.drawable.clearclouds2)
            binding.sunriselottie.setAnimation(R.raw.sunriseblacklottie)
            }
        }
        binding.sunriselottie.playAnimation()
    }

    private fun date(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))

    }
    private fun time(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date(timestamp * 1000)))

    }

    fun dayName(timestamp: Long):String{
             val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
          return sdf.format((Date()))
         }


}


