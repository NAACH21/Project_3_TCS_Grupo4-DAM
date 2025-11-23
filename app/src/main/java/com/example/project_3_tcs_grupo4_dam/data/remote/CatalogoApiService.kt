package com.example.project_3_tcs_grupo4_dam.data.remote

import com.example.project_3_tcs_grupo4_dam.data.model.CatalogoResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface CatalogoApiService {

    @GET("api/catalogo")
    suspend fun getCatalogo(): CatalogoResponse

    @GET("api/catalogo/seccion/{nombre}")
    suspend fun getCatalogoSeccion(
        @Path("nombre") nombre: String
    ): Any
}