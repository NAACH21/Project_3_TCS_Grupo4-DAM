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

    // Estado con la lista enriquecida (DTO + Visto)
    private val _alertasUi = MutableStateFlow<List<AlertaUiState>>(emptyList())
    val alertasUi = _alertasUi.asStateFlow()

    // ==================== NUEVO: Dashboard State ====================
    private val _alertasDashboard = MutableStateFlow<List<AlertaDashboard>>(emptyList())
    val alertasDashboard = _alertasDashboard.asStateFlow()

    // Badge real calculado
    private val _unreadCount = MutableStateFlow(0)
    val unreadCount = _unreadCount.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    // ==================== NUEVO: Error State ====================
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    // ==================== NUEVO: Success Message State ====================
    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage = _successMessage.asStateFlow()

    init {
        cargarAlertas()
    }

    /**
     * Función auxiliar para extraer el ID ya sea string o objeto Mongo { $oid: "..." }
     */
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

    // ==================== NUEVO: Cargar Dashboard de Notificaciones ====================
    /**
     * Carga las notificaciones del dashboard según el rol del usuario
     * @param esAdmin Si es true, carga dashboard de admin, si false carga del colaborador
     * @param userId ID del colaborador (obligatorio si esAdmin = false)
     */
    fun cargarNotificaciones(esAdmin: Boolean, userId: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val result = notificacionesRepository.obtenerDashboard(esAdmin, userId)

                result.onSuccess { alertas ->
                    _alertasDashboard.value = alertas
                    // Actualizar contador de no leídas (activas)
                    _unreadCount.value = alertas.count { it.activa }
                    Log.d("NotificacionesVM", "Dashboard cargado: ${alertas.size} notificaciones, ${_unreadCount.value} no leídas")
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
     * Marca una notificación del dashboard como leída (actualiza solo estado local)
     * En un sistema real, esto debería sincronizar con el backend
     */
    fun marcarDashboardComoLeida(idReferencia: String) {
        viewModelScope.launch {
            try {
                // Actualizar el estado local
                val updatedList = _alertasDashboard.value.map { alerta ->
                    if (alerta.idReferencia == idReferencia) {
                        alerta.copy(activa = false)
                    } else {
                        alerta
                    }
                }

                _alertasDashboard.value = updatedList
                _unreadCount.value = updatedList.count { it.activa }

                Log.d("NotificacionesVM", "Notificación $idReferencia marcada como leída")
            } catch (e: Exception) {
                Log.e("NotificacionesVM", "Error marcando notificación como leída", e)
            }
        }
    }

    fun cargarAlertas() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 1. Obtener datos sesión
                val rolUsuario = sessionManager.getRol() ?: "INVITADO"
                val rawColaboradorId = sessionManager.getColaboradorId()
                val rawUsuarioId = sessionManager.getUsuarioId()
                val miColaboradorId = rawColaboradorId?.trim()
                val miUsuarioId = rawUsuarioId?.trim()

                // 2. Obtener TODAS las alertas (Backend)
                val response = apiService.getAllAlertas()
                val todasLasAlertas = response.data ?: emptyList()

                // 3. Filtrar alertas relevantes para este usuario (Lógica original)
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
                } else {
                    todasLasAlertas.filter { alerta ->
                        alerta.destinatarios?.any { 
                            it.tipo?.equals(rolUsuario, ignoreCase = true) == true 
                        } == true
                    }
                }

                // 4. CRUCE DE DATOS: API vs LOCAL
                val readIds = localManager.getReadIds()
                
                val uiList = alertasFiltradas.map { alerta ->
                    val id = extraerId(alerta.id) ?: ""
                    val isRead = readIds.contains(id)
                    
                    AlertaUiState(
                        alerta = alerta,
                        isVisto = isRead
                    )
                }

                // 5. Actualizar estados UI
                _alertasUi.value = uiList
                _unreadCount.value = uiList.count { !it.isVisto }

            } catch (e: Exception) {
                Log.e("NotificacionesVM", "Error cargando alertas", e)
                _alertasUi.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Acción de usuario: Marcar como visto
    fun marcarComoVisto(alertaId: String) {
        // 1. Persistir localmente
        localManager.markAsRead(alertaId)

        // 2. Actualizar estado en memoria inmediatamente para UI responsiva
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

    /**
     * Limpia el mensaje de error
     */
    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    /**
     * Limpia el mensaje de éxito
     */
    fun clearSuccessMessage() {
        _successMessage.value = null
    }
}
