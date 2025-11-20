package com.example.project_3_tcs_grupo4_dam.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorReadDto
import com.example.project_3_tcs_grupo4_dam.data.remote.ColaboradorApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ColaboradorHomeViewModel(
    private val apiService: ColaboradorApiService,
    private val colaboradorId: String
) : ViewModel() {

    private val _colaborador = MutableStateFlow<ColaboradorReadDto?>(null)
    val colaborador: StateFlow<ColaboradorReadDto?> = _colaborador.asStateFlow()

    init {
        fetchColaborador()
    }

    private fun fetchColaborador() {
        viewModelScope.launch {
            try {
                val result = apiService.getColaboradorById(colaboradorId)
                _colaborador.value = result
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

class ColaboradorHomeViewModelFactory(
    private val apiService: ColaboradorApiService,
    private val colaboradorId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ColaboradorHomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ColaboradorHomeViewModel(apiService, colaboradorId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
