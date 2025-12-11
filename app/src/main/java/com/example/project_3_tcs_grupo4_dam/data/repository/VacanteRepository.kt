package com.example.project_3_tcs_grupo4_dam.data.repository

import com.example.project_3_tcs_grupo4_dam.data.model.VacanteResponse
import com.example.project_3_tcs_grupo4_dam.data.model.Vacante
import com.example.project_3_tcs_grupo4_dam.data.model.VacanteCreateDto
import com.example.project_3_tcs_grupo4_dam.data.remote.VacanteApiService
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class VacanteRepository(private val vacanteApiService: VacanteApiService) {

    suspend fun getVacantes(): List<VacanteResponse> {
        return vacanteApiService.getVacantes()
    }

    suspend fun getVacantes(activa: Boolean?): List<VacanteResponse> = withContext(Dispatchers.IO) {
        vacanteApiService.getVacantes(activa)
    }

    suspend fun createVacante(vacante: VacanteCreateDto): Result<Vacante> = withContext(Dispatchers.IO) {
        try {
            // 1. Obtenemos el JsonObject crudo para evitar conversiones automáticas de tipos numéricos
            val jsonObject = vacanteApiService.createVacante(vacante)

            // 2. Extraemos el ID manualmente buscando "id", "_id" o "Id"
            val idElement = jsonObject.get("id") 
                         ?: jsonObject.get("_id") 
                         ?: jsonObject.get("Id")

            // 3. Forzamos conversión a String evitando notación científica
            // asString de Gson devuelve el valor textual correcto incluso si viene como número en el JSON
            val idString = if (idElement != null && !idElement.isJsonNull) {
                idElement.asString 
            } else {
                ""
            }

            if (idString.isBlank()) {
                return@withContext Result.failure(Exception("La respuesta del servidor no incluyó un ID de vacante válido."))
            }

            // 4. Construimos el objeto Vacante con el ID String garantizado
            val vacanteCreada = Vacante(
                id = idString, 
                nombrePerfil = vacante.nombrePerfil,
                area = vacante.area,
                rolLaboral = vacante.rolLaboral,
                skillsRequeridos = vacante.skillsRequeridos,
                certificacionesRequeridas = vacante.certificacionesRequeridas,
                fechaInicio = vacante.fechaInicio,
                urgencia = vacante.urgencia,
                estadoVacante = vacante.estadoVacante,
                creadaPorUsuarioId = vacante.creadaPorUsuarioId,
                fechaCreacion = "", 
                fechaActualizacion = "",
                usuarioActualizacion = ""
            )

            Result.success(vacanteCreada)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
