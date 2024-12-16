package com.isga.quran.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://api.quran.com/api/v4/" // Replace with your API base URL

    val instance: AudioDownloadService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AudioDownloadService::class.java)
    }
}
