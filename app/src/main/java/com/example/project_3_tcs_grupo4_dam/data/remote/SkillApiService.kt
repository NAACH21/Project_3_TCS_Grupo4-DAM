package com.example.project_3_tcs_grupo4_dam.data.remote

import com.example.project_3_tcs_grupo4_dam.data.model.SkillDto
import retrofit2.http.GET

interface SkillApiService {
    @GET("api/skills")
    suspend fun getAllSkills(): List<SkillDto>
}

