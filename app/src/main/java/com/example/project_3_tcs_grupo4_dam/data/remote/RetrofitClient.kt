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

    private const val BASE_URL = "http://192.168.1.8:5260/"
    // IP Actualizada: Asegúrate de que esta sea la IP de tu PC en la red local
    private const val BASE_URL = "http://192.168.18.94:5260/"

    // Variable para almacenar el token JWT en memoria
    private var authToken: String? = null

    /**
     * Función para establecer el token JWT después del login
     */
    fun setJwtToken(token: String) {
        authToken = token
    }

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
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
            else HttpLoggingInterceptor.Level.NONE
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

    private val retrofit: Retrofit by lazy {
        // si tu API devuelve fechas ISO / bson, ajusta el dateFormat:
        val gson = GsonBuilder()
            .setLenient()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") // ajusta si tu API usa otro formato
            .create()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    // Exponer los servicios usados en la app
    val authApi: AuthApiService by lazy { retrofit.create(AuthApiService::class.java) }
    val catalogoApi: CatalogoApiService by lazy { retrofit.create(CatalogoApiService::class.java) }
    val colaboradorApi: ColaboradorApiService by lazy { retrofit.create(ColaboradorApiService::class.java) }
    val vacanteApi: VacanteApiService by lazy { retrofit.create(VacanteApiService::class.java) }
    val procesosMatchingApi: ProcesosMatchingApiService by lazy { retrofit.create(ProcesosMatchingApiService::class.java) }
    val skillApi: SkillApiService by lazy { retrofit.create(SkillApiService::class.java) }
    val nivelSkillApi: NivelSkillApiService by lazy { retrofit.create(NivelSkillApiService::class.java) }
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

    /**
     * API Service de skills
     */


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
}
