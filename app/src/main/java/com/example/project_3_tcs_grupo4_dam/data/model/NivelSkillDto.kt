package com.example.project_3_tcs_grupo4_dam.data.model

import com.google.gson.annotations.SerializedName

data class NivelSkillDto(
    @SerializedName("id") val id: String,
    @SerializedName("codigo") val codigo: Int,
    @SerializedName("nombre") val nombre: String
)

