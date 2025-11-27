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
 * RetrofitClient unificado:
 * - JWT Interceptor dinámico
 * - Logging BODY en debug
 * - BaseURL modificable en runtime
 * - Servicios expuestos correctamente (incluyendo Evaluaciones)
 */
object RetrofitClient {

    // --- BASE URL (priorizamos la del master: 10.0.2.2 para emulador) ---
    @Volatile
    private var baseUrl: String = "http://192.168.1.7:5260/"

    @Synchronized
    fun setBaseUrl(url: String) {
        baseUrl = if (url.endsWith("/")) url else "$url/"
        retrofit = createRetrofit()
    }

    // --- JWT TOKEN ---
    private var authToken: String? = null

    fun setJwtToken(token: String) {
        authToken = token
        retrofit = createRetrofit() // Rebuild para adjuntar token en headers
    }

    fun clearToken() {
        authToken = null
        retrofit = createRetrofit()
    }

    // --- OKHTTP CLIENT ---
    private val okHttpClient: OkHttpClient
        get() {
            val logging = HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG)
                    HttpLoggingInterceptor.Level.BODY
                else
                    HttpLoggingInterceptor.Level.NONE
            }

            val authInterceptor = Interceptor { chain ->
                val newRequest = chain.request()
                    .newBuilder()
                    .apply {
                        authToken?.let {
                            addHeader("Authorization", "Bearer $it")
                        }
                    }
                    .build()

                chain.proceed(newRequest)
            }

            return OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .addInterceptor(logging)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()
        }

    // --- RETROFIT INSTANCE ---
    @Volatile
    private var retrofit: Retrofit = createRetrofit()

    private fun createRetrofit(): Retrofit {
        val gson = GsonBuilder()
            .setLenient()
            .create()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    // --- API SERVICES UNIFICADOS ---

    val authApi: AuthApiService
        get() = retrofit.create(AuthApiService::class.java)

    val colaboradorApi: ColaboradorApiService
        get() = retrofit.create(ColaboradorApiService::class.java)

    val skillApi: SkillApiService
        get() = retrofit.create(SkillApiService::class.java)

    val nivelSkillApi: NivelSkillApiService
        get() = retrofit.create(NivelSkillApiService::class.java)

    val catalogoApi: CatalogoApiService
        get() = retrofit.create(CatalogoApiService::class.java)

    val vacanteApi: VacanteApiService
        get() = retrofit.create(VacanteApiService::class.java)

    val procesosMatchingApi: ProcesosMatchingApiService
        get() = retrofit.create(ProcesosMatchingApiService::class.java)

    val alertasApi: AlertasApiService
        get() = retrofit.create(AlertasApiService::class.java)

    val solicitudesApi: SolicitudesApiService
        get() = retrofit.create(SolicitudesApiService::class.java)

    // ⭐ EXTRAÍDO DEL MERGE (y agregado correctamente)
    val evaluacionApi: EvaluacionApiService
        get() = retrofit.create(EvaluacionApiService::class.java)
}
