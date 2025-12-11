package com.example.project_3_tcs_grupo4_dam.data.remote

import com.example.project_3_tcs_grupo4_dam.BuildConfig
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
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
  

    // Variable para almacenar el token JWT en memoria
    private var authToken: String? = null

    /*
     * Función para establecer el token JWT después del login
     */
    fun setJwtToken(token: String) {
        authToken = token
    }
    // Base URL del backend .NET
    private const val BASE_URL = "http://192.168.1.5:5260/"

    /**
     * Función para limpiar el token al cerrar sesión
     */
    fun clearToken() {
        authToken = null
    }
 
    /**
     * Cliente OkHttp con logging, timeouts y AuthInterceptor
     */
    private val okHttpClient: OkHttpClient by lazy {
        
        // Interceptor para Logging
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        // Interceptor para Autenticación (Bearer Token)
        val authInterceptor = Interceptor { chain ->
            val originalRequest = chain.request()
            val requestBuilder = originalRequest.newBuilder()
            
            // Si hay un token, lo agregamos al header Authorization
            authToken?.let { token ->
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }
            
            chain.proceed(requestBuilder.build())
        }

        OkHttpClient.Builder()
            .addInterceptor(authInterceptor) // Primero Auth para inyectar header
            .addInterceptor(loggingInterceptor) // Luego Logger para verlo
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

     * API Service de alertas
     */
    val alertasApi: AlertasApiService by lazy {
        retrofit.create(AlertasApiService::class.java)
    }

    /**
     * API Service de solicitudes
     */
    val solicitudesApi: SolicitudesApiService by lazy {
        retrofit.create(SolicitudesApiService::class.java)
    }

    val catalogoApi: CatalogoApiService by lazy {
        retrofit.create(CatalogoApiService::class.java)
  
    }
    val vacanteApi: VacanteApiService by lazy {
        retrofit.create(VacanteApiService::class.java)
    }

    val evaluacionApiService: EvaluacionApiService by lazy {
        retrofit.create(EvaluacionApiService::class.java)
    }

    val colaboradorApiService: ColaboradorApiService by lazy { // Added this
        retrofit.create(ColaboradorApiService::class.java)
    }
    val procesosMatchingApi: ProcesosMatchingApiService
        get() = retrofit.create(ProcesosMatchingApiService::class.java)

    // En data/remote/RetrofitClient.kt agrega:
    val dashboardApiService: DashboardApiService by lazy {
        retrofit.create(DashboardApiService::class.java)
    }

    /**
     * API Service de certificados
     */
    val certificadosApi: CertificadosApiService by lazy {
        retrofit.create(CertificadosApiService::class.java)
    }
}
