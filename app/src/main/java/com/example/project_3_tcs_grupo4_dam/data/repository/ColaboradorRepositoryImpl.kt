package com.example.project_3_tcs_grupo4_dam.data.repository

import com.example.project_3_tcs_grupo4_dam.data.model.CertificacionDto
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorDtos.ColaboradorCreateDto
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorDtos.ColaboradorReadDto
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorDtos.ColaboradorUpdateDto
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorResponse
import com.example.project_3_tcs_grupo4_dam.data.model.DisponibilidadDto
import com.example.project_3_tcs_grupo4_dam.data.remote.ColaboradorApiService
import com.example.project_3_tcs_grupo4_dam.data.remote.RetrofitClient
import javax.inject.Inject

class ColaboradorRepositoryImpl @Inject constructor(
    private val apiService: ColaboradorApiService = RetrofitClient.colaboradorApi,
    private val catalogoRepository: CatalogoRepository = CatalogoRepositoryImpl()
) : ColaboradorRepository {

    override suspend fun getAllColaboradores(): List<ColaboradorReadDto> {
        val responses = apiService.getColaboradores()
        val catalogSkills = catalogoRepository.getSkillsCatalogo()
        val nameToId = catalogSkills.associateBy({ it.nombre.trim().lowercase() }, { it.id })

        return responses.map { mapResponseToReadDto(it, nameToId) }
    }

    override suspend fun getColaboradorById(id: String): ColaboradorReadDto {
        val resp = apiService.getColaboradorById(id)
        val catalogSkills = catalogoRepository.getSkillsCatalogo()
        val nameToId = catalogSkills.associateBy({ it.nombre.trim().lowercase() }, { it.id })
        return mapResponseToReadDto(resp, nameToId)
    }

    override suspend fun createColaborador(body: ColaboradorCreateDto): ColaboradorReadDto {
        return apiService.createColaborador(body)
    }

    override suspend fun updateColaborador(
        id: String,
        body: ColaboradorUpdateDto
    ): ColaboradorReadDto {
        return apiService.updateColaborador(id, body)
    }

    override suspend fun deleteColaborador(id: String) {
        val response = apiService.deleteColaborador(id)
        if (!response.isSuccessful) {
            throw Exception("Error al eliminar colaborador: ${response.code()}")
        }
    }

    private fun mapResponseToReadDto(resp: ColaboradorResponse, nameToId: Map<String, String>): ColaboradorReadDto {
        // id
        val id = resp.id?.`$oid` ?: ""

        // rolLaboral -> rolActual
        val rolActual = resp.rolLaboral ?: ""

        // skills: intentar mapear por nombre a id; si no se encuentra, usar el nombre como fallback
        val skillsIds: List<String> = resp.skills.mapNotNull { skillResp ->
            val nameKey = skillResp.nombre.trim().lowercase()
            nameToId[nameKey] ?: skillResp.nombre
        }

        // nivelCodigo: no está en la respuesta general; intentar inferir del primer skill si existe
        val nivelCodigo: Int? = resp.skills.firstOrNull()?.nivel

        // certificaciones: mapear algunos campos relevantes
        val certificaciones = resp.certificaciones.map { cert ->
            CertificacionDto(
                nombre = cert.nombre,
                imagenUrl = cert.archivoPdfUrl,
                fechaObtencion = cert.fechaObtencion?.toString(),
                estado = cert.estado ?: ""
            )
        }

        // disponibilidad: si no existe, usar defaults
        val disponibilidad = DisponibilidadDto(
            estado = "Disponible",
            dias = 0
        )

        return ColaboradorReadDto(
            id = id,
            nombres = resp.nombres,
            apellidos = resp.apellidos,
            area = resp.area,
            rolActual = rolActual,
            skills = skillsIds,
            nivelCodigo = nivelCodigo,
            certificaciones = certificaciones,
            disponibilidad = disponibilidad
        )
    }
}
