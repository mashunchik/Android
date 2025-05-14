package com.example.newday

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.newday.ui.theme.NewDayTheme


import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newday.models.ExchangeRateResponse
import com.example.newday.models.NewsResponse
import com.example.newday.models.WeatherResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var etCity: EditText
    private lateinit var btnGetWeather: Button
    private lateinit var tvResult: TextView

    private val apiKey = "dd112c3bcdea52d4cccb42d91b5dde11"

    private val currencyApiKey = "sHhGTjwHQ6chxqHsnSgp7e9oCifRXHYz"

    private val gNewsApiKey = "194e57008d160cf05073d1bdaeb8a220"



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etCity = findViewById(R.id.etCity)
        btnGetWeather = findViewById(R.id.btnGetWeather)
        tvResult = findViewById(R.id.tvResult)

        btnGetWeather.setOnClickListener {
            val city = etCity.text.toString()
            getWeather(city)
        }
        val btnGetRates: Button = findViewById(R.id.btnGetRates)
        val tvRates: TextView = findViewById(R.id.tvRates)
        val newsRecyclerView: RecyclerView = findViewById(R.id.newsRecyclerView)
        newsRecyclerView.layoutManager = LinearLayoutManager(this)
        loadNews(newsRecyclerView)

        btnGetRates.setOnClickListener {
            getExchangeRates(tvRates)
        }
    }
    private fun getExchangeRates(tvRates: TextView) {
        CurrencyRetrofitInstance.api.getRates("UAH", "EUR,USD,PLN", currencyApiKey)
            .enqueue(object : Callback<ExchangeRateResponse> {
                override fun onResponse(
                    call: Call<ExchangeRateResponse>,
                    response: Response<ExchangeRateResponse>
                ) {
                    if (response.isSuccessful) {
                        val rates = response.body()?.rates
                        val result = StringBuilder()
                        rates?.forEach { (currency, rate) ->
                            val converted = 1 / rate
                            result.append("1 $currency = %.2f UAH\n".format(converted))
                        }
                        tvRates.text = result.toString()
                    } else {
                        tvRates.text = "Помилка завантаження валют"
                    }
                }

                override fun onFailure(call: Call<ExchangeRateResponse>, t: Throwable) {
                    tvRates.text = "Помилка: ${t.message}"
                }
            })
    }

    private fun getWeather(city: String) {
        RetrofitInstance.api.getWeatherByCity(city, apiKey)
            .enqueue(object : Callback<WeatherResponse> {
                override fun onResponse(
                    call: Call<WeatherResponse>,
                    response: Response<WeatherResponse>
                ) {
                    if (response.isSuccessful) {
                        val weather = response.body()
                        val temp = weather?.main?.temp
                        val desc = weather?.weather?.firstOrNull()?.description
                        val name = weather?.name
                        tvResult.text = "$name: $temp°C, $desc"
                    } else {
                        tvResult.text = "Місто не знайдено або помилка."
                    }
                }

                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    tvResult.text = "Помилка: ${t.message}"
                }
            })
    }

    private fun loadNews(recyclerView: RecyclerView) {
        RetrofitNewsInstance.api.getNews("Ukraine", apiKey = gNewsApiKey)
            .enqueue(object : Callback<NewsResponse> {
                override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                    if (response.isSuccessful) {
                        val articles = response.body()?.articles ?: emptyList()
                        recyclerView.adapter = NewsAdapter(this@MainActivity, articles)
                    } else {
                        Toast.makeText(this@MainActivity, "Новини не завантажились", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                    Toast.makeText(this@MainActivity, "Помилка новин: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }


}