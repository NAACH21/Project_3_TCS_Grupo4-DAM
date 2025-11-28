package com.example.project_3_tcs_grupo4_dam.presentation.notificaciones

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_3_tcs_grupo4_dam.data.local.LocalAlertManager
import com.example.project_3_tcs_grupo4_dam.data.local.SessionManager
import com.example.project_3_tcs_grupo4_dam.data.model.AlertaDto
import com.example.project_3_tcs_grupo4_dam.data.remote.RetrofitClient
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
    private val context: android.content.Context // Necesitamos el contexto para LocalAlertManager
) : ViewModel() {

    private val apiService = RetrofitClient.alertasApi
    private val localManager = LocalAlertManager(context) // Manager para persistencia local

    // Estado con la lista enriquecida (DTO + Visto)
    private val _alertasUi = MutableStateFlow<List<AlertaUiState>>(emptyList())
    val alertasUi = _alertasUi.asStateFlow()

    // Badge real calculado
    private val _unreadCount = MutableStateFlow(0)
    val unreadCount = _unreadCount.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

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

                // 4. CRUCE DE DATOS: API vs LOCAL (Nuevo)
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
}
