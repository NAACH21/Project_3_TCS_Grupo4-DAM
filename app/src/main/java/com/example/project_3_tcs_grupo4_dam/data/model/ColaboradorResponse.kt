package com.example.project_3_tcs_grupo4_dam.data.model

import com.google.gson.annotations.SerializedName
import java.util.*

data class ColaboradorResponse(
    @SerializedName("_id") val id: IdWrapper?,
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
)

data class IdWrapper(val `$oid`: String)
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
