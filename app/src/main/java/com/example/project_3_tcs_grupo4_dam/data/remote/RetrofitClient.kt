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
 * - Servicios expuestos via getters
 */
object RetrofitClient {
    // BASE_URL por defecto: usa 10.0.2.2 para el emulador Android que apunta al host
    @Volatile
    private var baseUrl: String = "http://10.0.2.2:5260/"

    // Permitir cambiar la base URL en tiempo de ejecución
    @Synchronized
    fun setBaseUrl(url: String) {
        baseUrl = if (url.endsWith("/")) url else "$url/"
        // reconstrucción perezosa: se recreará retrofit al pedir servicios
        retrofit = createRetrofit()
    }

    // JWT en memoria
    private var authToken: String? = null

    fun setJwtToken(token: String) {
        authToken = token
    }

    fun clearToken() {
        authToken = null
    }

    // OKHTTP CLIENT (compartido)
    private val okHttpClient: OkHttpClient by lazy {

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
            else HttpLoggingInterceptor.Level.NONE
        }

        val authInterceptor = Interceptor { chain ->
            val original = chain.request()
            val builder = original.newBuilder()

            authToken?.let { token ->
                builder.addHeader("Authorization", "Bearer $token")
            }

            chain.proceed(builder.build())
        }

        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    // Retrofit instance (recreable)
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

    // Exponer servicios como getters para usar la instancia actualizada de retrofit
    val authApi: AuthApiService get() = retrofit.create(AuthApiService::class.java)
    val colaboradorApi: ColaboradorApiService get() = retrofit.create(ColaboradorApiService::class.java)
    val skillApi: SkillApiService get() = retrofit.create(SkillApiService::class.java)
    val catalogoApi: CatalogoApiService get() = retrofit.create(CatalogoApiService::class.java)
    val nivelSkillApi: NivelSkillApiService get() = retrofit.create(NivelSkillApiService::class.java)
    val vacanteApi: VacanteApiService get() = retrofit.create(VacanteApiService::class.java)
    val procesosMatchingApi: ProcesosMatchingApiService get() = retrofit.create(ProcesosMatchingApiService::class.java)
    val alertasApi: AlertasApiService get() = retrofit.create(AlertasApiService::class.java)
    val solicitudesApi: SolicitudesApiService get() = retrofit.create(SolicitudesApiService::class.java)
}
