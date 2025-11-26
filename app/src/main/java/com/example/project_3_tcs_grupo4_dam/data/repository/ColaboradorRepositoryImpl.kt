package com.example.project_3_tcs_grupo4_dam.data.repository

import com.example.project_3_tcs_grupo4_dam.data.model.*
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorDtos.ColaboradorCreateDto
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorDtos.ColaboradorUpdateDto
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorDtos.ColaboradorReadDto
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorDtos.CertificacionReadDto
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorDtos.DisponibilidadDto
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorDtos.SkillReadDto
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

        // Se obtiene catálogo solo 1 vez (ya no es necesario para el mapeo directo aquí)
        // val catalogSkills = try { skillApiService.getAllSkills() } catch (_: Exception) { emptyList() }
        // val nameToId = catalogSkills.associateBy(
        //     keySelector = { it.nombre.trim().lowercase() },
        //     valueTransform = { it.id }
        // )

        return responses.map { mapResponseToReadDto(it) }
    }

    // ------------------------------------------------------------------
    // GET BY ID
    // ------------------------------------------------------------------
    override suspend fun getColaboradorById(id: String): ColaboradorReadDto {
        val resp = apiService.getColaboradorById(id)
        // val catalogSkills = try { skillApiService.getAllSkills() } catch (_: Exception) { emptyList() }
        // val nameToId = catalogSkills.associateBy(
        //     keySelector = { it.nombre.trim().lowercase() },
        //     valueTransform = { it.id }
        // )

        return mapResponseToReadDto(resp)
    }

    // ------------------------------------------------------------------
    // CREATE
    // ------------------------------------------------------------------
    override suspend fun createColaborador(body: ColaboradorCreateDto): ColaboradorReadDto {
        val resp = apiService.createColaborador(body)

        // val catalogSkills = try { skillApiService.getAllSkills() } catch (_: Exception) { emptyList() }
        // val nameToId = catalogSkills.associateBy(
        //     keySelector = { it.nombre.trim().lowercase() },
        //     valueTransform = { it.id }
        // )

        return mapResponseToReadDto(resp)
    }

    // ------------------------------------------------------------------
    // UPDATE
    // ------------------------------------------------------------------
    override suspend fun updateColaborador(id: String, body: ColaboradorUpdateDto): ColaboradorReadDto {
        val resp = apiService.updateColaborador(id, body)

        // val catalogSkills = try { skillApiService.getAllSkills() } catch (_: Exception) { emptyList() }
        // val nameToId = catalogSkills.associateBy(
        //     keySelector = { it.nombre.trim().lowercase() },
        //     valueTransform = { it.id }
        // )

        return mapResponseToReadDto(resp)
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
    override suspend fun getAllSkills(): List<com.example.project_3_tcs_grupo4_dam.data.model.SkillDto> {
        return try { skillApiService.getAllSkills() } catch (e: Exception) { emptyList() }
    }

    override suspend fun getAllNiveles(): List<com.example.project_3_tcs_grupo4_dam.data.model.CatalogoDtos.NivelSkillDto> {
        return try { nivelSkillApiService.getAllNiveles() } catch (e: Exception) { emptyList() }
    }

    // ------------------------------------------------------------------
    // MAPEO AVANZADO DE RESPUESTA -> ReadDto
    // ------------------------------------------------------------------
    private fun mapResponseToReadDto(
        resp: ColaboradorResponse
    ): ColaboradorReadDto {

        val id = resp.id?.`$oid` ?: ""

        // rol actual
        val rolActual = resp.rolLaboral ?: ""

        // Mapeo de skills
        val skillsReadDto: List<SkillReadDto> = resp.skills.map { skillResp ->
            SkillReadDto(
                nombre = skillResp.nombre,
                tipo = skillResp.tipo,
                nivel = skillResp.nivel,
                esCritico = skillResp.esCritico
            )
        }

        // Certificaciones
        val certificaciones = resp.certificaciones.map { cert ->
            CertificacionReadDto(
                certificacionId = cert.certificacionId,
                nombre = cert.nombre,
                institucion = cert.institucion ?: "Desconocida",
                fechaObtencion = cert.fechaObtencion?.toString(),
                fechaVencimiento = cert.fechaVencimiento?.toString(),
                archivoPdfUrl = cert.archivoPdfUrl,
                estado = cert.estado ?: "",
                fechaRegistro = cert.fechaRegistro?.toString(),
                fechaActualizacion = cert.fechaActualizacion?.toString(),
                proximaEvaluacion = cert.proximaEvaluacion?.toString()
            )
        }

        return ColaboradorReadDto(
            id = id,
            nombres = resp.nombres,
            apellidos = resp.apellidos,
            correo = resp.correo ?: "",
            area = resp.area,
            rolLaboral = rolActual,
            estado = resp.estado,
            disponibleParaMovilidad = resp.disponibleParaMovilidad,
            skills = skillsReadDto,
            certificaciones = certificaciones,
            fechaRegistro = resp.fechaRegistro?.toString(),
            fechaActualizacion = resp.fechaActualizacion?.toString()
        )
    }
}
