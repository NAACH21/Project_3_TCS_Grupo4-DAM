package com.example.project_3_tcs_grupo4_dam.data.repository

import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorDtos.ColaboradorCreateDto
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorDtos.ColaboradorUpdateDto
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorDtos.ColaboradorReadDto

interface ColaboradorRepository {

    suspend fun getAllColaboradores(): List<ColaboradorReadDto>

    suspend fun getColaboradorById(id: String): ColaboradorReadDto

    suspend fun createColaborador(body: ColaboradorCreateDto): ColaboradorReadDto

    suspend fun updateColaborador(id: String, body: ColaboradorUpdateDto): ColaboradorReadDto

    suspend fun deleteColaborador(id: String)
}
