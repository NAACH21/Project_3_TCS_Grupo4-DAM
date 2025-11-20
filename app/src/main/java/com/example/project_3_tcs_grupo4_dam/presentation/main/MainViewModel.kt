package com.example.project_3_tcs_grupo4_dam.presentation.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorListDto // CORRECCIÓN: Importar DTO de lista
import com.example.project_3_tcs_grupo4_dam.data.repository.ColaboradorRepository
import com.example.project_3_tcs_grupo4_dam.data.repository.ColaboradorRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    // Instancia del repositorio
    private val repository: ColaboradorRepository = ColaboradorRepositoryImpl()

    // --- Estado de la UI ---
    // CORRECCIÓN: Usar ColaboradorListDto en lugar de ColaboradorReadDto
    private val _colaboradores = MutableStateFlow<List<ColaboradorListDto>>(emptyList())

    // Un StateFlow público que la UI puede "observar" (solo leer)
    val colaboradores: StateFlow<List<ColaboradorListDto>> = _colaboradores.asStateFlow()

    // --- Estado de Carga ---
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // El bloque 'init' se ejecuta tan pronto como se crea el ViewModel
    init {
        fetchColaboradores()
    }

    private fun fetchColaboradores() {
        viewModelScope.launch {
            _isLoading.value = true // Mostrar 'cargando'
            try {
                // Llamamos al repositorio (ahora devuelve List<ColaboradorListDto>)
                val lista = repository.getAllColaboradores()
                _colaboradores.value = lista // Ahora los tipos coinciden

                Log.d("MainViewModel", "Datos recibidos: ${lista.size} colaboradores")

            } catch (e: Exception) {
                Log.e("MainViewModel", "Error al cargar datos", e)
                _colaboradores.value = emptyList()
            } finally {
                _isLoading.value = false // Ocultar 'cargando'
            }
        }
    }
}
