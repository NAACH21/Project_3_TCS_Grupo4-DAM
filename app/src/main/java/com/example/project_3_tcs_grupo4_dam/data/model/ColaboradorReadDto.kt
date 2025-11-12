package com.example.project_3_tcs_grupo4_dam.data.model

import com.google.gson.annotations.SerializedName

data class ColaboradorReadDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("nombres")
    val nombres: String,

    @SerializedName("apellidos")
    val apellidos: String,

    @SerializedName("area")
    val area: String,

    @SerializedName("rolActual")
    val rolActual: String,

    @SerializedName("skillPrimario")
    val skillPrimario: String,

    @SerializedName("skillSecundario")
    val skillSecundario: String,

    @SerializedName("nivelDominio")
    val nivelDominio: Int,

    @SerializedName("certificaciones")
    val certificaciones: List<String>,

    @SerializedName("disponibilidad")
    val disponibilidad: String,

    @SerializedName("diasDisponibilidad")
    val diasDisponibilidad: Int?
)