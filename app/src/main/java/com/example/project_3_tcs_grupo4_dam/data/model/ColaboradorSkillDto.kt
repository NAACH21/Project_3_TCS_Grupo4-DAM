package com.example.project_3_tcs_grupo4_dam.data.model

import com.google.gson.annotations.SerializedName

data class ColaboradorSkillDto(
    @SerializedName("nombre") val nombre: String, // Clave natural
    @SerializedName("tipo") val tipo: String,     // "TECNICO", "BLANDO"
    @SerializedName("nivel") val nivel: Int,      // 1, 2, 3, 4
    @SerializedName("estado") val estado: String?, // "APROBADO", "PENDIENTE", etc. (Puede ser null en BD antigua)
    @SerializedName("evidenciaUrl") val evidenciaUrl: String?
)

data class UpdateColaboradorSkillDto(
    @SerializedName("nivel") val nivel: Int,
    @SerializedName("evidenciaUrl") val evidenciaUrl: String?
)

data class CreateColaboradorSkillDto(
    @SerializedName("nombre") val nombre: String,
    @SerializedName("tipo") val tipo: String,
    @SerializedName("nivel") val nivel: Int,
    @SerializedName("evidenciaUrl") val evidenciaUrl: String?
)
