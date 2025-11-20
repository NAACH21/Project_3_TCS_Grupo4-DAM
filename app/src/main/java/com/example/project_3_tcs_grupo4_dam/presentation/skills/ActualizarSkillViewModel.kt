package com.example.project_3_tcs_grupo4_dam.presentation.skills

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.project_3_tcs_grupo4_dam.data.remote.ColaboradorApiService
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorReadDto
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorSkillDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ActualizarSkillViewModel(
    private val apiService: ColaboradorApiService,
    private val colaboradorId: String,
    private val skillName: String
) : ViewModel() {

    private var currentColaborador: ColaboradorReadDto? = null
    private val _skillState = MutableStateFlow<ColaboradorSkillDto?>(null)
    val skillState = _skillState.asStateFlow()

    var selectedNivel by mutableStateOf(0)
    var selectedTipoEvidencia by mutableStateOf("URL")
    var urlEvidencia by mutableStateOf("")
    var notasAdicionales by mutableStateOf("")

    var isLoading by mutableStateOf(false)
    var isSuccess by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    init {
        cargarColaborador()
    }

    private fun cargarColaborador() {
        viewModelScope.launch {
            isLoading = true
            try {
                // CORRECCIÓN: getColaboradorById devuelve Response<ColaboradorReadDto>
                val response = apiService.getColaboradorById(colaboradorId)
                
                if (response.isSuccessful) {
                    val data = response.body()
                    if (data != null) {
                        currentColaborador = data
                        val skill = data.skills.find { 
                            it.nombre.equals(skillName, ignoreCase = true) 
                        }
                        if (skill != null) {
                            _skillState.value = skill
                            selectedNivel = skill.nivel
                        } else {
                            errorMessage = "Skill no encontrado"
                        }
                    } else {
                        errorMessage = "Datos vacíos"
                    }
                } else {
                    errorMessage = "Error HTTP: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage = "Error de red: ${e.message}"
                Log.e("ActualizarSkillVM", "Error", e)
            } finally {
                isLoading = false
            }
        }
    }

    fun enviarValidacion() {
        if (currentColaborador == null || _skillState.value == null) return
        
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val updatedSkills = currentColaborador!!.skills.map { skill ->
                    if (skill.nombre.equals(skillName, ignoreCase = true)) {
                        skill.copy(nivel = selectedNivel)
                    } else {
                        skill
                    }
                }

                // updateColaborador devuelve Response<ColaboradorReadDto>
                val response = apiService.updateColaborador(
                    colaboradorId, 
                    currentColaborador!!.copy(skills = updatedSkills)
                )
                
                if (response.isSuccessful) {
                    isSuccess = true
                } else {
                    errorMessage = "Error al actualizar: ${response.code()}"
                }

            } catch (e: Exception) {
                errorMessage = "Excepción: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}

class ActualizarSkillViewModelFactory(
    private val apiService: ColaboradorApiService,
    private val colaboradorId: String,
    private val skillName: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ActualizarSkillViewModel(apiService, colaboradorId, skillName) as T
    }
}
