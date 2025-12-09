package com.example.project_3_tcs_grupo4_dam.presentation.notificaciones

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_3_tcs_grupo4_dam.data.local.LocalAlertManager
import com.example.project_3_tcs_grupo4_dam.data.local.SessionManager
import com.example.project_3_tcs_grupo4_dam.data.model.AlertaDto
import com.example.project_3_tcs_grupo4_dam.data.model.AlertaDashboard
import com.example.project_3_tcs_grupo4_dam.data.remote.RetrofitClient
import com.example.project_3_tcs_grupo4_dam.data.repository.NotificacionesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Modelo UI Wrapper para manejar estado local
data class AlertaUiState(
    val alerta: AlertaDto,
    val isVisto: Boolean
)

class NotificacionesViewModel(
    private val sessionManager: SessionManager,
    @Suppress("StaticFieldLeak") // El contexto es Application context, seguro de usar
    private val context: android.content.Context // Necesitamos el contexto para LocalAlertManager
) : ViewModel() {

    private val apiService = RetrofitClient.alertasApi
    private val localManager = LocalAlertManager(context) // Manager para persistencia local

    // ==================== NUEVO: Dashboard Repository ====================
    private val notificacionesRepository = NotificacionesRepository(apiService)

    // Estado con la lista enriquecida (DTO + Visto) para la pantalla de Colaborador
    private val _alertasUi = MutableStateFlow<List<AlertaUiState>>(emptyList())
    val alertasUi = _alertasUi.asStateFlow()

    // ==================== NUEVO: Dashboard State (Solo para Admin) ====================
    private val _alertasDashboard = MutableStateFlow<List<AlertaDashboard>>(emptyList())
    val alertasDashboard = _alertasDashboard.asStateFlow()

    // Badge real calculado
    private val _unreadCount = MutableStateFlow(0)
    val unreadCount = _unreadCount.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage = _successMessage.asStateFlow()

    init {
        // Al crear el ViewModel, decidimos qué cargar según el rol
        val rol = sessionManager.getRol() ?: ""
        if (rol.equals("COLABORADOR", ignoreCase = true)) {
            cargarAlertas() // Carga la lista detallada para colaborador
        } else {
            // Para Admin/Manager, la carga se dispara desde la UI con `cargarNotificaciones`
        }
    }

    private fun extraerId(valor: Any?): String? {
        if (valor == null) return null
        return try {
            when (valor) {
                is String -> valor
                is Map<*, *> -> valor["\$oid"]?.toString() ?: valor["oid"]?.toString()
                else -> valor.toString()
            }
        } catch (e: Exception) {
            Log.e("NotificacionesVM", "Error extrayendo ID: $valor", e)
            null
        }
    }

    /**
     * Carga las notificaciones del DASHBOARD DE ADMIN.
     */
    fun cargarNotificaciones(esAdmin: Boolean, userId: String? = null) {
        if (_isLoading.value) return

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val result = notificacionesRepository.obtenerDashboard(esAdmin, userId)

                result.onSuccess { alertas ->
                    val readIds = localManager.getReadIds()
                    val mergedAlertas = alertas.map { alerta ->
                        if (readIds.contains(alerta.idReferencia)) {
                            alerta.copy(activa = false)
                        } else {
                            alerta
                        }
                    }

                    _alertasDashboard.value = mergedAlertas
                    _unreadCount.value = mergedAlertas.count { it.activa }
                    Log.d("NotificacionesVM", "Dashboard cargado: ${mergedAlertas.size} notificaciones, ${_unreadCount.value} no leídas")
                }.onFailure { error ->
                    _errorMessage.value = error.message ?: "Error desconocido al cargar notificaciones"
                    Log.e("NotificacionesVM", "Error cargando dashboard", error)
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error de conexión: ${e.message}"
                Log.e("NotificacionesVM", "Excepción en cargarNotificaciones", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Marca una notificación del DASHBOARD DE ADMIN como leída.
     */
    fun marcarDashboardComoLeida(idReferencia: String) {
        viewModelScope.launch {
            try {
                localManager.markAsRead(idReferencia)

                val updatedList = _alertasDashboard.value.map { alerta ->
                    if (alerta.idReferencia == idReferencia) {
                        alerta.copy(activa = false)
                    } else {
                        alerta
                    }
                }

                _alertasDashboard.value = updatedList
                _unreadCount.value = updatedList.count { it.activa }

                Log.d("NotificacionesVM", "Notificación $idReferencia marcada como leída y persistida")
            } catch (e: Exception) {
                Log.e("NotificacionesVM", "Error marcando notificación como leída", e)
            }
        }
    }

    /**
     * Carga las ALERTAS DETALLADAS PARA COLABORADOR.
     */
    fun cargarAlertas() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val rolUsuario = sessionManager.getRol() ?: "INVITADO"
                val miColaboradorId = sessionManager.getColaboradorId()?.trim()
                val miUsuarioId = sessionManager.getUsuarioId()?.trim()

                val response = apiService.getAllAlertas()
                val todasLasAlertas = response.data ?: emptyList()

                val alertasFiltradas = if (rolUsuario.equals("COLABORADOR", ignoreCase = true)) {
                    todasLasAlertas.filter { alerta ->
                        val alertaColabId = extraerId(alerta.colaboradorId)
                        val esParaMiColaborador = !miColaboradorId.isNullOrEmpty() && 
                                                  !alertaColabId.isNullOrEmpty() &&
                                                  alertaColabId == miColaboradorId

                        val soyDestinatarioDirecto = alerta.destinatarios?.any { dest ->
                            val destUsuarioId = extraerId(dest.usuarioId)
                            !miUsuarioId.isNullOrEmpty() && 
                            !destUsuarioId.isNullOrEmpty() &&
                            destUsuarioId == miUsuarioId
                        } == true

                        esParaMiColaborador || soyDestinatarioDirecto
                    }
                } else { // Para Admin/Manager, esta función no debería devolver nada
                    emptyList()
                }

                val readIds = localManager.getReadIds()
                val uiList = alertasFiltradas.map { alerta ->
                    val id = extraerId(alerta.id) ?: ""
                    val isRead = readIds.contains(id)
                    AlertaUiState(alerta = alerta, isVisto = isRead)
                }

                _alertasUi.value = uiList
                _unreadCount.value = uiList.count { !it.isVisto }

            } catch (e: Exception) {
                Log.e("NotificacionesVM", "Error cargando alertas de colaborador", e)
                _alertasUi.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Marca una ALERTA DE COLABORADOR como leída.
     */
    fun marcarComoVisto(alertaId: String) {
        localManager.markAsRead(alertaId)

        val currentList = _alertasUi.value.map { item ->
            val itemId = extraerId(item.alerta.id)
            if (itemId == alertaId) {
                item.copy(isVisto = true)
            } else {
                item
            }
        }
        
        _alertasUi.value = currentList
        _unreadCount.value = currentList.count { !it.isVisto }
    }

    fun clearErrorMessage() { _errorMessage.value = null }
    fun clearSuccessMessage() { _successMessage.value = null }
}
