package com.example.newday


import com.example.newday.models.ExchangeRateResponse
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Call


interface CurrencyApiService {
    @GET("latest")
    fun getRates(
        @Query("base") base: String,
        @Query("symbols") symbols: String,
        @Query("apikey") apiKey: String
    ): Call<ExchangeRateResponse>
}