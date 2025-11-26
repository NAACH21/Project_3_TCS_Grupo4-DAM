package com.example.project_3_tcs_grupo4_dam.data.remote

import com.example.project_3_tcs_grupo4_dam.BuildConfig
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = "http://192.168.1.8:5260/"

    private val okHttpClient: OkHttpClient by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
            else HttpLoggingInterceptor.Level.NONE
        }
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
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
}
