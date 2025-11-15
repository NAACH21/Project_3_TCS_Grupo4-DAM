package com.example.project_3_tcs_grupo4_dam.data.repository

import com.example.project_3_tcs_grupo4_dam.data.model.CertificacionDto
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorReadDto
import com.example.project_3_tcs_grupo4_dam.data.model.DisponibilidadDto

// Implementación MOCK: no usa API; devuelve datos en memoria
class ColaboradorRepositoryImpl : ColaboradorRepository {

    // Datos simulados en memoria para evitar llamadas a red
    private val mockColaboradores = listOf(
        ColaboradorReadDto(
            id = "1",
            nombres = "Ana",
            apellidos = "Pérez",
            area = "QA",
            rolActual = "QA Tester",
            skills = listOf("Testing", "Automatización", "SQL"),
            nivelCodigo = 2,
            certificaciones = listOf(
                CertificacionDto(
                    nombre = "ISTQB Foundation",
                    imagenUrl = null,
                    fechaObtencion = "2024-03-10T00:00:00Z",
                    estado = "vigente"
                )
            ),
            disponibilidad = DisponibilidadDto(estado = "Disponible", dias = 0)
        ),
        ColaboradorReadDto(
            id = "2",
            nombres = "Luis",
            apellidos = "Gómez",
            area = "Backend",
            rolActual = "Desarrollador Backend",
            skills = listOf("Kotlin", "Spring", "Docker", "SQL"),
            nivelCodigo = 3,
            certificaciones = listOf(
                CertificacionDto(
                    nombre = "Oracle Java SE 11",
                    imagenUrl = null,
                    fechaObtencion = "2023-08-15T00:00:00Z",
                    estado = "vigente"
                )
            ),
            disponibilidad = DisponibilidadDto(estado = "Ocupado", dias = 14)
        ),
        ColaboradorReadDto(
            id = "3",
            nombres = "María",
            apellidos = "Rojas",
            area = "Data",
            rolActual = "Data Engineer",
            skills = listOf("Python", "Spark", "SQL", "Data Engineering"),
            nivelCodigo = 3,
            certificaciones = listOf(
                CertificacionDto(
                    nombre = "DP-203 Azure Data Engineer",
                    imagenUrl = null,
                    fechaObtencion = "2022-11-02T00:00:00Z",
                    estado = "vigente"
                )
            ),
            disponibilidad = DisponibilidadDto(estado = "Disponible", dias = 0)
        )
    )

    override suspend fun getAllColaboradores(): List<ColaboradorReadDto> {
        // Retorna la lista simulada
        return mockColaboradores
    }

    override suspend fun getColaboradorById(id: String): ColaboradorReadDto {
        return mockColaboradores.firstOrNull { it.id == id }
            ?: throw NoSuchElementException("Colaborador no encontrado: $id")
    }
}