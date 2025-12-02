package com.example.project_3_tcs_grupo4_dam.data.model

import com.google.gson.annotations.SerializedName

/**
 * Request para anunciar una vacante disponible
 */
data class AnuncioVacanteRequest(
    @SerializedName("vacanteId")
    val vacanteId: String
)

