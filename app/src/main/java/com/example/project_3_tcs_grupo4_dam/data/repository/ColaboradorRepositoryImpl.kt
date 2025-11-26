package com.example.project_3_tcs_grupo4_dam.data.repository

import com.example.project_3_tcs_grupo4_dam.data.model.*
import com.example.project_3_tcs_grupo4_dam.data.remote.RetrofitClient

// Implementación del repositorio que llama a las APIs y transforma los modelos
class ColaboradorRepositoryImpl : ColaboradorRepository {

    private val apiService = RetrofitClient.colaboradorApi
    private val skillApiService = RetrofitClient.skillApi
    private val nivelSkillApiService = RetrofitClient.nivelSkillApi

    override suspend fun getAllColaboradores(): List<ColaboradorReadDto> {
        val responses = apiService.getColaboradores()
        // Para resolver nombres de skills a ids, obtener catálogo una vez
        val catalogSkills = try { skillApiService.getAllSkills() } catch (_: Exception) { emptyList() }
        val nameToId = catalogSkills.associateBy({ it.nombre.trim().lowercase() }, { it.id })

        return responses.map { mapResponseToReadDto(it, nameToId) }
    }

    override suspend fun getColaboradorById(id: String): ColaboradorReadDto {
        val resp = apiService.getColaboradorById(id)
        val catalogSkills = try { skillApiService.getAllSkills() } catch (_: Exception) { emptyList() }
        val nameToId = catalogSkills.associateBy({ it.nombre.trim().lowercase() }, { it.id })
        return mapResponseToReadDto(resp, nameToId)
    }

    override suspend fun createColaborador(body: ColaboradorCreateDto): ColaboradorReadDto {
        val resp = apiService.createColaborador(body)
        val catalogSkills = try { skillApiService.getAllSkills() } catch (_: Exception) { emptyList() }
        val nameToId = catalogSkills.associateBy({ it.nombre.trim().lowercase() }, { it.id })
        return mapResponseToReadDto(resp, nameToId)
    }

    override suspend fun updateColaborador(id: String, body: ColaboradorCreateDto): ColaboradorReadDto {
        val resp = apiService.updateColaborador(id, body)
        val catalogSkills = try { skillApiService.getAllSkills() } catch (_: Exception) { emptyList() }
        val nameToId = catalogSkills.associateBy({ it.nombre.trim().lowercase() }, { it.id })
        return mapResponseToReadDto(resp, nameToId)
    }

    override suspend fun deleteColaborador(id: String) {
        apiService.deleteColaborador(id)
    }

    override suspend fun getAllSkills(): List<SkillDto> {
        return try { skillApiService.getAllSkills() } catch (e: Exception) { emptyList() }
    }

    override suspend fun getAllNiveles(): List<NivelSkillDto> {
        return try { nivelSkillApiService.getAllNiveles() } catch (e: Exception) { emptyList() }
    }

    // Helpers
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