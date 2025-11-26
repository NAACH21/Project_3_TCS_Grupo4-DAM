package com.example.project_3_tcs_grupo4_dam.data.repository

import com.example.project_3_tcs_grupo4_dam.data.model.*
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorDtos.ColaboradorCreateDto
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorDtos.ColaboradorUpdateDto
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorDtos.ColaboradorReadDto
import com.example.project_3_tcs_grupo4_dam.data.remote.RetrofitClient

class ColaboradorRepositoryImpl : ColaboradorRepository {

    private val apiService = RetrofitClient.colaboradorApi
    private val skillApiService = RetrofitClient.skillApi
    private val nivelSkillApiService = RetrofitClient.nivelSkillApi

    // ------------------------------------------------------------------
    // GET ALL
    // ------------------------------------------------------------------
    override suspend fun getAllColaboradores(): List<ColaboradorReadDto> {
        val responses = apiService.getColaboradores()

        // Se obtiene catálogo solo 1 vez
        val catalogSkills = try { skillApiService.getAllSkills() } catch (_: Exception) { emptyList() }

        val nameToId = catalogSkills.associateBy(
            keySelector = { it.nombre.trim().lowercase() },
            valueTransform = { it.id }
        )

        return responses.map { mapResponseToReadDto(it, nameToId) }
    }

    // ------------------------------------------------------------------
    // GET BY ID
    // ------------------------------------------------------------------
    override suspend fun getColaboradorById(id: String): ColaboradorReadDto {
        val resp = apiService.getColaboradorById(id)
        val catalogSkills = try { skillApiService.getAllSkills() } catch (_: Exception) { emptyList() }

        val nameToId = catalogSkills.associateBy(
            keySelector = { it.nombre.trim().lowercase() },
            valueTransform = { it.id }
        )

        return mapResponseToReadDto(resp, nameToId)
    }

    // ------------------------------------------------------------------
    // CREATE
    // ------------------------------------------------------------------
    override suspend fun createColaborador(body: ColaboradorCreateDto): ColaboradorReadDto {
        val resp = apiService.createColaborador(body)

        val catalogSkills = try { skillApiService.getAllSkills() } catch (_: Exception) { emptyList() }
        val nameToId = catalogSkills.associateBy(
            keySelector = { it.nombre.trim().lowercase() },
            valueTransform = { it.id }
        )

        return mapResponseToReadDto(resp, nameToId)
    }

    // ------------------------------------------------------------------
    // UPDATE
    // ------------------------------------------------------------------
    override suspend fun updateColaborador(id: String, body: ColaboradorUpdateDto): ColaboradorReadDto {
        val resp = apiService.updateColaborador(id, body)

        val catalogSkills = try { skillApiService.getAllSkills() } catch (_: Exception) { emptyList() }
        val nameToId = catalogSkills.associateBy(
            keySelector = { it.nombre.trim().lowercase() },
            valueTransform = { it.id }
        )

        return mapResponseToReadDto(resp, nameToId)
    }

    // ------------------------------------------------------------------
    // DELETE
    // ------------------------------------------------------------------
    override suspend fun deleteColaborador(id: String) {
        val response = apiService.deleteColaborador(id)
        if (!response.isSuccessful) {
            throw Exception("Error al eliminar colaborador: ${response.code()}")
        }
    }

    // ------------------------------------------------------------------
    // CATÁLOGOS
    // ------------------------------------------------------------------
    override suspend fun getAllSkills(): List<SkillDto> {
        return try { skillApiService.getAllSkills() } catch (e: Exception) { emptyList() }
    }

    override suspend fun getAllNiveles(): List<NivelSkillDto> {
        return try { nivelSkillApiService.getAllNiveles() } catch (e: Exception) { emptyList() }
    }

    // ------------------------------------------------------------------
    // MAPEO AVANZADO DE RESPUESTA -> ReadDto
    // ------------------------------------------------------------------
    private fun mapResponseToReadDto(
        resp: ColaboradorResponse,
        nameToId: Map<String, String>
    ): ColaboradorReadDto {

        val id = resp.id?.`$oid` ?: ""

        // rol actual
        val rolActual = resp.rolLaboral ?: ""

        // Mapeo de skills
        val skillsIds: List<String> = resp.skills.mapNotNull { skillResp ->
            val nameKey = skillResp.nombre.trim().lowercase()
            nameToId[nameKey] ?: skillResp.nombre // fallback si no existe en el catálogo
        }

        // Nivel
        val nivelCodigo: Int? = resp.skills.firstOrNull()?.nivel

        // Certificaciones
        val certificaciones = resp.certificaciones.map { cert ->
            CertificacionDto(
                nombre = cert.nombre,
                imagenUrl = cert.archivoPdfUrl,
                fechaObtencion = cert.fechaObtencion?.toString(),
                estado = cert.estado ?: ""
            )
        }

        // Disponibilidad default
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
