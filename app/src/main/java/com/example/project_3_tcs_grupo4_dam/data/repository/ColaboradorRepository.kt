package com.example.project_3_tcs_grupo4_dam.data.repository

import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorCreateDto
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorListDto // Importar DTO de lista
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorReadDto
import com.example.project_3_tcs_grupo4_dam.data.model.NivelSkillDto
import com.example.project_3_tcs_grupo4_dam.data.model.SkillDto

interface ColaboradorRepository {
    // CORRECCIÓN: Retorna lista simplificada para evitar crash por skills complejas
    suspend fun getAllColaboradores(): List<ColaboradorListDto>
    
    suspend fun getColaboradorById(id: String): ColaboradorReadDto
    suspend fun createColaborador(body: ColaboradorCreateDto): ColaboradorReadDto
    suspend fun updateColaborador(id: String, body: ColaboradorCreateDto): ColaboradorReadDto
    suspend fun deleteColaborador(id: String)
    suspend fun getAllSkills(): List<SkillDto>
    suspend fun getAllNiveles(): List<NivelSkillDto>
}