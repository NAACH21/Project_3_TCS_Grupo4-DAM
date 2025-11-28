package com.example.project_3_tcs_grupo4_dam.presentation.matching
import com.example.project_3_tcs_grupo4_dam.data.model.*
import kotlin.math.round

object MatchingEngine {

    /**
     * Calcula el puntaje para un colaborador vs una vacante.
     * Devuelve value en 0..100 (porcentaje)
     */
    fun calculateMatchPercentage(colaborador: ColaboradorResponse, vacante: VacanteResponse): Double {
        val vacSkillsTecnicos = vacante.skillsRequeridos.filter { it.tipo.equals("TECNICO", true) }

        val n = vacSkillsTecnicos.size
        val m = vacante.certificacionesRequeridas.size

        // Puntaje skills
        var sumaSkills = 0.0
        for (req in vacSkillsTecnicos) {
            val skillCol = colaborador.skills.firstOrNull { it.nombre.equals(req.nombre, true) && it.tipo.equals("TECNICO", true) }
            val nivelCol = skillCol?.nivel ?: 0
            val nivelReq = req.nivelDeseado

            val puntaje = when {
                nivelCol >= nivelReq -> 1.0
                nivelCol == nivelReq - 1 -> 0.75
                nivelCol in 1 until (nivelReq - 1) -> 0.25
                else -> 0.0
            }
            sumaSkills += puntaje
        }
        val puntajeSkills = if (n > 0) sumaSkills / n else 1.0 // si no hay skills técnicos requeridos -> 100% en skills

        // Puntaje certificaciones
        var sumaCerts = 0.0
        for (reqCert in vacante.certificacionesRequeridas) {
            val tiene = colaborador.certificaciones.any { it.nombre.equals(reqCert, true) && (it.estado == null || it.estado.equals("VIGENTE", true)) }
            sumaCerts += if (tiene) 1.0 else 0.0
        }
        val puntajeCerts = if (m > 0) sumaCerts / m else 1.0 // si no hay certificaciones requeridas -> 100%

        // Puntaje final: promedio de ambos * 100
        val final = ((puntajeSkills + puntajeCerts) / 2.0) * 100.0

        // redondear a 2 decimales
        return (round(final * 100) / 100.0) // ej. 87.5 -> 87.5
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
        val candidatos = colaboradores
            .filter { it.area.equals(vacante.area, true) }
            .filter { it.estado.equals("ACTIVO", true) }

        val resultados = candidatos.map { c ->
            val puntaje = calculateMatchPercentage(c, vacante)
            ResultadoMatchingItem(
                colaboradorId = c.getIdValue(),
                nombres = c.nombres,
                apellidos = c.apellidos,
                puntaje = puntaje,
                disponibleParaMovilidad = c.disponibleParaMovilidad
            )
        }.filter { it.puntaje >= umbralPercent.toDouble() }
            .sortedWith(compareByDescending<ResultadoMatchingItem> { it.puntaje }
                .thenByDescending { it.disponibleParaMovilidad })

        return resultados
    }
}