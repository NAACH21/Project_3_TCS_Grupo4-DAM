package com.example.project_3_tcs_grupo4_dam.domain

import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorDtos.ColaboradorReadDto
import com.example.project_3_tcs_grupo4_dam.data.model.VacanteResponse
import com.example.project_3_tcs_grupo4_dam.data.model.ResultadoMatchingItem

object MatchingEngine {

    fun runMatching(
        colaboradores: List<ColaboradorReadDto>,
        vacante: VacanteResponse,
        umbralMinimo: Int
    ): List<ResultadoMatchingItem> {

        val reqSkills = vacante.skillsRequeridos
        val reqCerts = vacante.certificacionesRequeridas

        // Filtrar colaboradores por area y estado ACTIVO
        val colaboradoresFiltrados = colaboradores.filter {
            it.area.equals(vacante.area, true) && it.estado.equals("ACTIVO", true)
        }

        val resultados = colaboradoresFiltrados.map { col ->

            val colSkills = col.skills
            val colCerts = col.certificaciones.map { it.nombre }

            // =============================
            // 1. CALCULAR PUNTAJE DE SKILLS
            // =============================
            val puntajesSkills = reqSkills.map { req ->
                val colSkill = colSkills.firstOrNull { it.nombre.equals(req.nombre, true) }

                when {
                    colSkill == null -> 0.0
                    colSkill.nivel >= req.nivelDeseado -> 1.0
                    colSkill.nivel == req.nivelDeseado - 1 -> 0.75
                    colSkill.nivel < req.nivelDeseado - 1 && colSkill.nivel > 0 -> 0.25
                    else -> 0.0
                }
            }

            val promedioSkills = if (puntajesSkills.isNotEmpty()) puntajesSkills.average() else 0.0

            // =====================================
            // 2. CERTIFICACIONES (1 SI TIENE, 0 NO)
            // =====================================
            val puntajesCerts = reqCerts.map { certReq ->
                if (colCerts.any { it.equals(certReq, true) }) 1.0 else 0.0
            }

            val promedioCerts = if (puntajesCerts.isNotEmpty()) puntajesCerts.average() else 0.0

            // =====================================
            // 3. SCORE FINAL
            // =====================================
            val scoreFinal = ((promedioSkills + promedioCerts) / 2.0) * 100.0

            ResultadoMatchingItem(
                colaboradorId = col.id,
                nombres = col.nombres,
                apellidos = col.apellidos,
                puntaje = scoreFinal,
                disponibleParaMovilidad = col.disponibleParaMovilidad
            )
        }

        // Filtrar por umbral y ordenar por puntaje desc y disponibilidad inmediata (true primero)
        return resultados
            .filter { it.puntaje >= umbralMinimo }
            .sortedWith(compareByDescending<ResultadoMatchingItem> { it.puntaje }
                .thenByDescending { it.disponibleParaMovilidad })
    }
}
