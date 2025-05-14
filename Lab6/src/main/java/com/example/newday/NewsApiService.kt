package com.example.newday

import retrofit2.Call
import com.example.newday.models.NewsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {
    @GET("search")
    fun getNews(
        @Query("q") query: String,
        @Query("lang") lang: String = "uk",
        @Query("max") max: Int = 3,
        @Query("apikey") apiKey: String
    ): Call<NewsResponse>
}