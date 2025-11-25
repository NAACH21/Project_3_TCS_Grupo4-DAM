package com.example.project_3_tcs_grupo4_dam.data.repository

import com.example.project_3_tcs_grupo4_dam.data.model.CatalogoReadDto
import com.example.project_3_tcs_grupo4_dam.data.model.NivelSkillDto
import com.example.project_3_tcs_grupo4_dam.data.model.SkillCatalogItemDto
import com.example.project_3_tcs_grupo4_dam.data.remote.RetrofitClient

/**
 * Implementación del repositorio de Catálogo
 * Consume el CatalogoApiService para obtener opciones precargadas
 */
class CatalogoRepositoryImpl : CatalogoRepository {

    private val apiService = RetrofitClient.catalogoApi

    override suspend fun getCatalogoCompleto(): CatalogoReadDto {
        val response = apiService.getCatalogoCompleto()
        if (response.success && response.data != null) {
            return response.data
        } else {
            throw Exception(response.message ?: "Error al obtener el catálogo completo")
        }
    }

    override suspend fun getAreas(): List<String> {
        val response = apiService.getAreas()
        if (response.success && response.data != null) {
            return response.data
        } else {
            throw Exception(response.message ?: "Error al obtener las áreas")
        }
    }

    override suspend fun getRolesLaborales(): List<String> {
        val response = apiService.getRolesLaborales()
        if (response.success && response.data != null) {
            return response.data
        } else {
            throw Exception(response.message ?: "Error al obtener los roles laborales")
        }
    }

    override suspend fun getNivelesSkill(): List<NivelSkillDto> {
        val response = apiService.getNivelesSkill()
        if (response.success && response.data != null) {
            return response.data
        } else {
            throw Exception(response.message ?: "Error al obtener los niveles de skill")
        }
    }

    override suspend fun getTiposSkill(): List<String> {
        val response = apiService.getTiposSkill()
        if (response.success && response.data != null) {
            return response.data
        } else {
            throw Exception(response.message ?: "Error al obtener los tipos de skill")
        }
    }

    override suspend fun getSkillsCatalogo(): List<SkillCatalogItemDto> {
        val response = apiService.getSkillsCatalogo()
        if (response.success && response.data != null) {
            // Aplanar la estructura agrupada a lista plana
            // El response.data es List<SkillCatalogGroupDto>
            return response.data.flatMap { group ->
                group.items.map { skillName ->
                    SkillCatalogItemDto(
                        nombre = skillName,
                        tipo = group.tipo
                    )
                }
            }
        } else {
            throw Exception(response.message ?: "Error al obtener el catálogo de skills")
        }
    }
}
