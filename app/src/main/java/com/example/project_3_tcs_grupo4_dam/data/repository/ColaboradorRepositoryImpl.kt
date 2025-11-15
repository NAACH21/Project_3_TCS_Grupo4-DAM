package com.example.project_3_tcs_grupo4_dam.data.repository

import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorReadDto
import com.example.project_3_tcs_grupo4_dam.data.remote.RetrofitClient

// Esta clase implementa la interfaz
class ColaboradorRepositoryImpl : ColaboradorRepository {

    // Obtiene el servicio de API desde nuestro RetrofitClient
    private val apiService = RetrofitClient.api

    override suspend fun getAllColaboradores(): List<ColaboradorReadDto> {
        // Llama a la API y devuelve el resultado
        return apiService.getAllColaboradores()
    }

    override suspend fun getColaboradorById(id: String): ColaboradorReadDto {
        return apiService.getColaboradorById(id)
    }
}