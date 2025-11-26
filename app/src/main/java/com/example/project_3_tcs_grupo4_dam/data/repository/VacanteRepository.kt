package com.example.project_3_tcs_grupo4_dam.data.repository

import com.example.project_3_tcs_grupo4_dam.data.model.VacanteResponse
import com.example.project_3_tcs_grupo4_dam.data.remote.RetrofitClient

import com.example.project_3_tcs_grupo4_dam.data.model.Vacante
import com.example.project_3_tcs_grupo4_dam.data.model.VacanteCreateDto
import com.example.project_3_tcs_grupo4_dam.data.remote.VacanteApiService

class VacanteRepository(private val vacanteApiService: VacanteApiService) {
    suspend fun getVacantes(): List<VacanteResponse> {
        return vacanteApiService.getVacantes()
    }

    suspend fun createVacante(vacante: VacanteCreateDto): Vacante {
        return vacanteApiService.createVacante(vacante)
    }
}