package com.example.project_3_tcs_grupo4_dam.presentation.solicitud

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_3_tcs_grupo4_dam.data.local.SessionManager
import com.example.project_3_tcs_grupo4_dam.data.model.DatosEntrevistaPropuestaCreateDto
import com.example.project_3_tcs_grupo4_dam.data.model.SolicitudCreateDto
import com.example.project_3_tcs_grupo4_dam.data.model.SolicitudReadDto
import com.example.project_3_tcs_grupo4_dam.data.model.SolicitudUpdateEstadoDto
import com.example.project_3_tcs_grupo4_dam.data.repository.SolicitudesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel exclusivo para el flujo de Solicitudes de Entrevista del ADMINISTRADOR
 * Maneja SOLO solicitudes de tipo ENTREVISTA_DESEMPENO
 */
class SolicitudAdminViewModel(
    private val solicitudesRepository: SolicitudesRepository,
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

    // Diálogo de nueva entrevista
    private val _isDialogNuevaEntrevistaOpen = MutableStateFlow(false)
    val isDialogNuevaEntrevistaOpen: StateFlow<Boolean> = _isDialogNuevaEntrevistaOpen.asStateFlow()

    // Solicitud seleccionada para detalle/edición
    private val _solicitudSeleccionada = MutableStateFlow<SolicitudReadDto?>(null)
    val solicitudSeleccionada: StateFlow<SolicitudReadDto?> = _solicitudSeleccionada.asStateFlow()

    private val _showDetalleDialog = MutableStateFlow(false)
    val showDetalleDialog: StateFlow<Boolean> = _showDetalleDialog.asStateFlow()

    // Diálogo de cambio de estado
    private val _showCambioEstadoDialog = MutableStateFlow(false)
    val showCambioEstadoDialog: StateFlow<Boolean> = _showCambioEstadoDialog.asStateFlow()

    init {
        android.util.Log.d("SolicitudAdminVM", "=== INICIALIZACIÓN ===")
        android.util.Log.d("SolicitudAdminVM", "Rol usuario: $rolUsuario")
        android.util.Log.d("SolicitudAdminVM", "UsuarioId: $usuarioId")

        if (!esRolAdministrador()) {
            android.util.Log.e("SolicitudAdminVM", "Error: Usuario no es administrador. Rol: $rolUsuario")
            _errorMessage.value = "Esta pantalla es solo para administradores"
        } else if (usuarioId.isBlank()) {
            android.util.Log.e("SolicitudAdminVM", "ERROR CRÍTICO: usuarioId está vacío")
            _errorMessage.value = "Error de sesión: No se pudo obtener el ID de usuario"
        } else {
            android.util.Log.d("SolicitudAdminVM", "✅ Inicialización correcta, cargando datos...")
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
     * Carga todas las solicitudes de tipo ENTREVISTA_DESEMPENO
     */
    fun cargarSolicitudes() {
        if (!esRolAdministrador()) {
            android.util.Log.w("SolicitudAdminVM", "Intento de carga con rol no válido: $rolUsuario")
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                android.util.Log.d("SolicitudAdminVM", "=== CARGANDO SOLICITUDES DE ENTREVISTA ===")

                // Obtener todas las solicitudes
                val todasLasSolicitudes = solicitudesRepository.getSolicitudes()

                android.util.Log.d("SolicitudAdminVM", "Solicitudes totales recibidas: ${todasLasSolicitudes.size}")

                // Filtrar SOLO entrevistas de desempeño
                val solicitudesEntrevista = todasLasSolicitudes.filter {
                    it.tipoSolicitudGeneral == "ENTREVISTA_DESEMPENO"
                }

                android.util.Log.d("SolicitudAdminVM", "Solicitudes de entrevista filtradas: ${solicitudesEntrevista.size}")

                // Ordenar por fecha de creación descendente (más reciente primero)
                _todasSolicitudes.value = solicitudesEntrevista.sortedByDescending { it.fechaCreacion }

                aplicarFiltros()

                android.util.Log.d("SolicitudAdminVM", "=== CARGA COMPLETADA EXITOSAMENTE ===")
            } catch (e: Exception) {
                android.util.Log.e("SolicitudAdminVM", "=== ERROR AL CARGAR SOLICITUDES ===", e)
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
        android.util.Log.d("SolicitudAdminVM", "Aplicando búsqueda: $busqueda")
        _busquedaColaborador.value = busqueda
        aplicarFiltros()
    }

    /**
     * Crea una nueva solicitud de entrevista de desempeño
     */
    fun crearSolicitudEntrevista(
        colaboradorId: String,
        motivo: String,
        periodo: String,
        fechaSugerida: String?
    ) {
        if (!esRolAdministrador()) {
            _errorMessage.value = "Solo los administradores pueden crear entrevistas"
            return
        }

        if (usuarioId.isBlank()) {
            android.util.Log.e("SolicitudAdminVM", "ERROR CRÍTICO: usuarioId está vacío")
            _errorMessage.value = "Error: No se pudo obtener el ID de usuario"
            return
        }

        if (colaboradorId.isBlank()) {
            _errorMessage.value = "Debe seleccionar un colaborador"
            return
        }

        if (motivo.isBlank() || periodo.isBlank()) {
            _errorMessage.value = "Motivo y periodo son obligatorios"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                android.util.Log.d("SolicitudAdminVM", "=== CREANDO SOLICITUD DE ENTREVISTA ===")
                android.util.Log.d("SolicitudAdminVM", "ColaboradorId: $colaboradorId")
                android.util.Log.d("SolicitudAdminVM", "Motivo: $motivo")
                android.util.Log.d("SolicitudAdminVM", "Periodo: $periodo")
                android.util.Log.d("SolicitudAdminVM", "Fecha sugerida: $fechaSugerida")
                android.util.Log.d("SolicitudAdminVM", "CreadoPorUsuarioId: $usuarioId")

                val datosEntrevista = DatosEntrevistaPropuestaCreateDto(
                    motivo = motivo,
                    periodo = periodo,
                    fechaSugerida = fechaSugerida,
                    propuestoPorUsuarioId = usuarioId
                )

                val solicitud = SolicitudCreateDto(
                    tipoSolicitudGeneral = "ENTREVISTA_DESEMPENO",
                    tipoSolicitud = "PERIODICA",
                    colaboradorId = colaboradorId,
                    certificacionIdAnterior = null,
                    certificacionPropuesta = null,
                    datosEntrevistaPropuesta = datosEntrevista,
                    cambiosSkillsPropuestos = null,
                    creadoPorUsuarioId = usuarioId
                )

                android.util.Log.d("SolicitudAdminVM", "DTO a enviar: $solicitud")

                val solicitudCreada = solicitudesRepository.createSolicitud(solicitud)

                android.util.Log.d("SolicitudAdminVM", "✅ Solicitud de entrevista creada exitosamente. ID: ${solicitudCreada.id}")

                cerrarNuevaEntrevista()
                cargarSolicitudes() // Recargar lista
            } catch (e: Exception) {
                android.util.Log.e("SolicitudAdminVM", "❌ Error al crear solicitud de entrevista", e)
                _errorMessage.value = "Error al crear solicitud: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Actualiza el estado de una solicitud
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
            android.util.Log.e("SolicitudAdminVM", "ERROR CRÍTICO: usuarioId está vacío")
            _errorMessage.value = "Error: No se pudo obtener el ID de usuario"
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
                android.util.Log.d("SolicitudAdminVM", "=== ACTUALIZANDO ESTADO DE SOLICITUD ===")
                android.util.Log.d("SolicitudAdminVM", "SolicitudId: $solicitudId")
                android.util.Log.d("SolicitudAdminVM", "Nuevo estado: $nuevoEstado")
                android.util.Log.d("SolicitudAdminVM", "Observación: $observacion")
                android.util.Log.d("SolicitudAdminVM", "RevisadoPorUsuarioId: $usuarioId")

                val request = SolicitudUpdateEstadoDto(
                    estadoSolicitud = nuevoEstado,
                    observacionAdmin = observacion,
                    revisadoPorUsuarioId = usuarioId
                )

                val solicitudActualizada = solicitudesRepository.updateEstadoSolicitud(solicitudId, request)

                android.util.Log.d("SolicitudAdminVM", "✅ Estado actualizado exitosamente. Nuevo estado: ${solicitudActualizada.estadoSolicitud}")

                // Actualizar la lista local
                _todasSolicitudes.value = _todasSolicitudes.value.map { solicitud ->
                    if (solicitud.id == solicitudId) {
                        solicitudActualizada
                    } else {
                        solicitud
                    }
                }

                aplicarFiltros()
                cerrarCambioEstadoDialog()

                android.util.Log.d("SolicitudAdminVM", "Lista actualizada")

            } catch (e: Exception) {
                android.util.Log.e("SolicitudAdminVM", "❌ Error al actualizar estado", e)
                _errorMessage.value = "Error al actualizar estado: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun abrirNuevaEntrevista() {
        _isDialogNuevaEntrevistaOpen.value = true
    }

    fun cerrarNuevaEntrevista() {
        _isDialogNuevaEntrevistaOpen.value = false
    }

    fun mostrarDetalleSolicitud(solicitud: SolicitudReadDto) {
        android.util.Log.d("SolicitudAdminVM", "Mostrando detalle de solicitud: ${solicitud.id}")
        _solicitudSeleccionada.value = solicitud
        _showDetalleDialog.value = true
    }

    fun cerrarDetalleSolicitud() {
        _showDetalleDialog.value = false
        _solicitudSeleccionada.value = null
    }

    fun abrirCambioEstadoDialog(solicitud: SolicitudReadDto) {
        android.util.Log.d("SolicitudAdminVM", "Abriendo diálogo de cambio de estado para: ${solicitud.id}")
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
}
