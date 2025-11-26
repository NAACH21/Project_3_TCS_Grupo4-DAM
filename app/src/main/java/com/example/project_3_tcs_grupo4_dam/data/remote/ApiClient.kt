package com.example.project_3_tcs_grupo4_dam.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "http://192.168.18.4:5260/" // TODO: Reemplaza esto con la URL base de tu API

    // 1. Crear el interceptor de logging
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // 2. Crear el cliente OkHttp y añadir el interceptor
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    // 3. Configurar Retrofit para que use el cliente OkHttp
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // Añadir el cliente aquí
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authApiService: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }

    val evaluacionApiService: EvaluacionApiService by lazy {
        retrofit.create(EvaluacionApiService::class.java)
    }

    val colaboradorApiService: ColaboradorApiService by lazy { // Added this
        retrofit.create(ColaboradorApiService::class.java)
    }
}
