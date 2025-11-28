package com.example.project_3_tcs_grupo4_dam.presentation.matching

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList

/**
 * Contrato ligero que expone el subset de estado y acciones que la UI del Matching necesita.
 * Permite pasar un ViewModel real o una implementación local (para previews o uso aislado).
 */
interface MatchingUiContract {
    var selectedVacante: String
    val vacantesMock: List<String>
    var umbralMatch: Float
    val listaSkills: SnapshotStateList<UiSkill>
    val nivelesSkill: List<String>
    var mensajeSistema: String?

    // Lista de skills disponibles en el catálogo (planas)
    val availableSkills: List<String>

    fun agregarSkill()
    fun actualizarSkillNombre(index: Int, nombre: String)
    fun actualizarSkillNivel(index: Int, nivel: String)
    fun eliminarSkill(index: Int)
    fun ejecutarMatchingMock(): List<com.example.project_3_tcs_grupo4_dam.data.model.ResultadoMatchingItem>
}

// Reutilizable por VM y por la implementación local
data class UiSkill(var nombre: String = "", var nivel: String = "1")
