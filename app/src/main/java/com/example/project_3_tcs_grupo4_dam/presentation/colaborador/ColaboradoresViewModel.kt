package com.example.project_3_tcs_grupo4_dam.presentation.colaborador

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorReadDto
import com.example.project_3_tcs_grupo4_dam.data.repository.ColaboradorRepository
import com.example.project_3_tcs_grupo4_dam.data.repository.ColaboradorRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ColaboradoresViewModel : ViewModel() {

    // Repositorio que llama a la API
    private val repository: ColaboradorRepository = ColaboradorRepositoryImpl()

    // Estado: lista de colaboradores
    private val _colaboradores = MutableStateFlow<List<ColaboradorReadDto>>(emptyList())
    val colaboradores = _colaboradores.asStateFlow()

    // Estado: cargando
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    // Estado: error
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
                val lista = repository.getAllColaboradores()
                _colaboradores.value = lista
                Log.d("ColaboradoresVM", "Datos cargados: ${lista.size}")
            } catch (e: Exception) {
                Log.e("ColaboradoresVM", "Error al cargar colaboradores", e)
                _error.value = e.message ?: "Error desconocido"
                _colaboradores.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
