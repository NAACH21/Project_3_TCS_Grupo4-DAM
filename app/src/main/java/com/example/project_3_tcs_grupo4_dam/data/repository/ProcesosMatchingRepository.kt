package com.example.project_3_tcs_grupo4_dam.data.repository

import com.example.project_3_tcs_grupo4_dam.data.model.ProcesoMatchingRequest
import com.example.project_3_tcs_grupo4_dam.data.remote.RetrofitClient

class ProcesosMatchingRepository {
    private val api = RetrofitClient.procesosMatchingApi
    suspend fun createProceso(request: ProcesoMatchingRequest) = api.createProceso(request)
}
