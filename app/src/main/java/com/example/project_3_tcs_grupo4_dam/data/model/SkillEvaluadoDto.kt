package com.example.project_3_tcs_grupo4_dam.data.model

import com.google.gson.annotations.SerializedName

data class SkillEvaluadoDto(

    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("tipo")
    val tipo: String,            // TECNICO / BLANDO

    @SerializedName("nivelActual")
    val nivelActual: Int,

    @SerializedName("nivelRecomendado")
    val nivelRecomendado: Int
)

fun SkillEvaluadoDto.toSkillEvaluado(): SkillEvaluado {
    return SkillEvaluado(
        nombre = this.nombre,
        tipo = this.tipo,
        nivelActual = this.nivelActual,
        nivelRecomendado = this.nivelRecomendado
    )
}