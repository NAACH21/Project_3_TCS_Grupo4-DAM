package com.example.project_3_tcs_grupo4_dam.presentation.notificaciones

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_3_tcs_grupo4_dam.data.local.SessionManager
import com.example.project_3_tcs_grupo4_dam.data.model.AlertaDto
import com.example.project_3_tcs_grupo4_dam.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NotificacionesViewModel(
    private val sessionManager: SessionManager
) : ViewModel() {

    private val apiService = RetrofitClient.alertasApi

    private val _alertas = MutableStateFlow<List<AlertaDto>>(emptyList())
    val alertas = _alertas.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        cargarAlertas()
    }

    /**
     * Función auxiliar para extraer el ID ya sea string o objeto Mongo { $oid: "..." }
     * Se ha simplificado para ser más estable y no depender de clases internas de GSON.
     */
    private fun extraerId(valor: Any?): String? {
        if (valor == null) return null
        
        return try {
            when (valor) {
                is String -> valor
                is Map<*, *> -> valor["\$oid"]?.toString() ?: valor["oid"]?.toString()
                // LinkedTreeMap implementa Map, por lo que entra en el caso anterior de forma segura
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
                // 1. Obtener datos del usuario logueado
                val rolUsuario = sessionManager.getRol() ?: "INVITADO"
                val rawColaboradorId = sessionManager.getColaboradorId()
                val rawUsuarioId = sessionManager.getUsuarioId()

                // Normalizar IDs locales
                val miColaboradorId = rawColaboradorId?.trim()
                val miUsuarioId = rawUsuarioId?.trim()

                Log.d("NotificacionesVM", "--- INICIO CARGA ALERTAS ---")
                Log.d("NotificacionesVM", "Usuario Logueado -> Rol: '$rolUsuario', ColabID: '$miColaboradorId', UserID: '$miUsuarioId'")

                // 2. Obtener TODAS las alertas
                val response = apiService.getAllAlertas()
                val todasLasAlertas = response.data ?: emptyList()
                Log.d("NotificacionesVM", "Backend devolvió: ${todasLasAlertas.size} alertas totales")

                // 3. Filtrar en memoria
                val alertasFiltradas = if (rolUsuario.equals("COLABORADOR", ignoreCase = true)) {
                    todasLasAlertas.filter { alerta ->
                        // A. Extraer ColaboradorId de la alerta (maneja string u objeto)
                        val alertaColabId = extraerId(alerta.colaboradorId)
                        
                        val esParaMiColaborador = !miColaboradorId.isNullOrEmpty() && 
                                                  !alertaColabId.isNullOrEmpty() &&
                                                  alertaColabId == miColaboradorId

                        // B. Extraer UsuarioId de los destinatarios
                        val soyDestinatarioDirecto = alerta.destinatarios?.any { dest ->
                            val destUsuarioId = extraerId(dest.usuarioId)
                            !miUsuarioId.isNullOrEmpty() && 
                            !destUsuarioId.isNullOrEmpty() &&
                            destUsuarioId == miUsuarioId
                        } == true

                        if (esParaMiColaborador) Log.d("NotificacionesVM", "MATCH Colaborador: ${alerta.id}")
                        if (soyDestinatarioDirecto) Log.d("NotificacionesVM", "MATCH Destinatario: ${alerta.id}")

                        esParaMiColaborador || soyDestinatarioDirecto
                    }
                } else {
                    // Admin / Manager
                    todasLasAlertas.filter { alerta ->
                        alerta.destinatarios?.any { 
                            it.tipo?.equals(rolUsuario, ignoreCase = true) == true 
                        } == true
                    }
                }

                Log.d("NotificacionesVM", "Final: Mostrando ${alertasFiltradas.size} alertas")
                _alertas.value = alertasFiltradas

            } catch (e: Exception) {
                Log.e("NotificacionesVM", "Error cargando alertas", e)
                _alertas.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
