
package com.example.project_3_tcs_grupo4_dam.data.model

import com.google.gson.annotations.SerializedName

data class EvaluacionReadDto(

    @SerializedName("id")
    val id: String,

    @SerializedName("colaboradorId")
    val colaboradorId: String,

    @SerializedName("rolActual")
    val rolActual: String,

    @SerializedName("liderEvaluador")
    val liderEvaluador: String,

    @SerializedName("fechaEvaluacion")
    val fechaEvaluacion: String,   // "2025-03-15T00:00:00Z"

    @SerializedName("tipoEvaluacion")
    val tipoEvaluacion: String,    // ENTREVISTA, TECNICA, etc

    @SerializedName("skillsEvaluados")
    val skillsEvaluados: List<SkillEvaluadoDto>,  // lista anidada como en tu JSON

    @SerializedName("comentarios")
    val comentarios: String,

    @SerializedName("usuarioResponsable")
    val usuarioResponsable: String,

    @SerializedName("fechaCreacion")
    val fechaCreacion: String,

    @SerializedName("fechaActualizacion")
    val fechaActualizacion: String
)
