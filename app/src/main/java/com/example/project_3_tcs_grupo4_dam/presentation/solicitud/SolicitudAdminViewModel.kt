package com.example.project_3_tcs_grupo4_dam.presentation.solicitud

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_3_tcs_grupo4_dam.data.local.SessionManager
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorDtos
import com.example.project_3_tcs_grupo4_dam.data.model.SolicitudReadDto
import com.example.project_3_tcs_grupo4_dam.data.model.SolicitudUpdateEstadoDto
import com.example.project_3_tcs_grupo4_dam.data.repository.CertificadosRepository
import com.example.project_3_tcs_grupo4_dam.data.repository.ColaboradorRepository
import com.example.project_3_tcs_grupo4_dam.data.repository.SolicitudesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel exclusivo para Solicitudes de Skills (ACTUALIZACION_SKILLS) del ADMINISTRADOR
 * Maneja actualización de skills existentes y creación de skills nuevas
 * Incluye gestión de certificaciones asociadas
 */
class SolicitudAdminViewModel(
    private val solicitudesRepository: SolicitudesRepository,
    private val colaboradorRepository: ColaboradorRepository,
    private val certificadosRepository: CertificadosRepository,
    sessionManager: SessionManager
) : ViewModel() {

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

    // Filtros
    private val _filtroEstado = MutableStateFlow("TODOS")
    val filtroEstado: StateFlow<String> = _filtroEstado.asStateFlow()

    private val _busquedaColaborador = MutableStateFlow("")
    val busquedaColaborador: StateFlow<String> = _busquedaColaborador.asStateFlow()

    // Solicitud seleccionada para detalle/edición
    private val _solicitudSeleccionada = MutableStateFlow<SolicitudReadDto?>(null)
    val solicitudSeleccionada: StateFlow<SolicitudReadDto?> = _solicitudSeleccionada.asStateFlow()

    private val _showDetalleDialog = MutableStateFlow(false)
    val showDetalleDialog: StateFlow<Boolean> = _showDetalleDialog.asStateFlow()

    // Diálogo de cambio de estado
    private val _showCambioEstadoDialog = MutableStateFlow(false)
    val showCambioEstadoDialog: StateFlow<Boolean> = _showCambioEstadoDialog.asStateFlow()

    init {
        Log.d(TAG, "=== INICIALIZACIÓN ===")
        Log.d(TAG, "Rol usuario: $rolUsuario")
        Log.d(TAG, "UsuarioId: $usuarioId")

        if (!esRolAdministrador()) {
            Log.e(TAG, "Error: Usuario no es administrador. Rol: $rolUsuario")
            _errorMessage.value = "Esta pantalla es solo para administradores"
        } else if (usuarioId.isBlank()) {
            Log.e(TAG, "ERROR CRÍTICO: usuarioId está vacío")
            _errorMessage.value = "Error de sesión: No se pudo obtener el ID de usuario"
        } else {
            Log.d(TAG, "✅ Inicialización correcta, cargando datos...")
            cargarSolicitudes()
        }
    }

    /**
     * Verifica si el rol actual es de administrador
     */
    private fun esRolAdministrador(): Boolean {
        return rolUsuario in listOf("ADMIN", "ADMINISTRADOR", "GESTOR_TALENTO", "BUSINESS_MANAGER")
    }

    /**
     * Carga todas las solicitudes de tipo ACTUALIZACION_SKILLS
     */
    fun cargarSolicitudes() {
        if (!esRolAdministrador()) {
            Log.w(TAG, "Intento de carga con rol no válido: $rolUsuario")
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                Log.d(TAG, "=== CARGANDO SOLICITUDES DE SKILLS ===")

                // Obtener todas las solicitudes
                val todasLasSolicitudes = solicitudesRepository.getSolicitudes()

                Log.d(TAG, "Solicitudes totales recibidas: ${todasLasSolicitudes.size}")

                // Filtrar SOLO ACTUALIZACION_SKILLS
                val solicitudesSkills = todasLasSolicitudes.filter {
                    it.tipoSolicitudGeneral == "ACTUALIZACION_SKILLS"
                }

                Log.d(TAG, "Solicitudes de skills filtradas: ${solicitudesSkills.size}")

                // Ordenar por fecha de creación descendente (más reciente primero)
                _todasSolicitudes.value = solicitudesSkills.sortedByDescending { it.fechaCreacion }

                aplicarFiltros()

                Log.d(TAG, "=== CARGA COMPLETADA EXITOSAMENTE ===")
            } catch (e: Exception) {
                Log.e(TAG, "=== ERROR AL CARGAR SOLICITUDES ===", e)
                _errorMessage.value = "Error al cargar solicitudes: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Aplica filtros de estado y búsqueda por colaborador
     */
    private fun aplicarFiltros() {
        val estado = _filtroEstado.value
        val busqueda = _busquedaColaborador.value.trim().lowercase()

        val solicitudesFiltradas = _todasSolicitudes.value.filter { solicitud ->
            val cumpleEstado = estado == "TODOS" || solicitud.estadoSolicitud == estado
            
            // TODO: Filtrar por nombre de colaborador cuando tengamos el colaboradorNombre en el DTO
            val cumpleBusqueda = busqueda.isEmpty() || 
                                 solicitud.colaboradorId.lowercase().contains(busqueda)

            cumpleEstado && cumpleBusqueda
        }

        _solicitudesFiltradas.value = solicitudesFiltradas

        android.util.Log.d("SolicitudAdminVM", "Filtros aplicados - Total: ${_todasSolicitudes.value.size}, Filtradas: ${solicitudesFiltradas.size}")
    }

    fun aplicarFiltroEstado(nuevoEstado: String) {
        android.util.Log.d("SolicitudAdminVM", "Aplicando filtro estado: $nuevoEstado")
        _filtroEstado.value = nuevoEstado
        aplicarFiltros()
    }

    fun aplicarBusquedaColaborador(busqueda: String) {
        Log.d(TAG, "Aplicando búsqueda: $busqueda")
        _busquedaColaborador.value = busqueda
        aplicarFiltros()
    }

    /**
     * Actualiza el estado de una solicitud de skill
     * Si el estado es APROBADA, actualiza el colaborador y registra la certificación
     */
    fun actualizarEstadoSolicitud(
        solicitudId: String,
        nuevoEstado: String,
        observacion: String?
    ) {
        if (!esRolAdministrador()) {
            _errorMessage.value = "Solo los administradores pueden cambiar estados"
            return
        }

        if (usuarioId.isBlank()) {
            Log.e(TAG, "ERROR CRÍTICO: usuarioId está vacío")
            _errorMessage.value = "Error: No se pudo obtener el ID de usuario"
            return
        }

        // Validación: estados válidos
        val estadosValidos = listOf("PENDIENTE", "EN_REVISION", "OBSERVADO", "APROBADA", "RECHAZADA")
        if (nuevoEstado !in estadosValidos) {
            _errorMessage.value = "Estado no válido: $nuevoEstado"
            return
        }

        // Validación: si el estado es RECHAZADA, la observación es obligatoria
        if (nuevoEstado == "RECHAZADA" && observacion.isNullOrBlank()) {
            _errorMessage.value = "La observación es obligatoria para rechazar una solicitud"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                Log.d(TAG, "=== ACTUALIZANDO ESTADO DE SOLICITUD ===")
                Log.d(TAG, "SolicitudId: $solicitudId")
                Log.d(TAG, "Nuevo estado: $nuevoEstado")
                Log.d(TAG, "Observación: $observacion")
                Log.d(TAG, "RevisadoPorUsuarioId: $usuarioId")

                // 1. Actualizar estado en backend
                val request = SolicitudUpdateEstadoDto(
                    estadoSolicitud = nuevoEstado,
                    observacionAdmin = observacion,
                    revisadoPorUsuarioId = usuarioId
                )

                val solicitudActualizada = solicitudesRepository.updateEstadoSolicitud(solicitudId, request)

                Log.d(TAG, "✅ Estado actualizado exitosamente. Nuevo estado: ${solicitudActualizada.estadoSolicitud}")

                // 2. Si el estado es APROBADA y es de tipo ACTUALIZACION_SKILLS, procesar
                if (nuevoEstado == "APROBADA" && solicitudActualizada.tipoSolicitudGeneral == "ACTUALIZACION_SKILLS") {
                    procesarAprobacionSkill(solicitudActualizada)
                }

                // 3. Actualizar la lista local
                _todasSolicitudes.value = _todasSolicitudes.value.map { solicitud ->
                    if (solicitud.id == solicitudId) {
                        solicitudActualizada
                    } else {
                        solicitud
                    }
                }

                aplicarFiltros()
                cerrarCambioEstadoDialog()

                Log.d(TAG, "✅ Proceso completado exitosamente")

            } catch (e: Exception) {
                Log.e(TAG, "❌ Error al actualizar estado", e)
                _errorMessage.value = "Error al actualizar estado: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Procesa la aprobación de una solicitud de skill:
     * 1. Actualiza/Agrega la skill al colaborador
     * 2. Registra la certificación
     *
     * TODO: En el futuro, mover esta lógica a un caso de uso en la capa domain
     */
    private suspend fun procesarAprobacionSkill(solicitud: SolicitudReadDto) {
        try {
            Log.d(TAG, "=== PROCESANDO APROBACIÓN DE SKILL ===")

            // Validar que existe cambio de skill
            val cambio = solicitud.cambiosSkillsPropuestos?.firstOrNull()
            if (cambio == null) {
                Log.e(TAG, "❌ No hay cambio de skill en la solicitud")
                throw Exception("Solicitud sin cambio de skill")
            }

            Log.d(TAG, "Cambio de skill: ${cambio.nombre}, tipo: ${cambio.tipo}")
            Log.d(TAG, "Nivel: ${cambio.nivelActual} → ${cambio.nivelPropuesto}")
            Log.d(TAG, "Crítico: ${cambio.esCriticoActual} → ${cambio.esCriticoPropuesto}")

            val colaboradorId = solicitud.colaboradorId

            // Actualizar o agregar skill
            if (cambio.nivelActual != null) {
                // Skill existente: actualizar
                Log.d(TAG, "Actualizando skill existente...")
                actualizarSkillColaborador(
                    colaboradorId = colaboradorId,
                    nombreSkill = cambio.nombre,
                    tipoSkill = cambio.tipo,
                    nuevoNivel = cambio.nivelPropuesto,
                    esCritico = cambio.esCriticoPropuesto
                )
            } else {
                // Skill nueva: agregar
                Log.d(TAG, "Agregando skill nueva...")
                agregarSkillColaborador(
                    colaboradorId = colaboradorId,
                    nombreSkill = cambio.nombre,
                    tipoSkill = cambio.tipo,
                    nivel = cambio.nivelPropuesto,
                    esCritico = cambio.esCriticoPropuesto
                )
            }

            // Registrar certificación
            val cert = solicitud.certificacionPropuesta
            if (cert != null && !cert.archivoPdfUrl.isNullOrBlank()) {
                Log.d(TAG, "Registrando certificación: ${cert.nombre}")
                registrarCertificacion(
                    colaboradorId = colaboradorId,
                    cert = cert
                )
            } else {
                Log.w(TAG, "⚠️ No hay certificación para registrar")
            }

            Log.d(TAG, "✅ Skill y certificación procesadas exitosamente")

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error al procesar aprobación de skill", e)
            // No lanzar la excepción para no revertir el cambio de estado
            // pero informar al usuario
            _errorMessage.value = "Solicitud aprobada, pero error al actualizar colaborador: ${e.message}"
        }
    }

    /**
     * Actualiza una skill existente del colaborador
     * TODO: Implementar método updateSkill en ColaboradorRepository si no existe
     */
    private suspend fun actualizarSkillColaborador(
        colaboradorId: String,
        nombreSkill: String,
        tipoSkill: String,
        nuevoNivel: Int,
        esCritico: Boolean
    ) {
        try {
            // Obtener colaborador actual
            val colaborador = colaboradorRepository.getColaboradorById(colaboradorId)

            // Actualizar skills
            val skillsActualizadas = colaborador.skills.map { skill ->
                if (skill.nombre.equals(nombreSkill, ignoreCase = true) && skill.tipo.equals(tipoSkill, ignoreCase = true)) {
                    // Crear DTO actualizado
                    ColaboradorDtos.SkillCreateDto(
                        nombre = skill.nombre,
                        tipo = skill.tipo,
                        nivel = nuevoNivel,
                        esCritico = esCritico
                    )
                } else {
                    // Mantener skill sin cambios
                    ColaboradorDtos.SkillCreateDto(
                        nombre = skill.nombre,
                        tipo = skill.tipo,
                        nivel = skill.nivel,
                        esCritico = skill.esCritico
                    )
                }
            }

            // Actualizar colaborador con PUT
            val updateDto = ColaboradorDtos.ColaboradorUpdateDto(
                nombres = colaborador.nombres,
                apellidos = colaborador.apellidos,
                correo = colaborador.correo,
                area = colaborador.area,
                rolLaboral = colaborador.rolLaboral,
                estado = colaborador.estado,
                disponibleParaMovilidad = colaborador.disponibleParaMovilidad,
                skills = skillsActualizadas,
                certificaciones = colaborador.certificaciones.map {
                    ColaboradorDtos.CertificacionCreateDto(
                        nombre = it.nombre,
                        institucion = it.institucion,
                        fechaObtencion = it.fechaObtencion,
                        fechaVencimiento = it.fechaVencimiento,
                        archivoPdfUrl = it.archivoPdfUrl
                    )
                }
            )

            colaboradorRepository.updateColaborador(colaboradorId, updateDto)
            Log.d(TAG, "✅ Skill actualizada en colaborador")

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error al actualizar skill del colaborador", e)
            throw e
        }
    }

    /**
     * Agrega una skill nueva al colaborador
     */
    private suspend fun agregarSkillColaborador(
        colaboradorId: String,
        nombreSkill: String,
        tipoSkill: String,
        nivel: Int,
        esCritico: Boolean
    ) {
        try {
            // Obtener colaborador actual
            val colaborador = colaboradorRepository.getColaboradorById(colaboradorId)

            // Agregar nueva skill
            val skillsActualizadas = colaborador.skills.map {
                ColaboradorDtos.SkillCreateDto(
                    nombre = it.nombre,
                    tipo = it.tipo,
                    nivel = it.nivel,
                    esCritico = it.esCritico
                )
            }.toMutableList().apply {
                add(ColaboradorDtos.SkillCreateDto(
                    nombre = nombreSkill,
                    tipo = tipoSkill,
                    nivel = nivel,
                    esCritico = esCritico
                ))
            }

            // Actualizar colaborador con PUT
            val updateDto = ColaboradorDtos.ColaboradorUpdateDto(
                nombres = colaborador.nombres,
                apellidos = colaborador.apellidos,
                correo = colaborador.correo,
                area = colaborador.area,
                rolLaboral = colaborador.rolLaboral,
                estado = colaborador.estado,
                disponibleParaMovilidad = colaborador.disponibleParaMovilidad,
                skills = skillsActualizadas,
                certificaciones = colaborador.certificaciones.map {
                    ColaboradorDtos.CertificacionCreateDto(
                        nombre = it.nombre,
                        institucion = it.institucion,
                        fechaObtencion = it.fechaObtencion,
                        fechaVencimiento = it.fechaVencimiento,
                        archivoPdfUrl = it.archivoPdfUrl
                    )
                }
            )

            colaboradorRepository.updateColaborador(colaboradorId, updateDto)
            Log.d(TAG, "✅ Skill nueva agregada al colaborador")

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error al agregar skill nueva al colaborador", e)
            throw e
        }
    }

    /**
     * Registra la certificación en el sistema usando el PDF ya subido
     */
    private suspend fun registrarCertificacion(
        colaboradorId: String,
        cert: com.example.project_3_tcs_grupo4_dam.data.model.CertificacionPropuestaReadDto
    ) {
        try {
            // Obtener colaborador actual
            val colaborador = colaboradorRepository.getColaboradorById(colaboradorId)

            // Agregar nueva certificación
            val certificacionesActualizadas = colaborador.certificaciones.map {
                ColaboradorDtos.CertificacionCreateDto(
                    nombre = it.nombre,
                    institucion = it.institucion,
                    fechaObtencion = it.fechaObtencion,
                    fechaVencimiento = it.fechaVencimiento,
                    archivoPdfUrl = it.archivoPdfUrl
                )
            }.toMutableList().apply {
                add(ColaboradorDtos.CertificacionCreateDto(
                    nombre = cert.nombre,
                    institucion = cert.institucion,
                    fechaObtencion = cert.fechaObtencion,
                    fechaVencimiento = cert.fechaVencimiento,
                    archivoPdfUrl = cert.archivoPdfUrl
                ))
            }

            // Actualizar colaborador con PUT
            val updateDto = ColaboradorDtos.ColaboradorUpdateDto(
                nombres = colaborador.nombres,
                apellidos = colaborador.apellidos,
                correo = colaborador.correo,
                area = colaborador.area,
                rolLaboral = colaborador.rolLaboral,
                estado = colaborador.estado,
                disponibleParaMovilidad = colaborador.disponibleParaMovilidad,
                skills = colaborador.skills.map {
                    ColaboradorDtos.SkillCreateDto(
                        nombre = it.nombre,
                        tipo = it.tipo,
                        nivel = it.nivel,
                        esCritico = it.esCritico
                    )
                },
                certificaciones = certificacionesActualizadas
            )

            colaboradorRepository.updateColaborador(colaboradorId, updateDto)
            Log.d(TAG, "✅ Certificación registrada en colaborador")

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error al registrar certificación", e)
            throw e
        }
    }

    fun mostrarDetalleSolicitud(solicitud: SolicitudReadDto) {
        Log.d(TAG, "Mostrando detalle de solicitud: ${solicitud.id}")
        _solicitudSeleccionada.value = solicitud
        _showDetalleDialog.value = true
    }

    fun cerrarDetalleSolicitud() {
        _showDetalleDialog.value = false
        _solicitudSeleccionada.value = null
    }

    fun abrirCambioEstadoDialog(solicitud: SolicitudReadDto) {
        Log.d(TAG, "Abriendo diálogo de cambio de estado para: ${solicitud.id}")
        _solicitudSeleccionada.value = solicitud
        _showCambioEstadoDialog.value = true
    }

    fun cerrarCambioEstadoDialog() {
        _showCambioEstadoDialog.value = false
        _solicitudSeleccionada.value = null
    }

    fun limpiarError() {
        _errorMessage.value = null
    }

    companion object {
        private const val TAG = "SolicitudAdminVM"
    }
}
