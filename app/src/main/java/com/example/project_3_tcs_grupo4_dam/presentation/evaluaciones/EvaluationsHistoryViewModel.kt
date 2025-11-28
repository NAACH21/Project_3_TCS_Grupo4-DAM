package com.example.project_3_tcs_grupo4_dam.presentation.evaluaciones

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_3_tcs_grupo4_dam.data.model.toEvaluacion
import com.example.project_3_tcs_grupo4_dam.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

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

    init {
        loadEvaluations()

        viewModelScope.launch {
            combine(
                _uiState.map { it.searchQuery },
                _uiState.map { it.selectedType },
                _uiState.map { it.evaluations }
            ) { query, type, evaluations ->
                val filteredList = evaluations.filter { item ->
                    val matchesQuery = item.collaboratorName.contains(query, ignoreCase = true)
                    val matchesType = type == "Todos los tipos" || item.evaluationType == type
                    matchesQuery && matchesType
                }
                _uiState.update { it.copy(filteredEvaluations = filteredList) }
            }.collect()
        }
    }

    private fun loadEvaluations() {
        viewModelScope.launch {
            try {
                val evaluationsFromApi = RetrofitClient.evaluacionApiService.getEvaluaciones()
                val evaluationHistoryItems = evaluationsFromApi.map { evaluacionDto ->
                    val evaluacion = evaluacionDto.toEvaluacion()
                    // NOTA: Se han hecho algunas suposiciones para el mapeo.
                    // - `collaboratorName` no está directamente en `Evaluacion`. Se usa `liderEvaluador` como placeholder.
                    //   Lo ideal sería obtener el nombre del colaborador a partir de `colaboradorId`.
                    // - `level` no está disponible en el modelo `Evaluacion`. Se usa "N/A" como placeholder.
                    EvaluationHistoryItem(
                        id = evaluacion.id,
                        collaboratorName = evaluacion.liderEvaluador, // Placeholder
                        role = evaluacion.rolActual,
                        level = "N/A", // Placeholder
                        evaluationType = evaluacion.tipoEvaluacion,
                        date = formatDate(evaluacion.fechaEvaluacion),
                        skills = evaluacion.skillsEvaluados.map { it.nombre },
                        loadedBy = evaluacion.usuarioResponsable
                    )
                }
                _uiState.update { it.copy(evaluations = evaluationHistoryItems) }
            } catch (e: Exception) {
                // TODO: Manejar la excepción de forma adecuada (ej: mostrar un mensaje de error).
                e.printStackTrace()
            }
        }
    }

    private fun formatDate(isoDate: String): String {
        return try {
            // Se asume un formato como "2024-05-20T10:00:00.000Z"
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            parser.parse(isoDate)?.let { formatter.format(it) } ?: isoDate.take(10)
        } catch (e: Exception) {
            isoDate.take(10) // Fallback por si falla el parseo
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onEvaluationTypeChange(type: String) {
        _uiState.update { it.copy(selectedType = type) }
    }
}
