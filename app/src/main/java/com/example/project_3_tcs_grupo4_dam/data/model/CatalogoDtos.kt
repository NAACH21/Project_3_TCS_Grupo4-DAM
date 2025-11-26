package com.example.project_3_tcs_grupo4_dam.data.model

import com.google.gson.annotations.SerializedName

/**
 * DTOs para el módulo de Catálogo
 * Incluye opciones precargadas para áreas, roles laborales, niveles y tipos de skill
 */
object CatalogoDtos {

    /**
     * Representa un nivel de skill con su código y descripción
     * Ejemplo: { "codigo": 1, "descripcion": "No iniciado" }
     */
    data class NivelSkillDto(
        @SerializedName("codigo") val codigo: Int,
        @SerializedName("descripcion") val descripcion: String
    )

    /**
     * Grupo de skills por tipo desde el catálogo
     * Ejemplo: { "tipo": "TECNICO", "skills": [".NET", "C#", "SQL Server"] }
     */
    data class SkillCatalogGroupDto(
        @SerializedName("tipo") val tipo: String,
        @SerializedName("skills") val skills: List<String>
    )

    /**
     * Item individual de skill del catálogo (versión plana)
     * Usado para búsqueda y selección en la UI
     */
    data class SkillCatalogItemDto(
        val nombre: String,
        val tipo: String
    )

    /**
     * DTO de lectura del catálogo completo
     * Corresponde al GET /api/catalogo
     */
    data class CatalogoReadDto(
        @SerializedName("id") val id: String,
        @SerializedName("areas") val areas: List<String>,
        @SerializedName("rolesLaborales") val rolesLaborales: List<String>,
        @SerializedName("nivelesSkill") val nivelesSkill: List<NivelSkillDto>,
        @SerializedName("tiposSkill") val tiposSkill: List<String>
        // additionalSections se ignora por ahora
    )
}
