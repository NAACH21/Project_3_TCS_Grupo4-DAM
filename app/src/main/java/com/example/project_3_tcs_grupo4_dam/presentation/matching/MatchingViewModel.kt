package com.example.project_3_tcs_grupo4_dam.presentation.matching

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

// Datos del dominio mock

data class SkillRequerido(
    val nombre: String,
    val nivel: String
)

data class SkillColaborador(
    val nombre: String,
    val nivel: String
)

data class Candidato(
    val id: String,
    val nombre: String,
    val disponibilidadInmediata: Boolean,
    val skills: List<SkillColaborador>
)

data class ResultadoMatch(
    val candidato: Candidato,
    val porcentaje: Float
)

enum class SortBy { MATCH, DISPONIBILIDAD }

class MatchingViewModel : ViewModel() {

    // Variables MOCK solicitadas
    val vacantesMock = listOf(
        "Vacante QA Tester",
        "Vacante Desarrollador Backend",
        "Vacante Data Engineer",
        "Vacante Analista de Talento"
    )

    val nivelesSkill = listOf("Básico", "Intermedio", "Avanzado", "Experto")

    var selectedVacante by mutableStateOf("")
    var umbralMatch by mutableFloatStateOf(60f)

    var listaSkills = mutableStateListOf(
        SkillRequerido(nombre = "", nivel = nivelesSkill.first()) // skill inicial de guía
    )

    // Estado auxiliar
    var loading by mutableStateOf(false)
    var mensajeSistema by mutableStateOf<String?>(null)
    var resultados = mutableStateListOf<ResultadoMatch>()

    // Criterio de ordenamiento (lógica lista; UI se implementará luego)
    var sortBy by mutableStateOf(SortBy.MATCH)

    fun agregarSkill() {
        listaSkills.add(SkillRequerido(nombre = "", nivel = nivelesSkill.first()))
    }

    fun eliminarSkill(index: Int) {
        if (index in listaSkills.indices) listaSkills.removeAt(index)
    }

    fun actualizarSkillNombre(index: Int, nombre: String) {
        if (index in listaSkills.indices) {
            val actual = listaSkills[index]
            listaSkills[index] = actual.copy(nombre = nombre)
        }
    }

    fun actualizarSkillNivel(index: Int, nivel: String) {
        if (index in listaSkills.indices) {
            val actual = listaSkills[index]
            listaSkills[index] = actual.copy(nivel = nivel)
        }
    }

    // Mapeo de nivel textual -> numérico (1..4)
    private fun nivelNumerico(nivel: String): Int = when (nivel.trim()) {
        "Básico" -> 1
        "Intermedio" -> 2
        "Avanzado" -> 3
        "Experto" -> 4
        else -> 0
    }

    // STUB: calcular el match por colaborador respecto a los skills requeridos
    fun calcularMatchColaborador(
        skillsRequeridos: List<SkillRequerido>,
        skillsColaborador: List<SkillColaborador>
    ): Float {
        // Filtrar skills sin nombre para no alterar el cálculo
        val filtrados = skillsRequeridos.filter { it.nombre.isNotBlank() }
        if (filtrados.isEmpty()) return 0f
        val mapaColab = skillsColaborador.associateBy { it.nombre.trim().lowercase() }

        var suma = 0f
        var count = 0
        for (req in filtrados) {
            val reqNivel = nivelNumerico(req.nivel)
            val colab = mapaColab[req.nombre.trim().lowercase()]
            val puntaje = if (colab == null) {
                0f // No posee la habilidad
            } else {
                val colabNivel = nivelNumerico(colab.nivel)
                when {
                    colabNivel >= reqNivel -> 1.0f
                    colabNivel == reqNivel - 1 -> 0.75f
                    colabNivel < reqNivel - 1 -> 0.25f
                    else -> 0f
                }
            }
            suma += puntaje
            count++
        }
        return (suma / count) * 100f
    }

    // STUB: obtener candidatos (mock temporal)
    fun obtenerCandidatos(): List<Candidato> {
        return listOf(
            Candidato(
                id = "1",
                nombre = "Ana Pérez",
                disponibilidadInmediata = true,
                skills = listOf(
                    SkillColaborador("Testing", "Avanzado"),
                    SkillColaborador("Automatización", "Intermedio"),
                    SkillColaborador("Kotlin", "Intermedio"),
                    SkillColaborador("SQL", "Básico")
                )
            ),
            Candidato(
                id = "2",
                nombre = "Luis Gómez",
                disponibilidadInmediata = false,
                skills = listOf(
                    SkillColaborador("Kotlin", "Experto"),
                    SkillColaborador("Spring", "Avanzado"),
                    SkillColaborador("Docker", "Intermedio"),
                    SkillColaborador("SQL", "Intermedio")
                )
            ),
            Candidato(
                id = "3",
                nombre = "María Rojas",
                disponibilidadInmediata = true,
                skills = listOf(
                    SkillColaborador("Data Engineering", "Avanzado"),
                    SkillColaborador("Python", "Experto"),
                    SkillColaborador("Spark", "Intermedio"),
                    SkillColaborador("SQL", "Avanzado")
                )
            )
        )
    }

    // STUB: ejecutar matching usando la fórmula y el umbral
    fun ejecutarMatchingMock(): List<String> {

        if (selectedVacante.isEmpty()) {
            mensajeSistema = "Debe seleccionar una vacante."
            return emptyList()
        }

        if (listaSkills.isEmpty()) {
            mensajeSistema = "Debe agregar al menos un skill técnico."
            return emptyList()
        }

        // Resultados de prueba
        val resultados = listOf(
            "Carlos Ramírez – Backend – Match 92%",
            "Valeria Soto – QA – Match 85%",
            "Miguel Torres – Data Engineer – Match 70%"
        )

        mensajeSistema = "Matching ejecutado correctamente."
        return resultados
    }

}
