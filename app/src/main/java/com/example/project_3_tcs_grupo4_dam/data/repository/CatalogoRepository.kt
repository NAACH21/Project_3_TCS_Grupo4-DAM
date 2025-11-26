package com.example.project_3_tcs_grupo4_dam.data.repository

import com.example.project_3_tcs_grupo4_dam.data.model.CatalogoResponse
import com.example.project_3_tcs_grupo4_dam.data.remote.RetrofitClient
import com.example.project_3_tcs_grupo4_dam.data.model.CatalogoDtos.CatalogoReadDto
import com.example.project_3_tcs_grupo4_dam.data.model.CatalogoDtos.NivelSkillDto
import com.example.project_3_tcs_grupo4_dam.data.model.CatalogoDtos.SkillCatalogItemDto

/**
 * Repositorio para el módulo de Catálogo
 * Proporciona acceso a las opciones precargadas para áreas, roles, niveles y tipos de skill
 */
interface CatalogoRepository {

    /**
     * Obtiene el catálogo completo con todas las secciones
     */
    suspend fun getCatalogoCompleto(): CatalogoReadDto

    /**
     * Obtiene la lista de áreas disponibles
     */
    suspend fun getAreas(): List<String>

    /**
     * Obtiene la lista de roles laborales disponibles
     */
    suspend fun getRolesLaborales(): List<String>

class CatalogoRepository {
    private val api = RetrofitClient.catalogoApi
    suspend fun getCatalogo(): CatalogoResponse = api.getCatalogo()
    /**
     * Obtiene la lista de niveles de skill con su código y descripción
     */
    suspend fun getNivelesSkill(): List<NivelSkillDto>

    /**
     * Obtiene la lista de tipos de skill (TECNICO, BLANDO)
     */
    suspend fun getTiposSkill(): List<String>

    /**
     * Obtiene el catálogo de skills aplanado para búsqueda y selección
     * Convierte la estructura agrupada en una lista plana de items
     */
    suspend fun getSkillsCatalogo(): List<SkillCatalogItemDto>
}
