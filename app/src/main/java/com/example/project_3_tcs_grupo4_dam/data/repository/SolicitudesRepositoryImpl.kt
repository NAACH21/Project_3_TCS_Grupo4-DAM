package com.example.project_3_tcs_grupo4_dam.data.repository

import com.example.project_3_tcs_grupo4_dam.data.model.SolicitudCreateDto
import com.example.project_3_tcs_grupo4_dam.data.model.SolicitudReadDto
import com.example.project_3_tcs_grupo4_dam.data.model.SolicitudUpdateEstadoDto
import com.example.project_3_tcs_grupo4_dam.data.remote.SolicitudesApiService

class SolicitudesRepositoryImpl(
    private val apiService: SolicitudesApiService
) : SolicitudesRepository {

    override suspend fun getSolicitudes(): List<SolicitudReadDto> {
        val response = apiService.getSolicitudes()
        if (response.isSuccessful) {
            val solicitudes = response.body()
            if (solicitudes != null) {
                return solicitudes
            }
            throw Exception("Error al obtener solicitudes: respuesta vacía")
        }
        throw Exception("Error al obtener solicitudes: ${response.code()} - ${response.errorBody()?.string()}")
    }

    override suspend fun getSolicitudById(id: String): SolicitudReadDto {
        val response = apiService.getSolicitudById(id)
        if (response.isSuccessful) {
            val solicitud = response.body()
            if (solicitud != null) {
                return solicitud
            }
            throw Exception("Error al obtener solicitud: respuesta vacía")
        }
        throw Exception("Error al obtener solicitud: ${response.code()} - ${response.errorBody()?.string()}")
    }

    override suspend fun getSolicitudesByColaborador(colaboradorId: String): List<SolicitudReadDto> {
        val response = apiService.getSolicitudesByColaborador(colaboradorId)
        if (response.isSuccessful) {
            val solicitudes = response.body()
            if (solicitudes != null) {
                return solicitudes
            }
            throw Exception("Error al obtener solicitudes del colaborador: respuesta vacía")
        }
        throw Exception("Error al obtener solicitudes del colaborador: ${response.code()} - ${response.errorBody()?.string()}")
    }

    override suspend fun createSolicitud(request: SolicitudCreateDto): SolicitudReadDto {
        android.util.Log.d("SolicitudesRepository", "=== CREANDO SOLICITUD ===")
        android.util.Log.d("SolicitudesRepository", "Tipo: ${request.tipoSolicitudGeneral}")
        android.util.Log.d("SolicitudesRepository", "ColaboradorId: ${request.colaboradorId}")
        android.util.Log.d("SolicitudesRepository", "CreadoPorUsuarioId: '${request.creadoPorUsuarioId}'")
        android.util.Log.d("SolicitudesRepository", "DTO completo: $request")

        // ⭐ VALIDACIÓN CRÍTICA: Verificar que creadoPorUsuarioId no esté vacío ⭐
        if (request.creadoPorUsuarioId.isNullOrBlank()) {
            android.util.Log.e("SolicitudesRepository", "❌ ERROR: creadoPorUsuarioId está vacío o nulo")
            throw Exception("Error interno: No se puede crear solicitud sin ID de usuario")
        }

        val response = apiService.createSolicitud(request)

        android.util.Log.d("SolicitudesRepository", "Código de respuesta: ${response.code()}")

        if (response.isSuccessful) {
            val solicitud = response.body()
            if (solicitud != null) {
                android.util.Log.d("SolicitudesRepository", "✅ Solicitud creada exitosamente. ID: ${solicitud.id}")
                return solicitud
            }
            android.util.Log.e("SolicitudesRepository", "❌ Error: respuesta vacía del servidor")
            throw Exception("Error al crear solicitud: respuesta vacía")
        }

        val errorBody = response.errorBody()?.string()
        android.util.Log.e("SolicitudesRepository", "❌ Error del servidor: ${response.code()}")
        android.util.Log.e("SolicitudesRepository", "Body del error: $errorBody")
        throw Exception("Error al crear solicitud: ${response.code()} - $errorBody")
    }

    override suspend fun updateEstadoSolicitud(id: String, request: SolicitudUpdateEstadoDto): SolicitudReadDto {
        val response = apiService.updateEstadoSolicitud(id, request)
        if (response.isSuccessful) {
            val solicitud = response.body()
            if (solicitud != null) {
                return solicitud
            }
            throw Exception("Error al actualizar estado de solicitud: respuesta vacía")
        }
        throw Exception("Error al actualizar estado de solicitud: ${response.code()} - ${response.errorBody()?.string()}")
    }

    override suspend fun deleteSolicitud(id: String): String {
        val response = apiService.deleteSolicitud(id)
        if (response.isSuccessful) {
            val deleteResponse = response.body()
            if (deleteResponse != null) {
                return deleteResponse.message
            }
            throw Exception("Error al eliminar solicitud: respuesta vacía")
        }
        throw Exception("Error al eliminar solicitud: ${response.code()} - ${response.errorBody()?.string()}")
    }
}
