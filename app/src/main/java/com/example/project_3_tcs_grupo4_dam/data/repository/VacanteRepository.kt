package com.example.project_3_tcs_grupo4_dam.data.repository

<<<<<<< HEAD
import com.example.project_3_tcs_grupo4_dam.data.model.VacanteResponse
import com.example.project_3_tcs_grupo4_dam.data.remote.RetrofitClient

class VacanteRepository {
    private val api = RetrofitClient.vacanteApi
    suspend fun getVacantes(): List<VacanteResponse> = api.getVacantes()
    suspend fun getVacanteById(id: String): VacanteResponse = api.getVacanteById(id)
=======
import com.example.project_3_tcs_grupo4_dam.data.model.Vacante
import com.example.project_3_tcs_grupo4_dam.data.model.VacanteCreateDto
import com.example.project_3_tcs_grupo4_dam.data.remote.VacanteApiService

class VacanteRepository(private val vacanteApiService: VacanteApiService) {
    suspend fun getVacantes(): List<Vacante> {
        return vacanteApiService.getVacantes()
    }

    suspend fun createVacante(vacante: VacanteCreateDto): Vacante {
        return vacanteApiService.createVacante(vacante)
    }
>>>>>>> origin/feat/ScreenVacantModify
}
