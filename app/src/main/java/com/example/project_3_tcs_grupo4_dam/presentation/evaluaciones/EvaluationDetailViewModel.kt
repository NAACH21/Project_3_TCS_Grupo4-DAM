package com.example.project_3_tcs_grupo4_dam.presentation.evaluaciones

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_3_tcs_grupo4_dam.data.model.EvaluacionReadDto
import com.example.project_3_tcs_grupo4_dam.data.remote.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EvaluationDetailViewModel : ViewModel() {

    private val _evaluation = MutableStateFlow<EvaluacionReadDto?>(null)
    val evaluation = _evaluation.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun loadEvaluation(evaluationId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _evaluation.value = ApiClient.evaluacionApiService.getEvaluacionById(evaluationId)
            } catch (e: Exception) {
                _error.value = "Error al cargar la evaluación: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
