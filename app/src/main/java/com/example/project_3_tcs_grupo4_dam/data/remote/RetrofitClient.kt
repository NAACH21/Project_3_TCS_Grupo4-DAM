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
 * Asegúrate de que la IP sea accesible desde el dispositivo (mismo Wi-Fi o 10.0.2.2 para emulador)
 */
object RetrofitClient {

    // IP Actualizada: Asegúrate de que esta sea la IP de tu PC en la red local
    private const val BASE_URL = "http://192.168.18.94:5260/"
 
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
     * API Service de skills de colaborador
     */
    val colaboradorSkillsApi: ColaboradorSkillsApiService by lazy {
        retrofit.create(ColaboradorSkillsApiService::class.java)
    }

    /**
     * API Service de skills
     */
    val skillApi: SkillApiService by lazy {
        retrofit.create(SkillApiService::class.java)
    }

    /**
     * API Service de niveles de skill
     */
    val nivelSkillApi: NivelSkillApiService by lazy {
        retrofit.create(NivelSkillApiService::class.java)
    }

    /**
     * API Service de alertas
     */
    val alertasApi: AlertasApiService by lazy {
        retrofit.create(AlertasApiService::class.java)
    }
}
