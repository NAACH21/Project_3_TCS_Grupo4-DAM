package com.example.project_3_tcs_grupo4_dam.presentation.evaluaciones

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class EvaluationHistoryItem(
    val id: String,
    val collaboratorName: String,
    val role: String,
    val level: String,
    val evaluationType: String,
    val date: String,
    val skills: List<String>,
    val loadedBy: String
)

data class EvaluationsHistoryUiState(
    val searchQuery: String = "",
    val selectedType: String = "Todos los tipos",
    val evaluationTypes: List<String> = listOf("Todos los tipos", "Autoevaluación", "Evaluación de desempeño", "Evaluación de pares"),
    val evaluations: List<EvaluationHistoryItem> = emptyList(),
    val filteredEvaluations: List<EvaluationHistoryItem> = emptyList()
)

class EvaluationsHistoryViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(EvaluationsHistoryUiState())
    val uiState = _uiState.asStateFlow()

    private val allEvaluations = listOf(
        EvaluationHistoryItem("1", "Ana García", "Analista BI", "Avanzado", "Evaluación de desempeño", "2023-10-15", listOf("Power BI", "SQL", "Python"), "Carlos Ruiz"),
        EvaluationHistoryItem("2", "Juan Pérez", "Desarrollador Senior", "Intermedio", "Autoevaluación", "2023-11-02", listOf("Kotlin", "Jetpack Compose", "Coroutines"), "Juan Pérez"),
        EvaluationHistoryItem("3", "María López", "Diseñadora UX", "Básico", "Evaluación de pares", "2023-09-20", listOf("Figma", "UI/UX Research"), "Ana García"),
        EvaluationHistoryItem("4", "Luis Rodríguez", "Project Manager", "Avanzado", "Evaluación de desempeño", "2023-11-10", listOf("Agile", "Scrum", "Jira"), "Sofía Torres")
    )

    init {
        _uiState.update { it.copy(evaluations = allEvaluations) }

        viewModelScope.launch {
            combine(
                _uiState.map { it.searchQuery },
                _uiState.map { it.selectedType }
            ) { query, type ->
                val filteredList = allEvaluations.filter { item ->
                    val matchesQuery = item.collaboratorName.contains(query, ignoreCase = true)
                    val matchesType = type == "Todos los tipos" || item.evaluationType == type
                    matchesQuery && matchesType
                }
                _uiState.update { it.copy(filteredEvaluations = filteredList) }
            }.collect{}
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onEvaluationTypeChange(type: String) {
        _uiState.update { it.copy(selectedType = type) }
    }
}
