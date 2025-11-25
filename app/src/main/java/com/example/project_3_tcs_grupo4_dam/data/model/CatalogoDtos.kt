package com.example.project_3_tcs_grupo4_dam.data.model

import com.google.gson.annotations.SerializedName

/**
 * DTOs para manejar el catálogo de skills y certificaciones
 */
data class CatalogoReadDto(
    @SerializedName("id") val id: String,
    @SerializedName("areas") val areas: List<String>,
    @SerializedName("rolesLaborales") val rolesLaborales: List<String>,
    @SerializedName("nivelesSkill") val nivelesSkill: List<NivelSkillDto>,
    @SerializedName("tiposSkill") val tiposSkill: List<String>,
    @SerializedName("skillsCatalogo") val skillsCatalogo: List<SkillCatalogGroupDto>
)

data class NivelSkillDto(
    @SerializedName("nivel") val nivel: Int,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("descripcion") val descripcion: String
)

data class SkillCatalogGroupDto(
    @SerializedName("tipo") val tipo: String,
    @SerializedName("items") val items: List<String>
)

// DTO para la lista aplanada usada en la UI (nombre + tipo)
data class SkillCatalogItemDto(
    val nombre: String,
    val tipo: String
)
