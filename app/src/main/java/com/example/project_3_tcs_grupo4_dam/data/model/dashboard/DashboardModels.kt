package com.example.project_3_tcs_grupo4_dam.data.model.dashboard

import com.google.gson.annotations.SerializedName

// 1. La respuesta envoltorio (Wrapper)
data class DashboardResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: DashboardData
)

// 2. El objeto principal de datos
data class DashboardData(
    @SerializedName("metricasVacantes") val metricasVacantes: MetricasVacantesDto?,
    @SerializedName("metricasMatching") val metricasMatching: MetricasMatchingDto?,
    @SerializedName("skillsMasDemandados") val skillsMasDemandados: List<SkillDemandadoDto>?,
    @SerializedName("brechasPrioritarias") val brechasPrioritarias: List<BrechaSkillDto>?,
    @SerializedName("fechaGeneracion") val fechaGeneracion: String?
)

// 3. Sub-objetos detallados
data class MetricasVacantesDto(
    val totalVacantes: Int = 0,
    val vacantesAbiertas: Int = 0,
    val vacantesCerradas: Int = 0,
    val tasaCobertura: Double = 0.0,
    // Usamos Map porque las claves son dinámicas ("ALTA", "Media", "Tecnología", etc.)
    val vacantesPorUrgencia: Map<String, Int>? = null,
    val vacantesPorArea: Map<String, Int>? = null
)

data class MetricasMatchingDto(
    val totalProcesosEjecutados: Int = 0,
    val promedioCandidatosPorVacante: Int = 0,
    val porcentajeMatchPromedio: Double = 0.0,
    val vacantesSinCandidatos: Int = 0
)

data class SkillDemandadoDto(
    val nombreSkill: String,
    val tipo: String, // "TECNICO", "BLANDO"
    val cantidadVacantes: Int,
    val nivelPromedioRequerido: Double
)

data class BrechaSkillDto(
    val nombreSkill: String,
    val nivelRequeridoPromedio: Double,
    val nivelActualPromedio: Double,
    val brechaPromedio: Double,
    val colaboradoresAfectados: Int
)