package com.example.newday.models

data class ExchangeRateResponse(
    val base: String? = null,
    val date: String? = null,
    val rates: Map<String, Double>? = null
)