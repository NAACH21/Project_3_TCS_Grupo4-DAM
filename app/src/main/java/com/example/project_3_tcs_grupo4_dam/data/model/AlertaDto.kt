package com.example.project_3_tcs_grupo4_dam.data.model

import com.google.gson.annotations.SerializedName

data class AlertaDto(
    // Mapeamos _id que viene de Mongo
    @SerializedName("id", alternate = ["_id"]) 
    val id: String,

    @SerializedName("tipo") 
    val tipo: String,

    @SerializedName("estado") 
    val estado: String,

    // CORRECCIÓN: Cambiado a Any? para aceptar String "ID" o Map { "$oid": "ID" }
    @SerializedName("colaboradorId") 
    val colaboradorId: Any?,

    // Necesario para filtrar por usuarioId o Rol
    @SerializedName("destinatarios") 
    val destinatarios: List<DestinatarioDto>? = emptyList(),

    @SerializedName("detalle") 
    val detalle: Any?,
    
    // Campo agregado para mostrar la fecha en el detalle
    @SerializedName("fechaCreacion")
    val fechaCreacion: String? = null
)

data class DestinatarioDto(
    // CORRECCIÓN: Cambiado a Any? para aceptar String "ID" o Map { "$oid": "ID" }
    @SerializedName("usuarioId") 
    val usuarioId: Any?,

    @SerializedName("tipo") 
    val tipo: String?
)
