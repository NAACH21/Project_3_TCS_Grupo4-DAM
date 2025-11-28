package com.example.project_3_tcs_grupo4_dam.data.repository

import com.example.project_3_tcs_grupo4_dam.data.model.SolicitudCreateDto
import com.example.project_3_tcs_grupo4_dam.data.model.SolicitudReadDto
import com.example.project_3_tcs_grupo4_dam.data.model.SolicitudUpdateEstadoDto

interface SolicitudesRepository {
    suspend fun getSolicitudes(): List<SolicitudReadDto>
    suspend fun getSolicitudById(id: String): SolicitudReadDto
    suspend fun getSolicitudesByColaborador(colaboradorId: String): List<SolicitudReadDto>
    suspend fun createSolicitud(request: SolicitudCreateDto): SolicitudReadDto
    suspend fun updateEstadoSolicitud(id: String, request: SolicitudUpdateEstadoDto): SolicitudReadDto
    suspend fun deleteSolicitud(id: String): String
}

