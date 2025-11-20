package com.example.project_3_tcs_grupo4_dam.data.repository

import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorCreateDto
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorListDto // Importar DTO de lista
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorReadDto
import com.example.project_3_tcs_grupo4_dam.data.model.NivelSkillDto
import com.example.project_3_tcs_grupo4_dam.data.model.SkillDto
import com.example.project_3_tcs_grupo4_dam.data.remote.RetrofitClient

// Esta clase implementa la interfaz
class ColaboradorRepositoryImpl : ColaboradorRepository {

    // Obtiene el servicio de API desde nuestro RetrofitClient
    private val apiService = RetrofitClient.colaboradorApi
    private val skillApiService = RetrofitClient.skillApi
    private val nivelSkillApiService = RetrofitClient.nivelSkillApi

    // CORRECCIÓN: Cambiar el tipo de retorno a List<ColaboradorListDto>
    override suspend fun getAllColaboradores(): List<ColaboradorListDto> {
        // Llama a la API y devuelve el resultado
        return apiService.getAllColaboradores()
    }

    override suspend fun getColaboradorById(id: String): ColaboradorReadDto {
        return apiService.getColaboradorById(id)
    }

    override suspend fun createColaborador(body: ColaboradorCreateDto): ColaboradorReadDto {
        return apiService.createColaborador(body)
    }

    override suspend fun updateColaborador(id: String, body: ColaboradorCreateDto): ColaboradorReadDto {
        return apiService.updateColaborador(id, body)
    }

    override suspend fun deleteColaborador(id: String) {
        return apiService.deleteColaborador(id)
    }

    override suspend fun getAllSkills(): List<SkillDto> {
        return skillApiService.getAllSkills()
    }

    override suspend fun getAllNiveles(): List<NivelSkillDto> {
        return nivelSkillApiService.getAllNiveles()
    }
}