package com.example.project_3_tcs_grupo4_dam.presentation.vacantes

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_3_tcs_grupo4_dam.data.remote.RetrofitClient
import com.example.project_3_tcs_grupo4_dam.data.repository.NotificacionesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para gestionar las vacantes y el anuncio de notificaciones
 * Integra la funcionalidad de anuncio de vacantes disponibles a colaboradores elegibles
 */
class VacantesViewModel : ViewModel() {

    private val notificacionesRepository = NotificacionesRepository(RetrofitClient.alertasApi)

    // Estado de carga
    private val _isAnunciando = MutableStateFlow(false)
    val isAnunciando = _isAnunciando.asStateFlow()

    // Mensaje de éxito
    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage = _successMessage.asStateFlow()

    // Mensaje de error
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    /**
     * Anuncia una vacante disponible a todos los colaboradores elegibles
     * Esta función envía notificaciones push a los colaboradores que cumplen
     * con los requisitos de la vacante
     *
     * @param vacanteId ID de la vacante a anunciar
     */
    fun anunciarVacante(vacanteId: String) {
        viewModelScope.launch {
            _isAnunciando.value = true
            _errorMessage.value = null
            _successMessage.value = null

            try {
                Log.d("VacantesVM", "Iniciando anuncio de vacante: $vacanteId")

                val result = notificacionesRepository.anunciarVacante(vacanteId)

                result.onSuccess {
                    _successMessage.value = "Vacante anunciada exitosamente a colaboradores elegibles"
                    Log.d("VacantesVM", "Vacante $vacanteId anunciada correctamente")
                }.onFailure { error ->
                    _errorMessage.value = error.message ?: "Error al anunciar la vacante"
                    Log.e("VacantesVM", "Error anunciando vacante $vacanteId", error)
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error de conexión: ${e.message}"
                Log.e("VacantesVM", "Excepción en anunciarVacante", e)
            } finally {
                _isAnunciando.value = false
            }
        }
    }

    /**
     * Limpia el mensaje de éxito
     */
    fun clearSuccessMessage() {
        _successMessage.value = null
    }

    /**
     * Limpia el mensaje de error
     */
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}

