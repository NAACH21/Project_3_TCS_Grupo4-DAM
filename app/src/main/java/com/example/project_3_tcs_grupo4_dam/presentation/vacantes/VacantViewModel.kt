package com.example.project_3_tcs_grupo4_dam.presentation.vacantes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_3_tcs_grupo4_dam.data.model.VacanteResponse
import com.example.project_3_tcs_grupo4_dam.data.remote.RetrofitClient
import com.example.project_3_tcs_grupo4_dam.data.repository.VacanteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class VacantViewModel : ViewModel() {

    private val repository = VacanteRepository(RetrofitClient.vacanteApi)

    private val _vacantes = MutableStateFlow<List<VacanteResponse>>(emptyList())
    val vacantes: StateFlow<List<VacanteResponse>> = _vacantes

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        fetchVacantes()
    }

    fun fetchVacantes() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _vacantes.value = repository.getVacantes()
            } catch (e: Exception) {
                _errorMessage.value = "Error al obtener las vacantes: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
