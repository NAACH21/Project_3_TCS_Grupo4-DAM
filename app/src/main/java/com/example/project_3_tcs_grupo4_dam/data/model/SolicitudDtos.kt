package com.example.project_3_tcs_grupo4_dam.data.model

import com.google.gson.annotations.SerializedName

// ========== DTOs para Certificaciones Propuestas ==========

data class CertificacionPropuestaCreateDto(
    @SerializedName("nombre") val nombre: String,
    @SerializedName("institucion") val institucion: String,
    @SerializedName("fechaObtencion") val fechaObtencion: String?,
    @SerializedName("fechaVencimiento") val fechaVencimiento: String?,
    @SerializedName("archivoPdfUrl") val archivoPdfUrl: String?
)

data class CertificacionPropuestaReadDto(
    @SerializedName("nombre") val nombre: String,
    @SerializedName("institucion") val institucion: String,
    @SerializedName("fechaObtencion") val fechaObtencion: String?,
    @SerializedName("fechaVencimiento") val fechaVencimiento: String?,
    @SerializedName("archivoPdfUrl") val archivoPdfUrl: String?
)

// ========== DTOs para Entrevistas de Desempeño ==========

data class DatosEntrevistaPropuestaCreateDto(
    @SerializedName("motivo") val motivo: String,
    @SerializedName("periodo") val periodo: String,
    @SerializedName("fechaSugerida") val fechaSugerida: String?,
    @SerializedName("propuestoPorUsuarioId") val propuestoPorUsuarioId: String?
)

data class DatosEntrevistaPropuestaReadDto(
    @SerializedName("motivo") val motivo: String,
    @SerializedName("periodo") val periodo: String,
    @SerializedName("fechaSugerida") val fechaSugerida: String?,
    @SerializedName("propuestoPorUsuarioId") val propuestoPorUsuarioId: String
)

// ========== DTOs para Cambios de Skills ==========

data class CambioSkillPropuestaCreateDto(
    @SerializedName("nombre") val nombre: String,
    @SerializedName("tipo") val tipo: String,
    @SerializedName("nivelActual") val nivelActual: Int?,
    @SerializedName("nivelPropuesto") val nivelPropuesto: Int,
    @SerializedName("esCriticoActual") val esCriticoActual: Boolean?,
    @SerializedName("esCriticoPropuesto") val esCriticoPropuesto: Boolean,
    @SerializedName("motivo") val motivo: String?
)

data class CambioSkillPropuestaReadDto(
    @SerializedName("nombre") val nombre: String,
    @SerializedName("tipo") val tipo: String,
    @SerializedName("nivelActual") val nivelActual: Int?,
    @SerializedName("nivelPropuesto") val nivelPropuesto: Int,
    @SerializedName("esCriticoActual") val esCriticoActual: Boolean?,
    @SerializedName("esCriticoPropuesto") val esCriticoPropuesto: Boolean,
    @SerializedName("motivo") val motivo: String?
)

// ========== DTOs para Solicitudes ==========

data class SolicitudCreateDto(
    @SerializedName("tipoSolicitudGeneral") val tipoSolicitudGeneral: String,
    @SerializedName("tipoSolicitud") val tipoSolicitud: String,
    @SerializedName("colaboradorId") val colaboradorId: String,
    @SerializedName("certificacionIdAnterior") val certificacionIdAnterior: String?,
    @SerializedName("certificacionPropuesta") val certificacionPropuesta: CertificacionPropuestaCreateDto?,
    @SerializedName("datosEntrevistaPropuesta") val datosEntrevistaPropuesta: DatosEntrevistaPropuestaCreateDto?,
    @SerializedName("cambiosSkillsPropuestos") val cambiosSkillsPropuestos: List<CambioSkillPropuestaCreateDto>?,
    @SerializedName("creadoPorUsuarioId") val creadoPorUsuarioId: String?
)

data class SolicitudReadDto(
    @SerializedName("id") val id: String,
    @SerializedName("tipoSolicitudGeneral") val tipoSolicitudGeneral: String,
    @SerializedName("tipoSolicitud") val tipoSolicitud: String,
    @SerializedName("colaboradorId") val colaboradorId: String,
    @SerializedName("certificacionIdAnterior") val certificacionIdAnterior: String?,
    @SerializedName("certificacionPropuesta") val certificacionPropuesta: CertificacionPropuestaReadDto?,
    @SerializedName("datosEntrevistaPropuesta") val datosEntrevistaPropuesta: DatosEntrevistaPropuestaReadDto?,
    @SerializedName("cambiosSkillsPropuestos") val cambiosSkillsPropuestos: List<CambioSkillPropuestaReadDto>?,
    @SerializedName("estadoSolicitud") val estadoSolicitud: String,
    @SerializedName("observacionAdmin") val observacionAdmin: String?,
    @SerializedName("creadoPorUsuarioId") val creadoPorUsuarioId: String,
    @SerializedName("revisadoPorUsuarioId") val revisadoPorUsuarioId: String?,
    @SerializedName("fechaCreacion") val fechaCreacion: String,
    @SerializedName("fechaRevision") val fechaRevision: String?
)

data class SolicitudUpdateEstadoDto(
    @SerializedName("estadoSolicitud") val estadoSolicitud: String,
    @SerializedName("observacionAdmin") val observacionAdmin: String?,
    @SerializedName("revisadoPorUsuarioId") val revisadoPorUsuarioId: String?
)

// ========== DTO para respuesta de eliminación ==========

data class DeleteSolicitudResponse(
    @SerializedName("message") val message: String
)
