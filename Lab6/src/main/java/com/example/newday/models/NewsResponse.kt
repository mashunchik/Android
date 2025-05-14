package com.example.newday.models

data class NewsResponse(
    val articles: List<Article>
)

data class Article(
    val title: String,
    val url: String
)