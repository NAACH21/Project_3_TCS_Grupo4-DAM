package com.example.project_3_tcs_grupo4_dam.presentation.vacantes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_3_tcs_grupo4_dam.data.model.SkillRequerido
import com.example.project_3_tcs_grupo4_dam.data.model.VacanteCreateDto
import com.example.project_3_tcs_grupo4_dam.data.remote.RetrofitClient
import com.example.project_3_tcs_grupo4_dam.data.repository.VacanteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class SaveResult {
    object Idle : SaveResult()
    object Loading : SaveResult()
    object Success : SaveResult()
    data class Error(val message: String) : SaveResult()
}

class NewVacantViewModel : ViewModel() {

    private val repository = VacanteRepository(RetrofitClient.vacanteApi)

    private val _saveStatus = MutableStateFlow<SaveResult>(SaveResult.Idle)
    val saveStatus: StateFlow<SaveResult> = _saveStatus

    fun saveVacante(
        nombrePerfil: String,
        area: String,
        rolLaboral: String,
        skillsRequeridos: List<SkillRequerido>,
        certificacionesRequeridas: List<String>,
        fechaInicio: String,
        urgencia: String,
        estadoVacante: String
    ) {
        if (nombrePerfil.isBlank() || area.isBlank() || rolLaboral.isBlank()) {
            _saveStatus.value = SaveResult.Error("Los campos marcados con * son obligatorios.")
            return
        }

        val vacante = VacanteCreateDto(
            nombrePerfil = nombrePerfil,
            area = area,
            rolLaboral = rolLaboral,
            skillsRequeridos = skillsRequeridos,
            certificacionesRequeridas = certificacionesRequeridas,
            fechaInicio = "2025-11-21T13:05:33.574Z", // TODO: Usar un selector de fecha
            urgencia = urgencia,
            estadoVacante = estadoVacante,
            creadaPorUsuarioId = "675000000000000000001001" // TODO: Obtener el ID del usuario actual
        )

        viewModelScope.launch {
            _saveStatus.value = SaveResult.Loading
            try {
                repository.createVacante(vacante)
                _saveStatus.value = SaveResult.Success
            } catch (e: Exception) {
                _saveStatus.value = SaveResult.Error("Error al guardar la vacante: ${e.message}")
            }
        }
    }

    fun resetSaveStatus() {
        _saveStatus.value = SaveResult.Idle
    }
}