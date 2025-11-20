package com.example.project_3_tcs_grupo4_dam.presentation.skills

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorSkillDto
import com.example.project_3_tcs_grupo4_dam.data.remote.ColaboradorApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ColaboradorSkillsUiState(
    val isLoading: Boolean = false,
    val skills: List<ColaboradorSkillDto> = emptyList(),
    val errorMessage: String? = null
)

class ColaboradorSkillsViewModel(
    private val apiService: ColaboradorApiService,
    private val colaboradorId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(ColaboradorSkillsUiState())
    val uiState: StateFlow<ColaboradorSkillsUiState> = _uiState.asStateFlow()

    init {
        fetchSkills()
    }

    fun fetchSkills() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            try {
                Log.d("ColaboradorSkillsVM", "Fetching skills for ID: $colaboradorId")
                
                // CORRECCIÓN: Response<ColaboradorReadDto> DIRECTO
                val response = apiService.getColaboradorById(colaboradorId)
                
                if (response.isSuccessful) {
                    val colaborador = response.body() // DIRECTAMENTE EL DTO
                    if (colaborador != null) {
                        Log.d("ColaboradorSkillsVM", "Colaborador encontrado: ${colaborador.nombres}")
                        
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            skills = colaborador.skills
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "Datos vacíos"
                        )
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Error de conexión: ${response.code()}"
                    )
                }

            } catch (e: Exception) {
                Log.e("ColaboradorSkillsVM", "Error fetching skills", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error al cargar skills: ${e.message}"
                )
            }
        }
    }

    fun filterSkills(query: String, typeFilter: String, statusFilter: String): List<ColaboradorSkillDto> {
        var filtered = _uiState.value.skills

        if (query.isNotBlank()) {
            filtered = filtered.filter { it.nombre.contains(query, ignoreCase = true) }
        }

        if (typeFilter != "Todos") {
            val backendType = when(typeFilter) {
                "Técnicos" -> "TECNICO"
                "Blandos" -> "BLANDO"
                else -> typeFilter.uppercase()
            }
            filtered = filtered.filter { it.tipo.equals(backendType, ignoreCase = true) }
        }

        if (statusFilter != "Todos") {
            val backendStatus = when(statusFilter) {
                "Aprobados" -> "APROBADO"
                "Pendientes" -> "PENDIENTE"
                "Rechazados" -> "RECHAZADO"
                else -> ""
            }
            if (backendStatus.isNotEmpty()) {
                filtered = filtered.filter { 
                    val estado = it.estado ?: "PENDIENTE"
                    estado.equals(backendStatus, ignoreCase = true) 
                }
            }
        }

        return filtered
    }
}

class ColaboradorSkillsViewModelFactory(
    private val apiService: ColaboradorApiService,
    private val colaboradorId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ColaboradorSkillsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ColaboradorSkillsViewModel(apiService, colaboradorId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
