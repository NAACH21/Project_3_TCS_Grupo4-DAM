package com.example.project_3_tcs_grupo4_dam.presentation.vacantes

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_3_tcs_grupo4_dam.data.model.SkillRequerido
import com.example.project_3_tcs_grupo4_dam.data.model.Vacante
import com.example.project_3_tcs_grupo4_dam.data.model.VacanteCreateDto
import com.example.project_3_tcs_grupo4_dam.data.remote.RetrofitClient
import com.example.project_3_tcs_grupo4_dam.data.repository.NotificacionesRepository
import com.example.project_3_tcs_grupo4_dam.data.repository.VacanteRepository
import com.example.project_3_tcs_grupo4_dam.utils.DateUtils
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class SaveResult {
    object Idle : SaveResult()
    object Loading : SaveResult()
    data class Success(val vacante: Vacante) : SaveResult()
    data class Error(val message: String) : SaveResult()
}

sealed class AnuncioResult {
    object Idle : AnuncioResult()
    object Loading : AnuncioResult()
    object Success : AnuncioResult()
    data class Error(val message: String) : AnuncioResult()
}

class NewVacantViewModel : ViewModel() {

    private val repository = VacanteRepository(RetrofitClient.vacanteApi)
    private val notificacionesRepository = NotificacionesRepository(RetrofitClient.alertasApi)

    private val _saveStatus = MutableStateFlow<SaveResult>(SaveResult.Idle)
    val saveStatus: StateFlow<SaveResult> = _saveStatus

    private val _anuncioStatus = MutableStateFlow<AnuncioResult>(AnuncioResult.Idle)
    val anuncioStatus: StateFlow<AnuncioResult> = _anuncioStatus

    fun saveVacante(
        nombrePerfil: String,
        area: String,
        rolLaboral: String,
        skillsRequeridos: List<SkillRequerido>,
        certificacionesRequeridas: List<String>,
        fechaInicio: String, // Formato "dd/MM/yyyy"
        urgencia: String,
        estadoVacante: String
    ) {
        // 1. VALIDACIÓN DE DATOS ANTES DE ENVIAR
        if (nombrePerfil.isBlank() || area.isBlank() || rolLaboral.isBlank()) {
            _saveStatus.value = SaveResult.Error("Los campos Nombre, Área y Rol son obligatorios.")
            return
        }
        if (skillsRequeridos.isEmpty()) {
            _saveStatus.value = SaveResult.Error("Debe agregar al menos un skill requerido.")
            return
        }

        // 2. CONVERSIÓN SEGURA DE FECHA A ISO 8601
        val fechaIso = DateUtils.convertToIso8601(fechaInicio)
        if (fechaInicio.isNotBlank() && fechaIso == null) {
            _saveStatus.value = SaveResult.Error("El formato de fecha de inicio es inválido. Use dd/MM/yyyy.")
            return
        }
        val fechaFinalParaEnviar = fechaIso ?: "2025-01-01T12:00:00.000Z" 

        // 3. CONSTRUCCIÓN DEL DTO CON DATOS VALIDADOS
        val vacante = VacanteCreateDto(
            nombrePerfil = nombrePerfil,
            area = area,
            rolLaboral = rolLaboral,
            skillsRequeridos = skillsRequeridos,
            certificacionesRequeridas = certificacionesRequeridas,
            fechaInicio = fechaFinalParaEnviar,
            urgencia = urgencia,
            estadoVacante = estadoVacante,
            creadaPorUsuarioId = "675000000000000000001001" // ID fijo por ahora (según instrucciones previas)
        )

        viewModelScope.launch {
            _saveStatus.value = SaveResult.Loading
            try {
                val gson = Gson()
                val jsonBody = gson.toJson(vacante)
                Log.d("NewVacantViewModel", "Enviando JSON para crear vacante: $jsonBody")
                
                val result = repository.createVacante(vacante)

                result.onSuccess { vacanteCreada ->
                    Log.d("NewVacantViewModel", "Vacante creada con ID: ${vacanteCreada.id}")
                    _saveStatus.value = SaveResult.Success(vacanteCreada)
                }.onFailure { error ->
                    Log.e("NewVacantViewModel", "Error al crear vacante: ${error.message}", error)
                    _saveStatus.value = SaveResult.Error("Error del servidor: ${error.message}")
                }
            } catch (e: Exception) {
                Log.e("NewVacantViewModel", "Excepción al crear vacante", e)
                _saveStatus.value = SaveResult.Error("Error de conexión: ${e.message}")
            }
        }
    }

    fun notificarAhora(vacanteId: String) {
        if (vacanteId.isBlank()) {
            _anuncioStatus.value = AnuncioResult.Error("ID de vacante inválido para notificar.")
            return
        }

        viewModelScope.launch {
            _anuncioStatus.value = AnuncioResult.Loading
            try {
                // LOG SOLICITADO: Verificación del ID antes de enviar para confirmar que es String puro
                Log.d("VACANTE_ID_ENVIO", vacanteId)
                
                val result = notificacionesRepository.anunciarVacante(vacanteId)

                result.onSuccess {
                    _anuncioStatus.value = AnuncioResult.Success
                }.onFailure { error ->
                    Log.e("NewVacantViewModel", "Error al notificar: ${error.message}", error)
                    _anuncioStatus.value = AnuncioResult.Error("Error del servidor: ${error.message}")
                }
            } catch (e: Exception) {
                Log.e("NewVacantViewModel", "Excepción al notificar", e)
                _anuncioStatus.value = AnuncioResult.Error("Error de conexión: ${e.message}")
            }
        }
    }

    fun resetSaveStatus() {
        _saveStatus.value = SaveResult.Idle
    }

    fun resetAnuncioStatus() {
        _anuncioStatus.value = AnuncioResult.Idle
    }
}
