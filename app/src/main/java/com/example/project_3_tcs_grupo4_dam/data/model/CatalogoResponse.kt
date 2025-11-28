package com.example.project_3_tcs_grupo4_dam.data.model

data class CatalogoResponse(
    val _id: String,
    val areas: List<String>,
    val rolesLaborales: List<String>,
    val nivelesSkill: List<NivelSkillCatalogo>,
    val tiposSkill: List<String>,
    val additionalSections: Map<String, Any>
)

data class NivelSkillCatalogo(
    val codigo: Int,
    val descripcion: String
)