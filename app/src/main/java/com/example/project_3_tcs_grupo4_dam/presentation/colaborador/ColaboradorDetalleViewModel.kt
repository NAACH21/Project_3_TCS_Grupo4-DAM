package com.example.project_3_tcs_grupo4_dam.presentation.colaborador

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorReadDto
import com.example.project_3_tcs_grupo4_dam.data.repository.ColaboradorRepository
import com.example.project_3_tcs_grupo4_dam.data.repository.ColaboradorRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ColaboradorDetalleViewModel(
    private val colaboradorId: String
) : ViewModel() {

    private val repository: ColaboradorRepository = ColaboradorRepositoryImpl()

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
        if (colaboradorId.isNotEmpty()) {
            fetchColaboradorDetalle()
        } else {
            _error.value = "ID de colaborador no válido"
        }
    }

    private fun fetchColaboradorDetalle() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                Log.d("ColaboradorDetalleVM", "Cargando colaborador: $colaboradorId")
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

class ColaboradorDetalleViewModelFactory(
    private val colaboradorId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ColaboradorDetalleViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ColaboradorDetalleViewModel(colaboradorId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
