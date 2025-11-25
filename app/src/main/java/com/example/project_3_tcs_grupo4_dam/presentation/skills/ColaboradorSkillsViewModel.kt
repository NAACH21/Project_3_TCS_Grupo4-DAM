package com.example.project_3_tcs_grupo4_dam.presentation.skills

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.project_3_tcs_grupo4_dam.data.local.SessionManager
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorDtos.SkillReadDto
import com.example.project_3_tcs_grupo4_dam.data.model.SolicitudCreateDto
import com.example.project_3_tcs_grupo4_dam.data.model.CambioSkillPropuestaCreateDto
import com.example.project_3_tcs_grupo4_dam.data.model.RoleDto
import com.example.project_3_tcs_grupo4_dam.data.model.UsuarioDto
import com.example.project_3_tcs_grupo4_dam.data.remote.ColaboradorApiService
import com.example.project_3_tcs_grupo4_dam.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class ColaboradorSkillsUiState(
    val isLoading: Boolean = false,
    val skills: List<SkillReadDto> = emptyList(),
    val errorMessage: String? = null
)

class ColaboradorSkillsViewModel(
    private val apiService: ColaboradorApiService,
    private val sessionManager: SessionManager,
    private val colaboradorId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(ColaboradorSkillsUiState())
    val uiState: StateFlow<ColaboradorSkillsUiState> = _uiState.asStateFlow()
    
    // Usamos Solicitudes y Alertas, NO SkillApi
    private val solicitudesApi = RetrofitClient.solicitudesApi
    private val alertasApi = RetrofitClient.alertasApi
    private val authApi = RetrofitClient.authApi

    init {
        fetchSkills()
    }

    private fun fetchSkills() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val colaborador = apiService.getColaboradorById(colaboradorId)
                _uiState.value = _uiState.value.copy(
                    skills = colaborador.skills,
                    isLoading = false
                )
            } catch (e: Exception) {
                Log.e("ColaboradorSkillsVM", "Error al obtener skills: ${e.message}", e)
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Error al cargar skills: ${e.message}",
                    isLoading = false
                )
            }
        }
    }
    
    // --- AGREGAR SKILL VÍA SOLICITUD (POST /api/Solicitudes) ---
    fun crearSkill(nombre: String, tipo: String, nivel: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                // 1. Obtener Admins para notificación
                val rolesResponse = authApi.getRoles()
                val roles = rolesResponse.data ?: emptyList()
                val adminRoleId = roles.find { it.nombreRol.equals("ADMIN", ignoreCase = true) }?.id
                
                val adminIds = if (adminRoleId != null) {
                    val usuariosResponse = authApi.getUsuarios()
                    val usuarios = usuariosResponse.data ?: emptyList()
                    usuarios.filter { it.rolId == adminRoleId }.map { it.id }
                } else {
                    emptyList()
                }

                // 2. Crear Solicitud
                // Se usa un CambioSkillPropuesta simulando "Nivel Actual" como null (porque es nuevo)
                val cambioDto = CambioSkillPropuestaCreateDto(
                    nombre = nombre,
                    tipo = tipo,
                    nivelActual = null, // No tiene nivel actual
                    nivelPropuesto = nivel,
                    esCriticoActual = false,
                    esCriticoPropuesto = false,
                    motivo = "Solicitud de nuevo skill por colaborador"
                )
                
                val solicitudDto = SolicitudCreateDto(
                    tipoSolicitudGeneral = "ACTUALIZACION_SKILLS",
                    tipoSolicitud = "NUEVO_SKILL", // Diferenciador opcional
                    colaboradorId = colaboradorId,
                    certificacionIdAnterior = null,
                    certificacionPropuesta = null,
                    datosEntrevistaPropuesta = null,
                    cambiosSkillsPropuestos = listOf(cambioDto),
                    creadoPorUsuarioId = sessionManager.getUsuarioId()
                )
                
                val responseSolicitud = solicitudesApi.createSolicitud(solicitudDto)
                
                // 3. Crear Alerta
                if (responseSolicitud.isSuccessful) {
                     val fechaProxima = getSevenDaysFromNowIsoDate()
                     val descripcionTexto = "Solicitud de NUEVO skill: $nombre (Nivel $nivel). Tipo: $tipo"
                     
                     val destinatariosList = adminIds.map { adminId ->
                        mapOf("usuarioId" to adminId, "tipo" to "ADMIN")
                     }
                     
                     val alertaBody = mapOf(
                        "tipo" to "SOLICITUD_ACTUALIZACION_SKILL",
                        "estado" to "PENDIENTE",
                        "colaboradorId" to colaboradorId,
                        "vacanteId" to null,
                        "procesoMatchingId" to null,
                        "usuarioResponsable" to (sessionManager.getUsuarioId() ?: ""),
                        "destinatarios" to destinatariosList,
                        "detalle" to mapOf(
                            "fechaProximaEvaluacion" to fechaProxima,
                            "skillsFaltantes" to emptyList<Any>(),
                            "nombreSkill" to nombre,
                            "nivelActual" to "Ninguno",
                            "nivelSolicitado" to getNivelLabel(nivel),
                            "descripcion" to descripcionTexto
                        )
                    )
                    
                    alertasApi.createAlerta(alertaBody)
                    
                    // Recargar para asegurar estado limpio (aunque la solicitud es pendiente)
                    fetchSkills() 
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Error al crear solicitud: ${responseSolicitud.code()}"
                    )
                }

            } catch (e: Exception) {
                Log.e("ColaboradorSkillsVM", "Error creando solicitud", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error: ${e.message}"
                )
            }
        }
    }

    private fun getSevenDaysFromNowIsoDate(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 7)
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        return sdf.format(calendar.time)
    }

    private fun getNivelLabel(nivel: Int): String = when(nivel) {
        1 -> "Básico"
        2 -> "Intermedio"
        3 -> "Avanzado"
        4 -> "Experto"
        else -> "Nivel $nivel"
    }
    
    // Funciones de filtro se mantienen igual
    fun filterSkills(searchQuery: String, selectedType: String, selectedStatus: String): List<SkillReadDto> {
        var result = _uiState.value.skills
        if (searchQuery.isNotBlank()) {
            result = result.filter { it.nombre.contains(searchQuery, ignoreCase = true) }
        }
        if (selectedType != "Todos") {
            result = result.filter { it.tipo == selectedType }
        }
        return result
    }
}

class ColaboradorSkillsViewModelFactory(
    private val apiService: ColaboradorApiService,
    private val sessionManager: SessionManager, // Agregamos sessionManager
    private val colaboradorId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ColaboradorSkillsViewModel(apiService, sessionManager, colaboradorId) as T
    }
}
