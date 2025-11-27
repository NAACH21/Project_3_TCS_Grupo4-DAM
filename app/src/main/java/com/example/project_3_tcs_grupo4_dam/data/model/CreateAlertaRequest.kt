package com.example.project_3_tcs_grupo4_dam.data.model

import com.google.gson.annotations.SerializedName

data class CreateAlertaRequest(
    @SerializedName("tipo")
    val tipo: String,

    @SerializedName("estado")
    val estado: String = "pendiente",

    @SerializedName("colaboradorId")
    val colaboradorId: String? = null,

    @SerializedName("vacanteId")
    val vacanteId: String? = null,

    @SerializedName("procesoMatchingId")
    val procesoMatchingId: String? = null,

    @SerializedName("detalle")
    val detalle: DetalleAlertaRequest? = null,

    @SerializedName("destinatarios")
    val destinatarios: List<DestinatarioAlertaRequest> = emptyList(),

    @SerializedName("usuarioResponsable")
    val usuarioResponsable: String? = null
)

data class DetalleAlertaRequest(
    @SerializedName("descripcion")
    val descripcion: String? = null
)

data class DestinatarioAlertaRequest(
    @SerializedName("usuarioId")
    val usuarioId: String? = null,

    @SerializedName("tipo")
    val tipo: String? = null
)

