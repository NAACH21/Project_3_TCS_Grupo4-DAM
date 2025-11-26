package com.example.project_3_tcs_grupo4_dam.data.repository

import com.example.project_3_tcs_grupo4_dam.data.model.VacanteResponse
import com.example.project_3_tcs_grupo4_dam.data.remote.RetrofitClient

class VacanteRepository {
    private val api = RetrofitClient.vacanteApi
    suspend fun getVacantes(): List<VacanteResponse> = api.getVacantes()
    suspend fun getVacanteById(id: String): VacanteResponse = api.getVacanteById(id)
}
