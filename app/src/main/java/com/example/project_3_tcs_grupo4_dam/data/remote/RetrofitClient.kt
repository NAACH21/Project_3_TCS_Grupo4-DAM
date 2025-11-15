package com.example.project_3_tcs_grupo4_dam.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // --- CORRECCIÓN APLICADA ---
    // Se usa la IP de tu Wi-Fi (10.72.90.84) obtenida del comando 'ipconfig'
    // Tu teléfono y tu PC deben estar en la MISMA red Wi-Fi.
    private const val BASE_URL = "http://10.72.90.84:5260"

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