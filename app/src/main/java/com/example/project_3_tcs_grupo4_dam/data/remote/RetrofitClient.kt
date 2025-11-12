package com.example.project_3_tcs_grupo4_dam.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // RECUERDA: 10.0.2.2 para emulador, o tu IP (ej. 192.168.1.10) para teléfono
    private const val BASE_URL = "http://10.0.2.2:5071/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}