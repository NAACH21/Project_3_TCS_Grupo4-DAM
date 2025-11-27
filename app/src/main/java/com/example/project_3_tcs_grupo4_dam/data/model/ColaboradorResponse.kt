package com.example.project_3_tcs_grupo4_dam.data.model

import com.google.gson.annotations.SerializedName
import java.util.*

data class ColaboradorResponse(
    @SerializedName("_id") val _id: IdWrapper?,
    @SerializedName("id") val id: String?,
    val nombres: String,
    val apellidos: String,
    val correo: String?,
    val area: String,
    val rolLaboral: String?,
    val estado: String, // "ACTIVO" | "INACTIVO"
    val disponibleParaMovilidad: Boolean = false,
    val skills: List<SkillResponse> = emptyList(),
    val certificaciones: List<CertificacionResponse> = emptyList(),
    val fechaRegistro: Date? = null,
    val fechaActualizacion: Date? = null
) {
    fun getIdValue(): String = _id?.value() ?: id ?: ""
}

data class SkillResponse(
    val nombre: String,
    val tipo: String, // "TECNICO" | "BLANDO"
    val nivel: Int,
    val esCritico: Boolean = false
)

data class CertificacionResponse(
    val certificacionId: String?,
    val nombre: String,
    val institucion: String?,
    val fechaObtencion: Date?,
    val fechaVencimiento: Date?,
    val archivoPdfUrl: String?,
    val estado: String?,
    val fechaRegistro: Date?,
    val fechaActualizacion: Date?,
    val proximaEvaluacion: Date?
)
