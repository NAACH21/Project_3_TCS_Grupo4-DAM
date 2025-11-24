package com.example.project_3_tcs_grupo4_dam.presentation.skills

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorDtos.SkillReadDto
import com.example.project_3_tcs_grupo4_dam.data.remote.ColaboradorApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ColaboradorSkillsUiState(
    val isLoading: Boolean = false,
    val skills: List<SkillReadDto> = emptyList(),
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

    private fun fetchSkills() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                // La API ahora devuelve ColaboradorReadDto con skills embebidos
                val colaborador = apiService.getColaboradorById(colaboradorId)

                Log.d("ColaboradorSkillsVM", "Colaborador: ${colaborador.nombres}")
                Log.d("ColaboradorSkillsVM", "Skills recibidos: ${colaborador.skills.size}")

                _uiState.value = _uiState.value.copy(
                    skills = colaborador.skills,
                    isLoading = false
                )
            } catch (e: Exception) {
                Log.e("ColaboradorSkillsVM", "Error al obtener skills: ${e.message}", e)
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Error al cargar skills: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    fun filtrarPorNombre(query: String): List<SkillReadDto> {
        return if (query.isBlank()) {
            _uiState.value.skills
        } else {
            _uiState.value.skills.filter {
                it.nombre.contains(query, ignoreCase = true)
            }
        }
    }

    fun filtrarPorTipo(tipo: String): List<SkillReadDto> {
        return if (tipo == "TODOS") {
            _uiState.value.skills
        } else {
            _uiState.value.skills.filter {
                it.tipo == tipo
            }
        }
    }

    fun filtrarPorEstado(estado: String): List<SkillReadDto> {
        // Como SkillReadDto no tiene estado, retornamos todos
        return _uiState.value.skills
    }

    fun filterSkills(searchQuery: String, selectedType: String, selectedStatus: String): List<SkillReadDto> {
        var result = _uiState.value.skills

        // Filtro por búsqueda
        if (searchQuery.isNotBlank()) {
            result = result.filter {
                it.nombre.contains(searchQuery, ignoreCase = true)
            }
        }

        // Filtro por tipo
        if (selectedType != "Todos") {
            result = result.filter {
                it.tipo == selectedType
            }
        }

        // El filtro de estado no aplica porque SkillReadDto no tiene estado
        // pero lo dejamos para compatibilidad con la UI

        return result
    }
}

class ColaboradorSkillsViewModelFactory(
    private val apiService: ColaboradorApiService,
    private val colaboradorId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ColaboradorSkillsViewModel(apiService, colaboradorId) as T
    }
}
