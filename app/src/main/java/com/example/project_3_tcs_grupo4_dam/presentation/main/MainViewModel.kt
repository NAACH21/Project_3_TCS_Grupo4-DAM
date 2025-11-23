package com.example.project_3_tcs_grupo4_dam.presentation.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorDtos.ColaboradorReadDto
import com.example.project_3_tcs_grupo4_dam.data.repository.ColaboradorRepository
import com.example.project_3_tcs_grupo4_dam.data.repository.ColaboradorRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    // Instancia del repositorio
    // (En una app más grande, esto se "inyecta", pero así está bien para empezar)
    private val repository: ColaboradorRepository = ColaboradorRepositoryImpl()

    // --- Estado de la UI ---
    // Un StateFlow privado que se puede modificar
    private val _colaboradores = MutableStateFlow<List<ColaboradorReadDto>>(emptyList())

    // Un StateFlow público que la UI puede "observar" (solo leer)
    val colaboradores: StateFlow<List<ColaboradorReadDto>> = _colaboradores.asStateFlow()

    // --- Estado de Carga ---
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // El bloque 'init' se ejecuta tan pronto como se crea el ViewModel
    init {
        fetchColaboradores()
    }

    private fun fetchColaboradores() {
        // 'viewModelScope' es una Corutina atada al ViewModel.
        // Se cancelará automáticamente cuando el ViewModel se destruya.
        viewModelScope.launch {
            _isLoading.value = true // Mostrar 'cargando'
            try {
                // Llamamos al repositorio
                val lista = repository.getAllColaboradores()
                _colaboradores.value = lista // Actualizamos el estado

                Log.d("MainViewModel", "Datos recibidos: ${lista.size} colaboradores")

            } catch (e: Exception) {
                // Manejar el error
                Log.e("MainViewModel", "Error al cargar datos", e)
                _colaboradores.value = emptyList() // Limpiar en caso de error
            } finally {
                _isLoading.value = false // Ocultar 'cargando'
            }
        }
    }
}