package com.example.project_3_tcs_grupo4_dam.data.remote

import com.example.project_3_tcs_grupo4_dam.BuildConfig
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Configuración singleton de Retrofit
 * Base URL: http://10.0.2.2:5260/ (emulador Android apuntando a localhost del host)
 */
object RetrofitClient {

    // Base URL del backend .NET
    private const val BASE_URL = "http://10.62.245.60:5260/"

    /**
     * Cliente OkHttp con logging y timeouts configurados
     */
    private val okHttpClient: OkHttpClient by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    /**
     * Instancia de Retrofit configurada
     */
    private val retrofit: Retrofit by lazy {
        val gson = GsonBuilder()
            .setLenient()
            .create()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    /**
     * API Service de autenticación
     */
    val authApi: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }

    /**
     * API Service de colaboradores
     */
    val colaboradorApi: ColaboradorApiService by lazy {
        retrofit.create(ColaboradorApiService::class.java)
    }

    /**
     * API Service de catálogo
     */
    val catalogoApi: CatalogoApiService by lazy {
        retrofit.create(CatalogoApiService::class.java)
    }
}
