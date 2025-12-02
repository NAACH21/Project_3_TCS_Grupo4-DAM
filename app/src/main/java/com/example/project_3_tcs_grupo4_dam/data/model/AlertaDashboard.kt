package com.example.project_3_tcs_grupo4_dam.data.model

import com.google.gson.annotations.SerializedName

/**
 * DTO simplificado para el Dashboard de Notificaciones
 * Este modelo NO reemplaza AlertaDto, es complementario para la UI
 */
data class AlertaDashboard(
    @SerializedName("idReferencia")
    val idReferencia: String,

    @SerializedName("titulo")
    val titulo: String,

    @SerializedName("mensaje")
    val mensaje: String,

    @SerializedName("fecha")
    val fecha: String,

    @SerializedName("tipoOrigen")
    val tipoOrigen: TipoOrigenAlerta,

    @SerializedName("activa")
    val activa: Boolean,

    @SerializedName("colorPrioridad")
    val colorPrioridad: ColorPrioridad
)

/**
 * Enum para tipos de origen de alertas
 */
enum class TipoOrigenAlerta {
    @SerializedName("SKILL_GAP")
    SKILL_GAP,

    @SerializedName("CERTIFICACION")
    CERTIFICACION,

    @SerializedName("GENERICA")
    GENERICA,

    @SerializedName("VACANTE_DISPONIBLE")
    VACANTE_DISPONIBLE
}

/**
 * Enum para colores de prioridad
 */
enum class ColorPrioridad {
    @SerializedName("ROJO")
    ROJO,

    @SerializedName("AMARILLO")
    AMARILLO,

    @SerializedName("VERDE")
    VERDE
}

