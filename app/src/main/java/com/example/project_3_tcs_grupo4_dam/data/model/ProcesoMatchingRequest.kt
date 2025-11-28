package com.example.project_3_tcs_grupo4_dam.data.model

import java.util.Date

data class ProcesoMatchingRequest(
    val vacanteId: String,
    val umbral: Int,
    val fechaEjecucion: Date = Date(),
    val resultados: List<ResultadoMatchingItem>
)

data class ProcesoMatchingResponse(
    val id: IdWrapper?,
    val vacanteId: IdWrapper?,
    val umbral: Int,
    val fechaEjecucion: Date?,
    val resultados: List<ResultadoMatchingItem>
)

data class ResultadoMatchingItem(
    val colaboradorId: String,
    val nombres: String,
    val apellidos: String,
    val puntaje: Double,
    val disponibleParaMovilidad: Boolean
)