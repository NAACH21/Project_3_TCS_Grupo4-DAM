package com.example.project_3_tcs_grupo4_dam.presentation.vacantes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_3_tcs_grupo4_dam.data.model.SkillRequerido
import com.example.project_3_tcs_grupo4_dam.data.model.VacanteCreateDto
import com.example.project_3_tcs_grupo4_dam.data.remote.RetrofitClient
import com.example.project_3_tcs_grupo4_dam.data.repository.VacanteRepository
import com.example.project_3_tcs_grupo4_dam.data.repository.NotificacionesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class SaveResult {
    object Idle : SaveResult()
    object Loading : SaveResult()
    data class Success(val vacanteId: String) : SaveResult() // MODIFICADO: Ahora incluye el ID
    data class Error(val message: String) : SaveResult()
}

// NUEVO: Estado para anuncio de vacante
sealed class AnuncioResult {
    object Idle : AnuncioResult()
    object Loading : AnuncioResult()
    object Success : AnuncioResult()
    data class Error(val message: String) : AnuncioResult()
}

class NewVacantViewModel : ViewModel() {

    private val repository = VacanteRepository(RetrofitClient.vacanteApi)
    // NUEVO: Repositorio de notificaciones
    private val notificacionesRepository = NotificacionesRepository(RetrofitClient.alertasApi)

    private val _saveStatus = MutableStateFlow<SaveResult>(SaveResult.Idle)
    val saveStatus: StateFlow<SaveResult> = _saveStatus

    // NUEVO: Estado para anuncio de vacante
    private val _anuncioStatus = MutableStateFlow<AnuncioResult>(AnuncioResult.Idle)
    val anuncioStatus: StateFlow<AnuncioResult> = _anuncioStatus

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
                // MODIFICADO: Usar Result para capturar el ID
                val result = repository.createVacante(vacante)

                result.onSuccess { vacanteCreada ->
                    // Capturar el ID de la vacante creada
                    val vacanteId = vacanteCreada.id
                    _saveStatus.value = SaveResult.Success(vacanteId)
                }.onFailure { error ->
                    _saveStatus.value = SaveResult.Error("Error al guardar la vacante: ${error.message}")
                }
            } catch (e: Exception) {
                _saveStatus.value = SaveResult.Error("Error al guardar la vacante: ${e.message}")
            }
        }
    }

    // NUEVO: Función para notificar vacante inmediatamente
    /**
     * Envía notificación de vacante disponible a colaboradores elegibles
     * @param vacanteId ID de la vacante recién creada
     */
    fun notificarAhora(vacanteId: String) {
        if (vacanteId.isBlank()) {
            _anuncioStatus.value = AnuncioResult.Error("ID de vacante inválido")
            return
        }

        viewModelScope.launch {
            _anuncioStatus.value = AnuncioResult.Loading
            try {
                val result = notificacionesRepository.anunciarVacante(vacanteId)

                result.onSuccess {
                    _anuncioStatus.value = AnuncioResult.Success
                }.onFailure { error ->
                    _anuncioStatus.value = AnuncioResult.Error(
                        "Error al enviar notificación: ${error.message}"
                    )
                }
            } catch (e: Exception) {
                _anuncioStatus.value = AnuncioResult.Error(
                    "Error de conexión: ${e.message}"
                )
            }
        }
    }

    fun resetSaveStatus() {
        _saveStatus.value = SaveResult.Idle
    }

    // NUEVO: Reset del estado de anuncio
    fun resetAnuncioStatus() {
        _anuncioStatus.value = AnuncioResult.Idle
    }
}