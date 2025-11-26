package com.example.project_3_tcs_grupo4_dam.presentation.evaluaciones

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_3_tcs_grupo4_dam.data.model.EvaluacionReadDto
import com.example.project_3_tcs_grupo4_dam.data.remote.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EvaluationHistoryViewModel : ViewModel() {

    private val _evaluations = MutableStateFlow<List<EvaluacionReadDto>>(emptyList())
    val evaluations = _evaluations.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    init {
        loadEvaluations()
    }

    private fun loadEvaluations() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _evaluations.value = ApiClient.evaluacionApiService.getEvaluaciones()
            } catch (e: Exception) {
                _error.value = "Error al cargar el historial de evaluaciones: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
