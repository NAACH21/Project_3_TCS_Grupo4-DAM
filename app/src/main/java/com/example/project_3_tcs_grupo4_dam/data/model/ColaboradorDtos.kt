package com.example.project_3_tcs_grupo4_dam.data.model

import com.google.gson.annotations.SerializedName

/**
 * DTOs para COLABORADOR (crear, leer, actualizar)
 */
object ColaboradorDtos {

    // ===============================
    // DTOs para SKILLS
    // ===============================

    data class SkillCreateDto(
        @SerializedName("nombre")
        val nombre: String,

        @SerializedName("tipo") // "TECNICO" o "BLANDO"
        val tipo: String,

        @SerializedName("nivel") // 1-4
        val nivel: Int,

        @SerializedName("esCritico")
        val esCritico: Boolean
    )

    data class SkillReadDto(
        @SerializedName("nombre")
        val nombre: String,

        @SerializedName("tipo")
        val tipo: String,

        @SerializedName("nivel")
        val nivel: Int,

        @SerializedName("esCritico")
        val esCritico: Boolean
    )

    // ===============================
    // DTOs para CERTIFICACIONES
    // ===============================

    data class CertificacionCreateDto(
        @SerializedName("nombre")
        val nombre: String,

        @SerializedName("institucion")
        val institucion: String,

        @SerializedName("fechaObtencion")
        val fechaObtencion: String?,   // ISO string (ej: "2025-01-10T00:00:00Z")

        @SerializedName("fechaVencimiento")
        val fechaVencimiento: String?,

        @SerializedName("archivoPdfUrl")
        val archivoPdfUrl: String?
        // Estado y fechas de auditoría NO se envían, se setean en backend
    )

    data class CertificacionReadDto(
        @SerializedName("certificacionId")
        val certificacionId: String?,

        @SerializedName("nombre")
        val nombre: String,

        @SerializedName("institucion")
        val institucion: String,

        @SerializedName("fechaObtencion")
        val fechaObtencion: String?,

        @SerializedName("fechaVencimiento")
        val fechaVencimiento: String?,

        @SerializedName("archivoPdfUrl")
        val archivoPdfUrl: String?,

        @SerializedName("estado")
        val estado: String,

        @SerializedName("fechaRegistro")
        val fechaRegistro: String?,

        @SerializedName("fechaActualizacion")
        val fechaActualizacion: String?,

        @SerializedName("proximaEvaluacion")
        val proximaEvaluacion: String?
    )

    // ===============================
    // DTOs para DISPONIBILIDAD
    // ===============================
    data class DisponibilidadDto(
        @SerializedName("estado") val estado: String,
        @SerializedName("dias") val dias: Int
    )

    // ===============================
    // DTOs para COLABORADOR
    // ===============================

    data class ColaboradorCreateDto(
        @SerializedName("nombres")
        val nombres: String,

        @SerializedName("apellidos")
        val apellidos: String,

        @SerializedName("correo")
        val correo: String,

        @SerializedName("area")
        val area: String,

        @SerializedName("rolLaboral")
        val rolLaboral: String,

        @SerializedName("disponibleParaMovilidad")
        val disponibleParaMovilidad: Boolean,

        // Skills embebidos
        @SerializedName("skills")
        val skills: List<SkillCreateDto>,

        // Certificaciones embebidas
        @SerializedName("certificaciones")
        val certificaciones: List<CertificacionCreateDto>
        // Estado se setea en backend como "ACTIVO"
    )

    data class ColaboradorUpdateDto(
        @SerializedName("nombres")
        val nombres: String,

        @SerializedName("apellidos")
        val apellidos: String,

        @SerializedName("correo")
        val correo: String,

        @SerializedName("area")
        val area: String,

        @SerializedName("rolLaboral")
        val rolLaboral: String,

        @SerializedName("disponibleParaMovilidad")
        val disponibleParaMovilidad: Boolean,

        // Opcional: permitir cambiar el estado desde UI
        @SerializedName("estado")
        val estado: String?,

        @SerializedName("skills")
        val skills: List<SkillCreateDto>,

        @SerializedName("certificaciones")
        val certificaciones: List<CertificacionCreateDto>
    )

    data class ColaboradorReadDto(
        @SerializedName("id")
        val id: String,

        @SerializedName("nombres")
        val nombres: String,

        @SerializedName("apellidos")
        val apellidos: String,

        @SerializedName("correo")
        val correo: String,

        @SerializedName("area")
        val area: String,

        @SerializedName("rolLaboral")
        val rolLaboral: String,

        @SerializedName("estado")
        val estado: String,

        @SerializedName("disponibleParaMovilidad")
        val disponibleParaMovilidad: Boolean,

        @SerializedName("skills")
        val skills: List<SkillReadDto>,

        @SerializedName("certificaciones")
        val certificaciones: List<CertificacionReadDto>,

        @SerializedName("fechaRegistro")
        val fechaRegistro: String?,

        @SerializedName("fechaActualizacion")
        val fechaActualizacion: String?,

        // Añadir aquí la disponibilidad si es parte del ReadDto
        // @SerializedName("disponibilidad")
        // val disponibilidad: DisponibilidadDto? // Ejemplo
    )
}