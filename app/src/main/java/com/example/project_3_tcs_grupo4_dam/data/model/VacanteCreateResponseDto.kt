package com.example.project_3_tcs_grupo4_dam.data.model

import com.google.gson.annotations.SerializedName

/**
 * DTO seguro y aislado, usado exclusivamente para capturar el ID 
 * de la respuesta del endpoint POST /api/vacantes.
 * Es flexible para leer "id", "_id" (formato Mongo) o "Id" (formato .NET).
 */
data class VacanteCreateResponseDto(
    @SerializedName("id", alternate = ["_id", "Id"])
    val id: String?
)
