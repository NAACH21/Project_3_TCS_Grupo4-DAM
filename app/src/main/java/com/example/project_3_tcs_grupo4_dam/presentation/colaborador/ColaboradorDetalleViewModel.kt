package com.example.project_3_tcs_grupo4_dam.presentation.colaborador

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorDtos.ColaboradorReadDto
import com.example.project_3_tcs_grupo4_dam.data.repository.ColaboradorRepository
import com.example.project_3_tcs_grupo4_dam.data.repository.ColaboradorRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ColaboradorDetalleViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val repository: ColaboradorRepository = ColaboradorRepositoryImpl()

    // Obtener el ID del colaborador de los argumentos de navegación
    private val colaboradorId: String = savedStateHandle.get<String>("colaboradorId") ?: ""

    // Estado: colaborador actual
    private val _colaborador = MutableStateFlow<ColaboradorReadDto?>(null)
    val colaborador = _colaborador.asStateFlow()

    // Estado: cargando
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    // Estado: error
    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    init {
        fetchColaboradorDetalle()
    }

    private fun fetchColaboradorDetalle() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val colaborador = repository.getColaboradorById(colaboradorId)
                _colaborador.value = colaborador
                Log.d("ColaboradorDetalleVM", "Colaborador cargado: ${colaborador.nombres}")
            } catch (e: Exception) {
                Log.e("ColaboradorDetalleVM", "Error al cargar colaborador", e)
                _error.value = e.message ?: "Error desconocido"
                _colaborador.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }
}