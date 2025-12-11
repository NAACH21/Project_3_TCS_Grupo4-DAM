package com.example.project_3_tcs_grupo4_dam.data.repository

import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorDtos.ColaboradorCreateDto
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorDtos.ColaboradorUpdateDto
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorDtos.ColaboradorReadDto
import com.example.project_3_tcs_grupo4_dam.data.model.SkillDto
import com.example.project_3_tcs_grupo4_dam.data.model.CatalogoDtos
import com.example.project_3_tcs_grupo4_dam.data.remote.RetrofitClient

class ColaboradorRepositoryImpl : ColaboradorRepository {

    // Único servicio necesario ahora
    private val apiService = RetrofitClient.colaboradorApi
    private val catalogoApi = RetrofitClient.catalogoApi

    override suspend fun getAllColaboradores(): List<ColaboradorReadDto> {
        return apiService.getAllColaboradores()
    }

    override suspend fun getColaboradorById(id: String): ColaboradorReadDto {
        return apiService.getColaboradorById(id)
    }

    override suspend fun createColaborador(body: ColaboradorCreateDto): ColaboradorReadDto {
        return apiService.createColaborador(body)
    }

    override suspend fun updateColaborador(
        id: String,
        body: ColaboradorUpdateDto
    ): ColaboradorReadDto {
        return apiService.updateColaborador(id, body)
    }

    override suspend fun deleteColaborador(id: String) {
        val response = apiService.deleteColaborador(id)
        if (!response.isSuccessful) {
            throw Exception("Error al eliminar colaborador: ${response.code()}")
        }
    }
    
    // Implementación de métodos nuevos usando apiService (WORKAROUND) y catalogoApi
    override suspend fun getAllSkills(): List<SkillDto> {
        return apiService.getAllSkills()
    }
    
    override suspend fun getAllNiveles(): List<CatalogoDtos.NivelSkillDto> {
        val response = catalogoApi.getNivelesSkill()
        return if (response.success && response.data != null) {
            response.data
        } else {
            emptyList()
        }
    }
}
