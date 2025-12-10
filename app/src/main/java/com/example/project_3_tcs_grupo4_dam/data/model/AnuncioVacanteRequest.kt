package com.example.project_3_tcs_grupo4_dam.data.model

import com.google.gson.annotations.SerializedName

/**
 * Request para anunciar una vacante.
 * Se define estrictamente como String para asegurar que el JSON se envíe
 * con comillas, tal como lo hace Swagger.
 * Ejemplo JSON enviado: { "vacanteId": "675000..." }
 */
data class AnuncioVacanteRequest(
    @SerializedName("vacanteId")
    val vacanteId: String
)
