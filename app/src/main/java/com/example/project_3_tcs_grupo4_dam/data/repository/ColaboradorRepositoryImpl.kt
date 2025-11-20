package com.example.project_3_tcs_grupo4_dam.data.repository

import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorCreateDto
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorListDto
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorReadDto
import com.example.project_3_tcs_grupo4_dam.data.model.NivelSkillDto
import com.example.project_3_tcs_grupo4_dam.data.model.SkillDto
import com.example.project_3_tcs_grupo4_dam.data.remote.RetrofitClient

class ColaboradorRepositoryImpl : ColaboradorRepository {

    private val apiService = RetrofitClient.colaboradorApi
    private val skillApiService = RetrofitClient.skillApi
    private val nivelSkillApiService = RetrofitClient.nivelSkillApi

    override suspend fun getAllColaboradores(): List<ColaboradorListDto> {
        return apiService.getAllColaboradores()
    }

    override suspend fun getColaboradorById(id: String): ColaboradorReadDto {
        val response = apiService.getColaboradorById(id)
        if (response.isSuccessful) {
            // CORRECCIÓN: Obtenemos el objeto directo, sin buscar 'success' ni 'data'
            val colaborador = response.body()
            if (colaborador != null) {
                return colaborador
            }
            throw Exception("La respuesta del servidor fue exitosa pero vacía")
        }
        throw Exception("Error HTTP: ${response.code()}")
    }

    override suspend fun createColaborador(body: ColaboradorCreateDto): ColaboradorReadDto {
        val response = apiService.createColaborador(body)
        if (response.isSuccessful) {
            val colaborador = response.body()
            if (colaborador != null) {
                return colaborador
            }
            throw Exception("Error al crear colaborador: respuesta vacía")
        }
        throw Exception("Error HTTP: ${response.code()}")
    }

    override suspend fun updateColaborador(id: String, body: ColaboradorCreateDto): ColaboradorReadDto {
        // apiService.updateColaborador acepta Any, así que podemos pasar el DTO
        val response = apiService.updateColaborador(id, body)
        if (response.isSuccessful) {
            val colaborador = response.body()
            if (colaborador != null) {
                return colaborador
            }
            throw Exception("Error al actualizar colaborador: respuesta vacía")
        }
        throw Exception("Error HTTP: ${response.code()}")
    }

    override suspend fun deleteColaborador(id: String) {
        val response = apiService.deleteColaborador(id)
        if (!response.isSuccessful) {
            throw Exception("Error HTTP al eliminar: ${response.code()}")
        }
    }

    override suspend fun getAllSkills(): List<SkillDto> {
        return skillApiService.getAllSkills()
    }

    override suspend fun getAllNiveles(): List<NivelSkillDto> {
        return nivelSkillApiService.getAllNiveles()
    }
}
