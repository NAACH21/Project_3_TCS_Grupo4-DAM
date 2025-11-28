package com.example.project_3_tcs_grupo4_dam.data.model

import com.google.gson.annotations.SerializedName

data class SkillEvaluadoCreateDto(
    @SerializedName("nombre") val nombre: String,
    @SerializedName("tipo") val tipo: String,
    @SerializedName("nivelActual") val nivelActual: Int,
    @SerializedName("nivelRecomendado") val nivelRecomendado: Int
)