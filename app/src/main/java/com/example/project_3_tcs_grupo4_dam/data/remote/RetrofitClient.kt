package com.example.project_3_tcs_grupo4_dam.data.remote

import com.example.project_3_tcs_grupo4_dam.BuildConfig
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * RetrofitClient unificado:
 * - Interceptor JWT
 * - Logging de requests/responses
 * - Conversor Gson lenient + soporte ISO Date
 * - Todos los servicios expuestos
 */
object RetrofitClient {
    // RECUERDA: 10.0.2.2 para emulador, o tu IP real para teléfono
    private const val BASE_URL = "http://10.0.2.2:5000/"


    // ============================
    // TOKEN JWT en memoria
    // ============================
    private var authToken: String? = null

    fun setJwtToken(token: String) {
        authToken = token
    }

    fun clearToken() {
        authToken = null
    }

    // ============================
    // OKHTTP CLIENT
    // ============================
    private val okHttpClient: OkHttpClient by lazy {

        // Logging HTTP
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
            else HttpLoggingInterceptor.Level.NONE
        }

        // Interceptor de autenticación
        val authInterceptor = Interceptor { chain ->
            val original = chain.request()
            val builder = original.newBuilder()

            authToken?.let { token ->
                builder.addHeader("Authorization", "Bearer $token")
            }

            chain.proceed(builder.build())
        }

        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)     // primero auth
            .addInterceptor(loggingInterceptor) // luego logging
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    // ============================
    // RETROFIT INSTANCE
    // ============================
    private val retrofit: Retrofit by lazy {
        // Definir y configurar Gson aquí
        val gson = GsonBuilder()
            .setLenient() // Permite un JSON más flexible
            .create()

        // Usar la instancia de gson que acabas de crear
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    val authApi: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }

    val colaboradorApi: ColaboradorApiService by lazy { retrofit.create(ColaboradorApiService::class.java) }

    val skillApi: SkillApiService by lazy { retrofit.create(SkillApiService::class.java) }

    val catalogoApi: CatalogoApiService by lazy { retrofit.create(CatalogoApiService::class.java) }


    val nivelSkillApi: NivelSkillApiService by lazy {
        retrofit.create(NivelSkillApiService::class.java)
    }

    val vacanteApi: VacanteApiService by lazy {
        retrofit.create(VacanteApiService::class.java)
    }
    val procesosMatchingApi: ProcesosMatchingApiService by lazy { retrofit.create(ProcesosMatchingApiService::class.java) }

    val alertasApi: AlertasApiService by lazy { retrofit.create(AlertasApiService::class.java) }

    val solicitudesApi: SolicitudesApiService by lazy { retrofit.create(SolicitudesApiService::class.java) }
}
