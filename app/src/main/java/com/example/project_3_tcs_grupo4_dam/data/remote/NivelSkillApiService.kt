package com.example.project_3_tcs_grupo4_dam.data.remote

import com.example.project_3_tcs_grupo4_dam.data.model.CatalogoDtos
import retrofit2.http.GET

interface NivelSkillApiService {
    @GET("api/niveles-skill") // Asumiendo esta ruta, ajústala si es diferente
    suspend fun getAllNiveles(): List<CatalogoDtos.NivelSkillDto>
}
