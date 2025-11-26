package com.example.project_3_tcs_grupo4_dam.presentation.colaborador

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorReadDto
import com.example.project_3_tcs_grupo4_dam.data.repository.ColaboradorRepository
import com.example.project_3_tcs_grupo4_dam.data.repository.ColaboradorRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ColaboradorListViewModel : ViewModel() {

    private val repository: ColaboradorRepository = ColaboradorRepositoryImpl()

    private val _colaboradores = MutableStateFlow<List<ColaboradorReadDto>>(emptyList())
    val colaboradores = _colaboradores.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    init {
        loadColaboradores()
    }

    fun loadColaboradores() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _colaboradores.value = repository.getAllColaboradores()
            } catch (e: Exception) {
                _error.value = "Error al cargar colaboradores: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteColaborador(id: String) {
        viewModelScope.launch {
            try {
                repository.deleteColaborador(id)
                loadColaboradores() // Refresh the list
            } catch (e: Exception) {
                _error.value = "Error al eliminar colaborador: ${e.message}"
            }
        }
    }
}