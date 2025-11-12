package com.example.project_3_tcs_grupo4_dam.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorReadDto
import com.example.project_3_tcs_grupo4_dam.data.repository.ColaboradorRepository
import com.example.project_3_tcs_grupo4_dam.data.repository.ColaboradorRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    // Repositorio
    private val repository: ColaboradorRepository = ColaboradorRepositoryImpl()

    // Estado de la lista
    private val _colaboradores = MutableStateFlow<List<ColaboradorReadDto>>(emptyList())
    val colaboradores = _colaboradores.asStateFlow()

    // Estado de carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    // Estado de error
    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    init {
        fetchColaboradores()
    }

    private fun fetchColaboradores() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _colaboradores.value = repository.getAllColaboradores()
                Log.d("HomeViewModel", "Datos cargados: ${_colaboradores.value.size} items")
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error al cargar colaboradores", e)
                _error.value = e.message ?: "Error desconocido"
                _colaboradores.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}