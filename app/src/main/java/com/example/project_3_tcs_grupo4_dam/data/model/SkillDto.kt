// ...existing code...
package com.example.project_3_tcs_grupo4_dam.data.model

import com.google.gson.annotations.SerializedName

/**
 * DTO plano para representar una skill recuperada desde /api/skills
 * Se coloca fuera de los objetos ColaboradorDtos para su reuso en listados generales.
 */
data class SkillDto(
    @SerializedName("id") val id: String,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("tipo") val tipo: String
)
// ...existing code...

