package com.example.project_3_tcs_grupo4_dam.presentation.matching

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_3_tcs_grupo4_dam.data.model.*
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorDtos.ColaboradorReadDto
import com.example.project_3_tcs_grupo4_dam.data.remote.RetrofitClient
import com.example.project_3_tcs_grupo4_dam.data.repository.*
import com.example.project_3_tcs_grupo4_dam.domain.MatchingEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*
import kotlinx.coroutines.withContext
import com.example.project_3_tcs_grupo4_dam.data.model.CreateAlertaRequest
import com.example.project_3_tcs_grupo4_dam.data.model.DetalleAlertaRequest
import com.example.project_3_tcs_grupo4_dam.data.local.SessionManager
import com.example.project_3_tcs_grupo4_dam.data.repository.AuthRepositoryImpl
import com.example.project_3_tcs_grupo4_dam.data.repository.AuthRepository

class MatchingViewModel(application: Application) : AndroidViewModel(application) {

    private val vacanteRepo = VacanteRepository(RetrofitClient.vacanteApi)
    private val colaboradorRepo = ColaboradorRepositoryImpl()
    private val procesosRepo = ProcesosMatchingRepository()
    private val alertasApi = RetrofitClient.alertasApi

    // Session + AuthRepository para obtener usuarioId
    private val sessionManager: SessionManager = SessionManager(application)
    private val authRepository: AuthRepository = AuthRepositoryImpl(RetrofitClient.authApi, sessionManager)

    private val _vacantes = MutableStateFlow<List<VacanteResponse>>(emptyList())
    val vacantes: StateFlow<List<VacanteResponse>> = _vacantes

    private val _colaboradores = MutableStateFlow<List<ColaboradorReadDto>>(emptyList())
    val colaboradores: StateFlow<List<ColaboradorReadDto>> = _colaboradores

    private val _resultados = MutableStateFlow<List<ResultadoMatchingItem>>(emptyList())
    val resultados: StateFlow<List<ResultadoMatchingItem>> = _resultados

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    fun loadVacantes() = viewModelScope.launch {
        try {
            _loading.value = true
            val all = vacanteRepo.getVacantes()
            // Mostrar sólo vacantes con estadoVacante == "Activa" (insensible a mayúsculas)
            _vacantes.value = all.filter { it.estadoVacante?.equals("Activa", true) == true }
        } catch (e: Exception) {
            _message.value = "Error cargando vacantes: ${e.message}"
        } finally {
            _loading.value = false
        }
    }

    fun loadColaboradores() = viewModelScope.launch {
        try {
            _loading.value = true
            val cols = colaboradorRepo.getAllColaboradores()
            _colaboradores.value = cols
        } catch (e: Exception) {
            _message.value = "Error cargando colaboradores: ${e.message}"
        } finally {
            _loading.value = false
        }
    }

    fun ejecutarMatching(
        vacante: VacanteResponse,
        umbral: Int,
        guardarProceso: Boolean = true
    ) {
        viewModelScope.launch {
            try {
                _loading.value = true

                val resultados = MatchingEngine.runMatching(
                    colaboradores = _colaboradores.value,
                    vacante = vacante,
                    umbralMinimo = umbral
                )

                _resultados.value = resultados

                if (resultados.isEmpty()) {
                    _message.value = "No se encontraron candidatos con puntaje >= $umbral%"
                    // No guardar proceso si no hay resultados
                    return@launch
                }

                if (guardarProceso) {
                    val req = ProcesoMatchingRequest(
                        vacanteId = vacante.getIdValue(),
                        umbral = umbral,
                        fechaEjecucion = Date(),
                        resultados = resultados
                    )
                    try {
                        procesosRepo.createProceso(req)
                    } catch (e: Exception) {
                        _message.value = "Matching ejecutado, pero error guardando proceso: ${e.message}"
                    }
                }

                _message.value = "Matching ejecutado: ${resultados.size} candidato(s) encontrados"

            } catch (e: Exception) {
                e.printStackTrace()
                _message.value = "Error ejecutando matching: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    // ======================================
    // CREAR ALERTA (POST /api/alertas)
    // ======================================
    suspend fun crearAlerta(
        tipo: String,
        descripcion: String,
        vacanteId: String?
    ) {
        try {
            // Obtener usuarioId desde el SessionManager a través del AuthRepository
            val usuarioId = authRepository.getSessionManager().getUsuarioId()

            val body = CreateAlertaRequest(
                tipo = tipo,
                estado = "pendiente",
                vacanteId = vacanteId,
                detalle = DetalleAlertaRequest(descripcion = descripcion),
                destinatarios = emptyList(),
                usuarioResponsable = usuarioId
            )

            // Llamada de red en IO
            val response = withContext(Dispatchers.IO) {
                alertasApi.createAlerta(body)
            }

            if (response.success) {
                _message.value = "Alerta creada correctamente: ${response.data?.tipo ?: tipo}"
            } else {
                _message.value = "No se pudo crear la alerta: ${response.message ?: "Error desconocido"}"
            }

        } catch (e: Exception) {
            _message.value = "Error al crear alerta: ${e.message}"
        }
    }
}
