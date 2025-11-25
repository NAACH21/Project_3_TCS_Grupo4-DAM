package com.example.project_3_tcs_grupo4_dam.presentation.skills

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.project_3_tcs_grupo4_dam.data.local.SessionManager
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorDtos.SkillReadDto
import com.example.project_3_tcs_grupo4_dam.data.remote.RetrofitClient
import com.example.project_3_tcs_grupo4_dam.data.model.RoleDto
import com.example.project_3_tcs_grupo4_dam.data.model.UsuarioDto
import com.example.project_3_tcs_grupo4_dam.data.model.SolicitudCreateDto
import com.example.project_3_tcs_grupo4_dam.data.model.CambioSkillPropuestaCreateDto
import com.example.project_3_tcs_grupo4_dam.data.model.ApiResponse
import com.example.project_3_tcs_grupo4_dam.data.model.AlertaDto
import com.example.project_3_tcs_grupo4_dam.data.remote.AlertasApiService
import com.example.project_3_tcs_grupo4_dam.data.remote.AuthApiService
import com.example.project_3_tcs_grupo4_dam.data.remote.ColaboradorApiService
import com.example.project_3_tcs_grupo4_dam.data.remote.SolicitudesApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ActualizarSkillViewModel(
    private val sessionManager: SessionManager,
    private val colaboradorId: String,
    private val skillName: String
) : ViewModel() {

    // Referencias explícitas a los servicios que SÍ existen
    private val solicitudesApi: SolicitudesApiService = RetrofitClient.solicitudesApi
    private val alertasApi: AlertasApiService = RetrofitClient.alertasApi
    private val authApi: AuthApiService = RetrofitClient.authApi
    private val colaboradorApi: ColaboradorApiService = RetrofitClient.colaboradorApi

    // Estado del formulario
    var selectedNivel by mutableStateOf(0)
    var selectedTipoEvidencia by mutableStateOf("URL")
    var urlEvidencia by mutableStateOf("")
    var notasAdicionales by mutableStateOf("")
    
    var currentNivel by mutableStateOf(1)
    var skillTipo by mutableStateOf("TECNICO")

    // Estados de UI
    var isLoading by mutableStateOf(false)
    var isSuccess by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    
    // Estado del Skill
    private val _skillState = MutableStateFlow<SkillReadDto?>(null)
    val skillState = _skillState.asStateFlow()

    init {
        cargarDatosIniciales()
    }

    private fun cargarDatosIniciales() {
        viewModelScope.launch {
            isLoading = true
            try {
                val colab = colaboradorApi.getColaboradorById(colaboradorId)
                val skill = colab.skills.find { it.nombre.equals(skillName, ignoreCase = true) }
                
                if (skill != null) {
                    _skillState.value = skill
                    currentNivel = skill.nivel
                    selectedNivel = skill.nivel 
                    skillTipo = skill.tipo
                } else {
                    errorMessage = "Skill no encontrado"
                }
            } catch (e: Exception) {
                errorMessage = "Error cargando datos: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun enviarValidacion() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                // 1. Obtener IDs de Administradores para la alerta
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

                // 2. Crear Solicitud (POST /api/Solicitudes)
                // NO MODIFICAMOS EL SKILL DIRECTAMENTE. Creamos una solicitud.
                
                val cambioDto = CambioSkillPropuestaCreateDto(
                    nombre = skillName,
                    tipo = skillTipo,
                    nivelActual = currentNivel,
                    nivelPropuesto = selectedNivel,
                    esCriticoActual = false, 
                    esCriticoPropuesto = false,
                    motivo = notasAdicionales
                )
                
                val solicitudDto = SolicitudCreateDto(
                    tipoSolicitudGeneral = "ACTUALIZACION_SKILLS",
                    tipoSolicitud = "AJUSTE_NIVEL",
                    colaboradorId = colaboradorId,
                    certificacionIdAnterior = null,
                    certificacionPropuesta = null,
                    datosEntrevistaPropuesta = null,
                    cambiosSkillsPropuestos = listOf(cambioDto),
                    creadoPorUsuarioId = sessionManager.getUsuarioId()
                )
                
                val responseSolicitud = solicitudesApi.createSolicitud(solicitudDto)

                // 3. Crear Alerta (POST /api/alertas)
                val fechaProxima = getSevenDaysFromNowIsoDate()
                val descripcionTexto = "Solicitud de actualización: $skillName de nivel $currentNivel a $selectedNivel. $notasAdicionales"
                
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
                        "nombreSkill" to skillName,
                        "nivelActual" to getNivelLabel(currentNivel),
                        "nivelSolicitado" to getNivelLabel(selectedNivel),
                        "descripcion" to descripcionTexto
                    )
                )

                // Enviamos alerta independientemente del resultado de la solicitud para asegurar notificación
                val responseAlerta = alertasApi.createAlerta(alertaBody)
                
                if (responseAlerta.success) {
                    isSuccess = true
                } else {
                    // Si la alerta falla pero la solicitud se creó, consideramos éxito parcial
                    if (responseSolicitud.isSuccessful) {
                        isSuccess = true
                        Log.w("ActualizarSkillVM", "Solicitud creada pero alerta falló: ${responseAlerta.message}")
                    } else {
                        errorMessage = "Error al procesar la solicitud"
                    }
                }

            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
                Log.e("ActualizarSkillVM", "Error enviando validación", e)
            } finally {
                isLoading = false
            }
        }
    }
    
    // Utilidad para fecha compatible con API antigua
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
}

class ActualizarSkillViewModelFactory(
    private val sessionManager: SessionManager,
    private val colaboradorId: String,
    private val skillName: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ActualizarSkillViewModel(sessionManager, colaboradorId, skillName) as T
    }
}
