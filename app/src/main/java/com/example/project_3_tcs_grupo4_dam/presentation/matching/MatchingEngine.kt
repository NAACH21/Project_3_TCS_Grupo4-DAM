package com.example.project_3_tcs_grupo4_dam.presentation.matching

import com.example.project_3_tcs_grupo4_dam.data.model.*

/**
 * Wrapper que delega al engine central en el paquete `domain`.
 */
object MatchingEngine {
    /**
     * Calcula el puntaje para un colaborador vs una vacante.
     * Devuelve value en 0..100 (porcentaje)
     */
    fun calculateMatchPercentage(colaborador: ColaboradorResponse, vacante: VacanteResponse): Double {
        return com.example.project_3_tcs_grupo4_dam.domain.MatchingEngine.calculateMatchPercentage(colaborador, vacante)
    }

    /**
     * Ejecuta matching sobre una lista de colaboradores y devuelve lista de resultados
     * Filtra por área y estado ACTIVO antes de calcular.
     */
    fun runMatching(
        colaboradores: List<ColaboradorResponse>,
        vacante: VacanteResponse,
        umbralPercent: Int
    ): List<ResultadoMatchingItem> {
        return com.example.project_3_tcs_grupo4_dam.domain.MatchingEngine.runMatching(colaboradores, vacante, umbralPercent)
    }
}