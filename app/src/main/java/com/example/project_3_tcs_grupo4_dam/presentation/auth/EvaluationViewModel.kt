package com.example.project_3_tcs_grupo4_dam.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

// Data class to represent the state of a single skill
data class Skill(
    val id: UUID = UUID.randomUUID(),
    val name: String = "",
    val type: String = "Técnico", // "Técnico" or "Soft"
    val currentLevel: String = "Básico",
    val recommendedLevel: String = "Básico",
    val comments: String = ""
)

// Data class to represent the entire UI state
data class EvaluationUiState(
    val collaborator: String = "",
    val currentRole: String = "Android Developer", // This would be derived from the collaborator
    val evaluatorLeader: String = "",
    val evaluationDate: String = "",
    val evaluationType: String = "",
    val skills: List<Skill> = listOf(Skill()),
    val collaboratorOptions: List<String> = listOf("Juan Pérez", "Ana García", "Luis Rodríguez"),
    val evaluationTypeOptions: List<String> = listOf("Trimestral", "Semestral", "Anual", "Feedback"),
    val skillTypeOptions: List<String> = listOf("Técnico", "Soft"),
    val skillLevelOptions: List<String> = listOf("Básico", "Intermedio", "Avanzado")
)

class EvaluationViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(EvaluationUiState())
    val uiState: StateFlow<EvaluationUiState> = _uiState.asStateFlow()

    fun onCollaboratorChange(newValue: String) {
        _uiState.update { it.copy(collaborator = newValue) }
        // Here you would typically fetch the collaborator's role
    }

    fun onEvaluatorLeaderChange(newValue: String) {
        _uiState.update { it.copy(evaluatorLeader = newValue) }
    }

    fun onDateChange(newValue: String) {
        _uiState.update { it.copy(evaluationDate = newValue) }
    }

    fun onEvaluationTypeChange(newValue: String) {
        _uiState.update { it.copy(evaluationType = newValue) }
    }

    fun addSkill() {
        viewModelScope.launch {
            val newSkills = _uiState.value.skills.toMutableList().apply {
                add(Skill())
            }
            _uiState.update { it.copy(skills = newSkills) }
        }
    }

    fun removeSkill(skillId: UUID) {
        viewModelScope.launch {
            val updatedSkills = _uiState.value.skills.filterNot { it.id == skillId }
            _uiState.update { it.copy(skills = updatedSkills) }
        }
    }

    fun onSkillChange(skillId: UUID, update: (Skill) -> Skill) {
        viewModelScope.launch {
            val updatedSkills = _uiState.value.skills.map {
                if (it.id == skillId) {
                    update(it)
                } else {
                    it
                }
            }
            _uiState.update { it.copy(skills = updatedSkills) }
        }
    }

    fun saveEvaluation() {
        // Handle the logic to save the evaluation data
    }

    fun cancelEvaluation() {
        // Handle the logic to cancel the operation, e.g., navigate back
    }
}
