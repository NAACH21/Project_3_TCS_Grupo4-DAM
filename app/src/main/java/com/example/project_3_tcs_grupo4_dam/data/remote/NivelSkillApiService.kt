package com.example.project_3_tcs_grupo4_dam.data.remote

import com.example.project_3_tcs_grupo4_dam.data.model.NivelSkillDto
import retrofit2.http.GET

interface NivelSkillApiService {
    @GET("api/nivelesskill")
    suspend fun getAllNiveles(): List<NivelSkillDto>
}

