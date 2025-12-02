package com.example.project_3_tcs_grupo4_dam.presentation.solicitud

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_3_tcs_grupo4_dam.data.local.SessionManager
import com.example.project_3_tcs_grupo4_dam.data.model.CambioSkillPropuestaCreateDto
import com.example.project_3_tcs_grupo4_dam.data.model.CatalogoDtos
import com.example.project_3_tcs_grupo4_dam.data.model.CertificacionPropuestaCreateDto
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorDtos.SkillReadDto
import com.example.project_3_tcs_grupo4_dam.data.model.SolicitudCreateDto
import com.example.project_3_tcs_grupo4_dam.data.repository.CatalogoRepository
import com.example.project_3_tcs_grupo4_dam.data.repository.CertificadosRepository
import com.example.project_3_tcs_grupo4_dam.data.repository.ColaboradorRepository
import com.example.project_3_tcs_grupo4_dam.data.repository.SolicitudesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

/**
 * ViewModel para gestionar la creación de solicitudes de ACTUALIZACION_SKILLS
 * Flujo completo: Seleccionar skill (existente/nueva) + Subir certificado PDF + Crear solicitud
 */
class SolicitudActualizacionSkillsViewModel(
    private val solicitudesRepository: SolicitudesRepository,
    private val certificadosRepository: CertificadosRepository,
    private val colaboradorRepository: ColaboradorRepository,
    private val catalogoRepository: CatalogoRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val colaboradorId = sessionManager.getColaboradorId() ?: ""
    private val usuarioId = sessionManager.getUsuarioId() ?: ""

    // Estados de carga y mensajes
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    // Skills actuales del colaborador
    private val _misSkillsActuales = MutableStateFlow<List<SkillReadDto>>(emptyList())
    val misSkillsActuales: StateFlow<List<SkillReadDto>> = _misSkillsActuales.asStateFlow()

    // Catálogo de skills
    private val _skillsCatalogo = MutableStateFlow<List<CatalogoDtos.SkillCatalogItemDto>>(emptyList())
    val skillsCatalogo: StateFlow<List<CatalogoDtos.SkillCatalogItemDto>> = _skillsCatalogo.asStateFlow()

    private val _nivelesSkill = MutableStateFlow<List<CatalogoDtos.NivelSkillDto>>(emptyList())
    val nivelesSkill: StateFlow<List<CatalogoDtos.NivelSkillDto>> = _nivelesSkill.asStateFlow()

    // Modo: ¿Skill nueva o actualizar existente?
    private val _esSkillNueva = MutableStateFlow(false)
    val esSkillNueva: StateFlow<Boolean> = _esSkillNueva.asStateFlow()

    // Para skill existente
    private val _skillSeleccionada = MutableStateFlow<SkillReadDto?>(null)
    val skillSeleccionada: StateFlow<SkillReadDto?> = _skillSeleccionada.asStateFlow()

    // Para skill nueva
    private val _nombreSkillNueva = MutableStateFlow("")
    val nombreSkillNueva: StateFlow<String> = _nombreSkillNueva.asStateFlow()

    private val _tipoSkillNueva = MutableStateFlow("")
    val tipoSkillNueva: StateFlow<String> = _tipoSkillNueva.asStateFlow()

    // Propuesta de cambio (común para ambos casos)
    private val _nivelPropuesto = MutableStateFlow(1)
    val nivelPropuesto: StateFlow<Int> = _nivelPropuesto.asStateFlow()

    private val _esCriticoPropuesto = MutableStateFlow(false)
    val esCriticoPropuesto: StateFlow<Boolean> = _esCriticoPropuesto.asStateFlow()

    private val _motivo = MutableStateFlow("")
    val motivo: StateFlow<String> = _motivo.asStateFlow()

    // Certificación propuesta (OBLIGATORIA)
    private val _nombreCertificacion = MutableStateFlow("")
    val nombreCertificacion: StateFlow<String> = _nombreCertificacion.asStateFlow()

    private val _institucionCertificacion = MutableStateFlow("")
    val institucionCertificacion: StateFlow<String> = _institucionCertificacion.asStateFlow()

    private val _fechaObtencion = MutableStateFlow<String?>(null)
    val fechaObtencion: StateFlow<String?> = _fechaObtencion.asStateFlow()

    private val _fechaVencimiento = MutableStateFlow<String?>(null)
    val fechaVencimiento: StateFlow<String?> = _fechaVencimiento.asStateFlow()

    private val _pdfSeleccionado = MutableStateFlow<File?>(null)
    val pdfSeleccionado: StateFlow<File?> = _pdfSeleccionado.asStateFlow()

    private val _pdfSubiendo = MutableStateFlow(false)
    val pdfSubiendo: StateFlow<Boolean> = _pdfSubiendo.asStateFlow()

    private val _archivoPdfUrl = MutableStateFlow<String?>(null)

    init {
        Log.d(TAG, "=== INICIALIZACIÓN ===")
        Log.d(TAG, "ColaboradorId: $colaboradorId")
        Log.d(TAG, "UsuarioId: $usuarioId")

        cargarDatosIniciales()
    }

    private fun cargarDatosIniciales() {
        viewModelScope.launch {
            try {
                // Cargar skills actuales del colaborador
                val colaborador = colaboradorRepository.getColaboradorById(colaboradorId)
                _misSkillsActuales.value = colaborador.skills
                Log.d(TAG, "✅ Skills actuales cargadas: ${colaborador.skills.size}")

                // Cargar catálogo
                _skillsCatalogo.value = catalogoRepository.getSkillsCatalogo()
                _nivelesSkill.value = catalogoRepository.getNivelesSkill()
                Log.d(TAG, "✅ Catálogo cargado: ${_skillsCatalogo.value.size} skills")
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error al cargar datos iniciales", e)
                _errorMessage.value = "Error al cargar datos: ${e.message}"
            }
        }
    }

    // ========== EVENTOS DE UI ==========

    fun onTipoSkillChange(esNueva: Boolean) {
        _esSkillNueva.value = esNueva
        // Limpiar datos al cambiar de modo
        _skillSeleccionada.value = null
        _nombreSkillNueva.value = ""
        _tipoSkillNueva.value = ""
        _nivelPropuesto.value = 1
        _esCriticoPropuesto.value = false
        _motivo.value = ""
    }

    fun onSkillExistenteSeleccionada(skill: SkillReadDto) {
        _skillSeleccionada.value = skill
        // Precargar nivel propuesto = nivel actual + 1
        _nivelPropuesto.value = minOf(skill.nivel + 1, 5)
        _esCriticoPropuesto.value = skill.esCritico
        Log.d(TAG, "Skill existente seleccionada: ${skill.nombre}, nivel: ${skill.nivel}")
    }

    fun onNombreSkillNuevaChange(nombre: String) {
        _nombreSkillNueva.value = nombre
    }

    fun onSkillCatalogoSeleccionada(skillCatalog: CatalogoDtos.SkillCatalogItemDto) {
        _nombreSkillNueva.value = skillCatalog.nombre
        _tipoSkillNueva.value = skillCatalog.tipo
        Log.d(TAG, "Skill del catálogo seleccionada: ${skillCatalog.nombre}, tipo: ${skillCatalog.tipo}")
    }

    fun onTipoSkillNuevaChange(tipo: String) {
        _tipoSkillNueva.value = tipo
    }

    fun onNivelPropuestoChange(nivel: Int) {
        _nivelPropuesto.value = nivel
    }

    fun onEsCriticoChange(esCritico: Boolean) {
        _esCriticoPropuesto.value = esCritico
    }

    fun onMotivoChange(motivo: String) {
        _motivo.value = motivo
    }

    fun onNombreCertificacionChange(nombre: String) {
        _nombreCertificacion.value = nombre
    }

    fun onInstitucionCertificacionChange(institucion: String) {
        _institucionCertificacion.value = institucion
    }

    fun onFechaObtencionChange(fecha: String) {
        _fechaObtencion.value = fecha
    }

    fun onFechaVencimientoChange(fecha: String) {
        _fechaVencimiento.value = fecha
    }

    fun onPdfSeleccionado(file: File) {
        _pdfSeleccionado.value = file
        Log.d(TAG, "PDF seleccionado: ${file.name}")
        // Subir automáticamente
        subirPdf(file)
    }

    private fun subirPdf(file: File) {
        viewModelScope.launch {
            _pdfSubiendo.value = true
            try {
                Log.d(TAG, "📤 Subiendo PDF: ${file.name}")

                val result = certificadosRepository.uploadCertificado(
                    file = file,
                    colaboradorId = colaboradorId,
                    nombreCertificacion = _nombreCertificacion.value.ifBlank { null }
                )

                result.onSuccess { response ->
                    _archivoPdfUrl.value = response.archivoPdfUrl
                    Log.d(TAG, "✅ PDF subido exitosamente: ${response.archivoPdfUrl}")
                }

                result.onFailure { exception ->
                    Log.e(TAG, "❌ Error al subir PDF", exception)
                    _errorMessage.value = exception.message ?: "Error al subir PDF"
                    _pdfSeleccionado.value = null
                }
            } finally {
                _pdfSubiendo.value = false
            }
        }
    }

    // ========== VALIDACIÓN Y ENVÍO ==========

    fun enviarSolicitud() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                // Validaciones
                if (!validarFormulario()) {
                    _isLoading.value = false
                    return@launch
                }

                Log.d(TAG, "=== CREANDO SOLICITUD ACTUALIZACION_SKILLS ===")

                // Construir CambioSkillPropuestaCreateDto
                val cambioSkill = if (_esSkillNueva.value) {
                    // Skill nueva
                    CambioSkillPropuestaCreateDto(
                        nombre = _nombreSkillNueva.value,
                        tipo = convertirTipoSkillABackend(_tipoSkillNueva.value),
                        nivelActual = null,
                        nivelPropuesto = _nivelPropuesto.value,
                        esCriticoActual = null,
                        esCriticoPropuesto = _esCriticoPropuesto.value,
                        motivo = _motivo.value.ifBlank { null }
                    )
                } else {
                    // Skill existente
                    val skill = _skillSeleccionada.value!!
                    CambioSkillPropuestaCreateDto(
                        nombre = skill.nombre,
                        tipo = skill.tipo,
                        nivelActual = skill.nivel,
                        nivelPropuesto = _nivelPropuesto.value,
                        esCriticoActual = skill.esCritico,
                        esCriticoPropuesto = _esCriticoPropuesto.value,
                        motivo = _motivo.value.ifBlank { null }
                    )
                }

                Log.d(TAG, "CambioSkill: ${cambioSkill.nombre}, nivel: ${cambioSkill.nivelActual} → ${cambioSkill.nivelPropuesto}")

                // Construir CertificacionPropuestaCreateDto
                val certificacion = CertificacionPropuestaCreateDto(
                    nombre = _nombreCertificacion.value,
                    institucion = _institucionCertificacion.value,
                    fechaObtencion = _fechaObtencion.value,
                    fechaVencimiento = _fechaVencimiento.value,
                    archivoPdfUrl = _archivoPdfUrl.value!!
                )

                Log.d(TAG, "Certificación: ${certificacion.nombre}, PDF: ${certificacion.archivoPdfUrl}")

                // Construir SolicitudCreateDto
                val solicitud = SolicitudCreateDto(
                    tipoSolicitudGeneral = "ACTUALIZACION_SKILLS",
                    tipoSolicitud = if (_esSkillNueva.value) "NUEVA_SKILL" else "ACTUALIZACION_SKILL",
                    colaboradorId = colaboradorId,
                    certificacionIdAnterior = null,
                    certificacionPropuesta = certificacion,
                    datosEntrevistaPropuesta = null,
                    cambiosSkillsPropuestos = listOf(cambioSkill),
                    creadoPorUsuarioId = usuarioId
                )

                Log.d(TAG, "Solicitud completa construida. Enviando al backend...")

                // Llamar al repositorio
                val solicitudCreada = solicitudesRepository.createSolicitud(solicitud)

                Log.d(TAG, "✅ SOLICITUD CREADA EXITOSAMENTE: ${solicitudCreada.id}")
                _successMessage.value = "Solicitud creada exitosamente"

            } catch (e: Exception) {
                Log.e(TAG, "❌ ERROR AL CREAR SOLICITUD", e)
                _errorMessage.value = e.message ?: "Error al crear solicitud"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun validarFormulario(): Boolean {
        // Validar skill
        if (!_esSkillNueva.value) {
            if (_skillSeleccionada.value == null) {
                _errorMessage.value = "Debes seleccionar una skill existente"
                return false
            }
        } else {
            if (_nombreSkillNueva.value.isBlank()) {
                _errorMessage.value = "Debes ingresar el nombre de la skill nueva"
                return false
            }
            if (_tipoSkillNueva.value.isBlank()) {
                _errorMessage.value = "Debes seleccionar el tipo de skill"
                return false
            }
        }

        // Validar certificación
        if (_nombreCertificacion.value.isBlank()) {
            _errorMessage.value = "Debes ingresar el nombre del certificado"
            return false
        }
        if (_institucionCertificacion.value.isBlank()) {
            _errorMessage.value = "Debes ingresar la institución del certificado"
            return false
        }
        if (_archivoPdfUrl.value.isNullOrBlank()) {
            _errorMessage.value = "Debes subir un archivo PDF del certificado"
            return false
        }

        return true
    }

    private fun convertirTipoSkillABackend(tipoUI: String): String {
        return when (tipoUI) {
            "Técnica" -> "TECNICO"
            "Blanda" -> "BLANDO"
            else -> tipoUI.uppercase()
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun clearSuccess() {
        _successMessage.value = null
    }

    fun onError(message: String) {
        _errorMessage.value = message
    }

    companion object {
        private const val TAG = "SolicitudSkillsVM"
    }
}

