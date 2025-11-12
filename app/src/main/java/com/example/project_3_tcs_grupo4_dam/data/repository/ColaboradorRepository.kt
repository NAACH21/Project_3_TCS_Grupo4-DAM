package com.example.project_3_tcs_grupo4_dam.data.repository

import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorReadDto

interface ColaboradorRepository {
    suspend fun getAllColaboradores(): List<ColaboradorReadDto>
}