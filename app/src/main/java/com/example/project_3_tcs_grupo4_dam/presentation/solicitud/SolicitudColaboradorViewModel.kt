package com.example.project_3_tcs_grupo4_dam.presentation.solicitud

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_3_tcs_grupo4_dam.data.local.SessionManager
import com.example.project_3_tcs_grupo4_dam.data.model.CambioSkillPropuestaCreateDto
import com.example.project_3_tcs_grupo4_dam.data.model.CertificacionPropuestaCreateDto
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorDtos.SkillReadDto
import com.example.project_3_tcs_grupo4_dam.data.model.NivelSkillDto
import com.example.project_3_tcs_grupo4_dam.data.model.SkillCatalogItemDto
import com.example.project_3_tcs_grupo4_dam.data.model.SolicitudCreateDto
import com.example.project_3_tcs_grupo4_dam.data.model.SolicitudReadDto
import com.example.project_3_tcs_grupo4_dam.data.model.SolicitudUpdateEstadoDto
import com.example.project_3_tcs_grupo4_dam.data.repository.CatalogoRepository
import com.example.project_3_tcs_grupo4_dam.data.repository.SolicitudesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SolicitudColaboradorViewModel(
    private val solicitudesRepository: SolicitudesRepository,
    private val catalogoRepository: CatalogoRepository,
    sessionManager: SessionManager
) : ViewModel() {

    private val colaboradorId = sessionManager.getColaboradorId() ?: ""
    private val usuarioId = sessionManager.getUsuarioId() ?: ""
    private val rolUsuario = sessionManager.getRol()?.uppercase() ?: ""

    // Estados principales
    private val _todasSolicitudes = MutableStateFlow<List<SolicitudReadDto>>(emptyList())

    private val _solicitudesFiltradas = MutableStateFlow<List<SolicitudReadDto>>(emptyList())
    val solicitudesFiltradas: StateFlow<List<SolicitudReadDto>> = _solicitudesFiltradas.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Filtros - Solo CERTIFICACION y ACTUALIZACION_SKILLS para colaborador
    private val _filtroTipo = MutableStateFlow("TODOS")
    val filtroTipo: StateFlow<String> = _filtroTipo.asStateFlow()

    private val _filtroEstado = MutableStateFlow("TODOS")
    val filtroEstado: StateFlow<String> = _filtroEstado.asStateFlow()

    // Diálogo de nueva solicitud
    private val _isDialogNuevaSolicitudOpen = MutableStateFlow(false)
    val isDialogNuevaSolicitudOpen: StateFlow<Boolean> = _isDialogNuevaSolicitudOpen.asStateFlow()

    // Catálogo
    // FIXED: Cambio de CatalogoDtos.SkillCatalogItemDto a SkillCatalogItemDto (top-level)
    private val _skillsCatalogo = MutableStateFlow<List<SkillCatalogItemDto>>(emptyList())
    val skillsCatalogo: StateFlow<List<SkillCatalogItemDto>> = _skillsCatalogo.asStateFlow()

    // FIXED: Cambio de CatalogoDtos.NivelSkillDto a NivelSkillDto (top-level)
    private val _nivelesSkill = MutableStateFlow<List<NivelSkillDto>>(emptyList())
    val nivelesSkill: StateFlow<List<NivelSkillDto>> = _nivelesSkill.asStateFlow()

    // Skills actuales del colaborador
    private val _misSkillsActuales = MutableStateFlow<List<SkillReadDto>>(emptyList())
    val misSkillsActuales: StateFlow<List<SkillReadDto>> = _misSkillsActuales.asStateFlow()

    // Solicitud seleccionada para detalle
    private val _solicitudSeleccionada = MutableStateFlow<SolicitudReadDto?>(null)
    val solicitudSeleccionada: StateFlow<SolicitudReadDto?> = _solicitudSeleccionada.asStateFlow()

    private val _showDetalleDialog = MutableStateFlow(false)
    val showDetalleDialog: StateFlow<Boolean> = _showDetalleDialog.asStateFlow()

    init {
        // Validar que el usuario sea colaborador
        android.util.Log.d("SolicitudViewModel", "=== INICIALIZACIÓN ===")
        android.util.Log.d("SolicitudViewModel", "Rol usuario: $rolUsuario")
        android.util.Log.d("SolicitudViewModel", "ColaboradorId: $colaboradorId")
        android.util.Log.d("SolicitudViewModel", "UsuarioId: $usuarioId")

        if (rolUsuario != "COLABORADOR") {
            android.util.Log.e("SolicitudViewModel", "Error: Usuario no es colaborador. Rol: $rolUsuario")
            _errorMessage.value = "Esta pantalla es solo para colaboradores"
        } else if (usuarioId.isBlank()) {
            android.util.Log.e("SolicitudViewModel", "ERROR CRÍTICO: usuarioId está vacío")
            _errorMessage.value = "Error de sesión: No se pudo obtener el ID de usuario. Por favor, cierra sesión e inicia nuevamente."
        } else if (colaboradorId.isBlank()) {
            android.util.Log.e("SolicitudViewModel", "ERROR CRÍTICO: colaboradorId está vacío")
            _errorMessage.value = "Error de sesión: No se pudo obtener el ID de colaborador. Por favor, cierra sesión e inicia nuevamente."
        } else {
            android.util.Log.d("SolicitudViewModel", "✅ Inicialización correcta, cargando datos...")
            cargarSolicitudes()
            cargarCatalogo()
            cargarSkillsActuales()
        }
    }

    private fun cargarSkillsActuales() {
        viewModelScope.launch {
            try {
                val colaboradorRepository = com.example.project_3_tcs_grupo4_dam.data.repository.ColaboradorRepositoryImpl()
                val colaborador = colaboradorRepository.getColaboradorById(colaboradorId)
                _misSkillsActuales.value = colaborador.skills
                android.util.Log.d("SolicitudViewModel", "Skills actuales cargadas: ${colaborador.skills.size}")
            } catch (e: Exception) {
                android.util.Log.e("SolicitudViewModel", "Error al cargar skills actuales", e)
                _misSkillsActuales.value = emptyList()
            }
        }
    }

    fun cargarSolicitudes() {
        if (rolUsuario != "COLABORADOR") {
            android.util.Log.w("SolicitudViewModel", "Intento de carga con rol no válido: $rolUsuario")
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                android.util.Log.d("SolicitudViewModel", "=== INICIANDO CARGA DE SOLICITUDES ===")
                android.util.Log.d("SolicitudViewModel", "Colaborador ID: $colaboradorId")

                // Usar el endpoint específico del colaborador
                val solicitudes = solicitudesRepository.getSolicitudesByColaborador(colaboradorId)

                android.util.Log.d("SolicitudViewModel", "Solicitudes recibidas del backend: ${solicitudes.size}")
                solicitudes.forEachIndexed { index, sol ->
                    android.util.Log.d("SolicitudViewModel", "  [$index] ID: ${sol.id}, Tipo: ${sol.tipoSolicitudGeneral}, Estado: ${sol.estadoSolicitud}")
                }

                // Filtrar SOLO solicitudes de CERTIFICACION y ACTUALIZACION_SKILLS
                // NO mostrar ENTREVISTA_DESEMPENO en esta pantalla
                val solicitudesColaborador = solicitudes.filter {
                    it.tipoSolicitudGeneral == "CERTIFICACION" ||
                    it.tipoSolicitudGeneral == "ACTUALIZACION_SKILLS"
                }

                android.util.Log.d("SolicitudViewModel", "Solicitudes filtradas (CERT + SKILLS): ${solicitudesColaborador.size}")

                // Ordenar por fecha de creación descendente (más reciente primero)
                _todasSolicitudes.value = solicitudesColaborador.sortedByDescending { it.fechaCreacion }

                android.util.Log.d("SolicitudViewModel", "Solicitudes ordenadas y guardadas en _todasSolicitudes")

                aplicarFiltros()

                android.util.Log.d("SolicitudViewModel", "=== CARGA COMPLETADA EXITOSAMENTE ===")
            } catch (e: Exception) {
                android.util.Log.e("SolicitudViewModel", "=== ERROR AL CARGAR SOLICITUDES ===", e)
                android.util.Log.e("SolicitudViewModel", "Mensaje: ${e.message}")
                android.util.Log.e("SolicitudViewModel", "Causa: ${e.cause}")
                _errorMessage.value = "Error al cargar solicitudes: ${e.message}"
            } finally {
                _isLoading.value = false
                android.util.Log.d("SolicitudViewModel", "Loading finalizado")
            }
        }
    }

    private fun cargarCatalogo() {
        viewModelScope.launch {
            try {
                // FIXED: Ahora getSkillsCatalogo() devuelve List<SkillCatalogItemDto> (top-level)
                _skillsCatalogo.value = catalogoRepository.getSkillsCatalogo()
                // FIXED: Ahora getNivelesSkill() devuelve List<NivelSkillDto> (top-level)
                _nivelesSkill.value = catalogoRepository.getNivelesSkill()
                android.util.Log.d("SolicitudViewModel", "Catálogo cargado: ${_skillsCatalogo.value.size} skills, ${_nivelesSkill.value.size} niveles")
            } catch (e: Exception) {
                android.util.Log.e("SolicitudViewModel", "Error al cargar catálogo", e)
            }
        }
    }

    fun aplicarFiltroTipo(nuevoTipo: String) {
        android.util.Log.d("SolicitudViewModel", "Aplicando filtro tipo: $nuevoTipo")
        _filtroTipo.value = nuevoTipo
        aplicarFiltros()
    }

    fun aplicarFiltroEstado(nuevoEstado: String) {
        android.util.Log.d("SolicitudViewModel", "Aplicando filtro estado: $nuevoEstado")
        _filtroEstado.value = nuevoEstado
        aplicarFiltros()
    }

    private fun aplicarFiltros() {
        val tipo = _filtroTipo.value
        val estado = _filtroEstado.value

        val solicitudesFiltradas = _todasSolicitudes.value.filter { solicitud ->
            val cumpleTipo = tipo == "TODOS" || solicitud.tipoSolicitudGeneral == tipo
            val cumpleEstado = estado == "TODOS" || solicitud.estadoSolicitud == estado
            cumpleTipo && cumpleEstado
        }

        _solicitudesFiltradas.value = solicitudesFiltradas

        android.util.Log.d("SolicitudViewModel", "Filtros aplicados - Total: ${_todasSolicitudes.value.size}, Filtradas: ${solicitudesFiltradas.size}")
    }

    fun abrirNuevaSolicitud() {
        if (rolUsuario != "COLABORADOR") {
            _errorMessage.value = "Solo los colaboradores pueden crear solicitudes desde esta pantalla"
            return
        }
        _isDialogNuevaSolicitudOpen.value = true
    }

    fun cerrarNuevaSolicitud() {
        _isDialogNuevaSolicitudOpen.value = false
    }

    fun crearSolicitudCertificacion(
        nombre: String,
        institucion: String,
        fechaObtencion: String?,
        fechaVencimiento: String?,
        archivoPdfUrl: String?
    ) {
        if (rolUsuario != "COLABORADOR") {
            _errorMessage.value = "Solo los colaboradores pueden crear solicitudes"
            return
        }

        // ⭐ VALIDACIÓN CRÍTICA: Verificar usuarioId ANTES de crear solicitud ⭐
        if (usuarioId.isBlank()) {
            android.util.Log.e("SolicitudViewModel", "ERROR CRÍTICO: usuarioId está vacío al intentar crear certificación")
            _errorMessage.value = "Error: No se pudo obtener el ID de usuario. Por favor, cierra sesión y vuelve a iniciar."
            return
        }

        if (colaboradorId.isBlank()) {
            android.util.Log.e("SolicitudViewModel", "ERROR CRÍTICO: colaboradorId está vacío al intentar crear certificación")
            _errorMessage.value = "Error: No se pudo obtener el ID de colaborador. Por favor, cierra sesión y vuelve a iniciar."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                android.util.Log.d("SolicitudViewModel", "=== CREANDO SOLICITUD DE CERTIFICACIÓN ===")
                android.util.Log.d("SolicitudViewModel", "Nombre: $nombre")
                android.util.Log.d("SolicitudViewModel", "Institución: $institucion")
                android.util.Log.d("SolicitudViewModel", "ColaboradorId: $colaboradorId")
                android.util.Log.d("SolicitudViewModel", "CreadoPorUsuarioId: $usuarioId")

                val certificacion = CertificacionPropuestaCreateDto(
                    nombre = nombre,
                    institucion = institucion,
                    fechaObtencion = fechaObtencion,
                    fechaVencimiento = fechaVencimiento,
                    archivoPdfUrl = archivoPdfUrl
                )

                val solicitud = SolicitudCreateDto(
                    tipoSolicitudGeneral = "CERTIFICACION",
                    tipoSolicitud = "NUEVA",
                    colaboradorId = colaboradorId,
                    certificacionIdAnterior = null,
                    certificacionPropuesta = certificacion,
                    datosEntrevistaPropuesta = null,
                    cambiosSkillsPropuestos = null,
                    creadoPorUsuarioId = usuarioId // ⭐ USAR EL USUARIO ID CORRECTO ⭐
                )

                android.util.Log.d("SolicitudViewModel", "DTO a enviar: $solicitud")

                val solicitudCreada = solicitudesRepository.createSolicitud(solicitud)

                android.util.Log.d("SolicitudViewModel", "✅ Solicitud de certificación creada exitosamente. ID: ${solicitudCreada.id}")

                cerrarNuevaSolicitud()
                cargarSolicitudes() // Recargar lista
            } catch (e: Exception) {
                android.util.Log.e("SolicitudViewModel", "❌ Error al crear solicitud de certificación", e)
                _errorMessage.value = "Error al crear solicitud: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun crearSolicitudActualizacionSkills(cambiosSkills: List<CambioSkillUiModel>) {
        if (rolUsuario != "COLABORADOR") {
            _errorMessage.value = "Solo los colaboradores pueden crear solicitudes"
            return
        }

        // ⭐ VALIDACIÓN CRÍTICA: Verificar que tenemos usuarioId ⭐
        if (usuarioId.isBlank()) {
            android.util.Log.e("SolicitudViewModel", "ERROR CRÍTICO: usuarioId está vacío al intentar crear skills")
            _errorMessage.value = "Error: No se pudo obtener el ID de usuario. Por favor, cierra sesión y vuelve a iniciar."
            return
        }

        if (colaboradorId.isBlank()) {
            android.util.Log.e("SolicitudViewModel", "ERROR CRÍTICO: colaboradorId está vacío al intentar crear skills")
            _errorMessage.value = "Error: No se pudo obtener el ID de colaborador. Por favor, cierra sesión y vuelve a iniciar."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                android.util.Log.d("SolicitudViewModel", "=== CREANDO SOLICITUD DE SKILLS ===")
                android.util.Log.d("SolicitudViewModel", "Cantidad de cambios: ${cambiosSkills.size}")
                android.util.Log.d("SolicitudViewModel", "ColaboradorId: $colaboradorId")
                android.util.Log.d("SolicitudViewModel", "CreadoPorUsuarioId: $usuarioId")

                // Convertir los modelos UI a DTOs del backend
                val cambiosSkillsDto = cambiosSkills.map { cambio ->
                    android.util.Log.d("SolicitudViewModel", "  - Cambio: ${cambio.nombre} (${cambio.tipo}) -> Nivel: ${cambio.nivelPropuesto}, Crítico: ${cambio.esCriticoPropuesto}")
                    CambioSkillPropuestaCreateDto(
                        nombre = cambio.nombre,
                        tipo = cambio.tipo,
                        nivelActual = if (cambio.esNueva) null else cambio.nivelActual,
                        nivelPropuesto = cambio.nivelPropuesto,
                        esCriticoActual = if (cambio.esNueva) null else cambio.esCriticoActual,
                        esCriticoPropuesto = cambio.esCriticoPropuesto,
                        motivo = cambio.motivo.takeIf { it.isNotBlank() }
                    )
                }

                val solicitud = SolicitudCreateDto(
                    tipoSolicitudGeneral = "ACTUALIZACION_SKILLS",
                    tipoSolicitud = "AJUSTE_NIVEL",
                    colaboradorId = colaboradorId,
                    certificacionIdAnterior = null,
                    certificacionPropuesta = null,
                    datosEntrevistaPropuesta = null,
                    cambiosSkillsPropuestos = cambiosSkillsDto,
                    creadoPorUsuarioId = usuarioId // ⭐ USAR EL USUARIO ID CORRECTO ⭐
                )

                android.util.Log.d("SolicitudViewModel", "DTO a enviar: $solicitud")

                val solicitudCreada = solicitudesRepository.createSolicitud(solicitud)

                android.util.Log.d("SolicitudViewModel", "✅ Solicitud de skills creada exitosamente. ID: ${solicitudCreada.id}")

                cerrarNuevaSolicitud()
                cargarSolicitudes() // Recargar lista
            } catch (e: Exception) {
                android.util.Log.e("SolicitudViewModel", "❌ Error al crear solicitud de skills", e)
                android.util.Log.e("SolicitudViewModel", "Detalles del error: ${e.stackTraceToString()}")
                _errorMessage.value = "Error al crear solicitud: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun mostrarDetalleSolicitud(solicitud: SolicitudReadDto) {
        android.util.Log.d("SolicitudViewModel", "Mostrando detalle de solicitud: ${solicitud.id}")
        _solicitudSeleccionada.value = solicitud
        _showDetalleDialog.value = true
    }

    fun cerrarDetalleSolicitud() {
        _showDetalleDialog.value = false
        _solicitudSeleccionada.value = null
    }

    fun limpiarError() {
        _errorMessage.value = null
    }

    /**
     * Anula una solicitud cambiando su estado a ANULADA
     * Solo disponible para colaboradores con solicitudes en estado PENDIENTE o EN_REVISION
     */
    fun anularSolicitud(solicitudId: String) {
        if (rolUsuario != "COLABORADOR") {
            android.util.Log.e("SolicitudViewModel", "Error: Solo colaboradores pueden anular solicitudes. Rol actual: $rolUsuario")
            _errorMessage.value = "Solo los colaboradores pueden anular sus propias solicitudes"
            return
        }

        if (usuarioId.isBlank()) {
            android.util.Log.e("SolicitudViewModel", "ERROR CRÍTICO: usuarioId está vacío al intentar anular solicitud")
            _errorMessage.value = "Error: No se pudo obtener el ID de usuario. Por favor, cierra sesión y vuelve a iniciar."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                android.util.Log.d("SolicitudViewModel", "=== ANULANDO SOLICITUD ===")
                android.util.Log.d("SolicitudViewModel", "SolicitudId: $solicitudId")
                android.util.Log.d("SolicitudViewModel", "UsuarioId: $usuarioId")

                // Construir el request de actualización de estado
                val request = SolicitudUpdateEstadoDto(
                    estadoSolicitud = "ANULADA",
                    observacionAdmin = "Anulada por el colaborador desde la app móvil",
                    revisadoPorUsuarioId = usuarioId
                )

                android.util.Log.d("SolicitudViewModel", "Request: $request")

                // Llamar al repositorio para actualizar el estado
                val solicitudActualizada = solicitudesRepository.updateEstadoSolicitud(solicitudId, request)

                android.util.Log.d("SolicitudViewModel", "✅ Solicitud anulada exitosamente. Nuevo estado: ${solicitudActualizada.estadoSolicitud}")

                // Actualizar la lista local
                _todasSolicitudes.value = _todasSolicitudes.value.map { solicitud ->
                    if (solicitud.id == solicitudId) {
                        solicitudActualizada
                    } else {
                        solicitud
                    }
                }

                // Reaplicar filtros para actualizar la vista
                aplicarFiltros()

                android.util.Log.d("SolicitudViewModel", "Lista de solicitudes actualizada")

            } catch (e: Exception) {
                android.util.Log.e("SolicitudViewModel", "❌ Error al anular solicitud", e)
                android.util.Log.e("SolicitudViewModel", "Detalles del error: ${e.stackTraceToString()}")
                _errorMessage.value = "Error al anular solicitud: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}

// NUEVO: Modelo de UI para cambios de skills
data class CambioSkillUiModel(
    val nombre: String,
    val tipo: String,
    val nivelActual: Int? = null,
    val nivelPropuesto: Int,
    val esCriticoActual: Boolean? = null,
    val esCriticoPropuesto: Boolean,
    val motivo: String = "",
    val esNueva: Boolean = false // true si es nueva, false si es actualización
)
