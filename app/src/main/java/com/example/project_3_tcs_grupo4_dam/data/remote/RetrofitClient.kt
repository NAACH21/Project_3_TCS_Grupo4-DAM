package com.example.project_3_tcs_grupo4_dam.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // RECUERDA: 10.0.2.2 para emulador, o tu IP real para teléfono
    private const val BASE_URL = "http://10.0.2.2:5000/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    val authApi: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }

    val colaboradorApi: ColaboradorApiService by lazy {
        retrofit.create(ColaboradorApiService::class.java)
    }

    val skillApi: SkillApiService by lazy {
        retrofit.create(SkillApiService::class.java)
    }

    val nivelSkillApi: NivelSkillApiService by lazy {
        retrofit.create(NivelSkillApiService::class.java)
    }

    val vacanteApi: VacanteApiService by lazy {
        retrofit.create(VacanteApiService::class.java)
    }
}
