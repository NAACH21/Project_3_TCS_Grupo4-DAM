package com.example.project_3_tcs_grupo4_dam.presentation.matching

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_3_tcs_grupo4_dam.data.model.*
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorDtos.ColaboradorReadDto
import com.example.project_3_tcs_grupo4_dam.data.remote.RetrofitClient
import com.example.project_3_tcs_grupo4_dam.data.repository.*
import com.example.project_3_tcs_grupo4_dam.domain.MatchingEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*
import com.example.project_3_tcs_grupo4_dam.data.local.SessionManager
import com.example.project_3_tcs_grupo4_dam.data.repository.AuthRepositoryImpl
import com.example.project_3_tcs_grupo4_dam.data.repository.AuthRepository
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList

class MatchingViewModel(application: Application) : AndroidViewModel(application), MatchingUiContract {

    private val vacanteRepo = VacanteRepository(RetrofitClient.vacanteApi)
    private val colaboradorRepo = ColaboradorRepositoryImpl()
    private val procesosRepo = ProcesosMatchingRepository()
    private val catalogoRepo = CatalogoRepositoryImpl()
    private val alertasApi = RetrofitClient.alertasApi

    // Session + AuthRepository para obtener usuarioId
    private val sessionManager: SessionManager = SessionManager(application)
    private val authRepository: AuthRepository = AuthRepositoryImpl(RetrofitClient.authApi, sessionManager)

    // Ajustado: usamos el modelo Vacante que retorna VacanteRepository
    private val _vacantes = MutableStateFlow<List<Vacante>>(emptyList())
    val vacantes: StateFlow<List<Vacante>> = _vacantes

    private val _colaboradores = MutableStateFlow<List<ColaboradorReadDto>>(emptyList())
    val colaboradores: StateFlow<List<ColaboradorReadDto>> = _colaboradores

    private val _resultados = MutableStateFlow<List<ResultadoMatchingItem>>(emptyList())
    val resultados: StateFlow<List<ResultadoMatchingItem>> = _resultados

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    // ------------------------------------------------------------------
    // Implementación del contrato UI
    // ------------------------------------------------------------------

    override var selectedVacante by mutableStateOf("")

    override val vacantesMock: List<String>
        get() = _vacantes.value.map { it.nombrePerfil }

    override var umbralMatch by mutableStateOf(60f)

    // Usar UiSkill definido en MatchingContract
    override val listaSkills: SnapshotStateList<UiSkill> = mutableStateListOf()

    override val nivelesSkill: List<String> = listOf("1", "2", "3", "4")

    override var mensajeSistema by mutableStateOf<String?>(null)

    // Skills disponibles desde el catálogo (plana)
    private val _availableSkills = mutableStateListOf<String>()
    override val availableSkills: List<String>
        get() = _availableSkills

    init {
        // Cargar catálogo de skills al inicializar el ViewModel
        viewModelScope.launch {
            try {
                val items = catalogoRepo.getSkillsCatalogo() // SkillCatalogItemDto list
                _availableSkills.clear()
                _availableSkills.addAll(items.map { it.nombre })

                // Si el catálogo está vacío, intentar fallback a skills de colaboradores
                if (_availableSkills.isEmpty()) {
                    val cols = colaboradorRepo.getAllColaboradores()
                    val fallback = cols.flatMap { c -> c.skills.map { s -> s.nombre } }.distinct()
                    _availableSkills.addAll(fallback)
                }
            } catch (e: Exception) {
                // Intentar fallback a skills desde colaboradores
                try {
                    val cols = colaboradorRepo.getAllColaboradores()
                    val fallback = cols.flatMap { c -> c.skills.map { s -> s.nombre } }.distinct()
                    _availableSkills.clear()
                    _availableSkills.addAll(fallback)

                    if (_availableSkills.isEmpty()) {
                        _message.value = "No se encontraron skills en catálogo ni en colaboradores"
                    }
                } catch (e2: Exception) {
                    // No interrumpe la UI; dejamos mensaje para el usuario
                    _message.value = "No se pudo cargar catálogo de skills: ${e.message}"
                }
            }
        }
    }

    override fun agregarSkill() {
        listaSkills.add(UiSkill())
    }

    override fun actualizarSkillNombre(index: Int, nombre: String) {
        if (index in 0 until listaSkills.size) {
            val old = listaSkills[index]
            listaSkills[index] = old.copy(nombre = nombre)
        }
    }

    override fun actualizarSkillNivel(index: Int, nivel: String) {
        if (index in 0 until listaSkills.size) {
            val old = listaSkills[index]
            listaSkills[index] = old.copy(nivel = nivel)
        }
    }

    override fun eliminarSkill(index: Int) {
        if (index in 0 until listaSkills.size) {
            listaSkills.removeAt(index)
        }
    }

    override fun ejecutarMatchingMock(): List<ResultadoMatchingItem> {
        // Mapear vacante seleccionada a objeto Vacante
        val vac = _vacantes.value.firstOrNull { it.nombrePerfil == selectedVacante } ?: _vacantes.value.firstOrNull()
        if (vac == null) return emptyList()

        // Mapear colaboradores a ColaboradorResponse (motor espera ColaboradorResponse)
        val colaboradoresResp = _colaboradores.value.map { c ->
            ColaboradorResponse(
                _id = null,
                id = c.id,
                nombres = c.nombres,
                apellidos = c.apellidos,
                correo = c.correo,
                area = c.area,
                rolLaboral = c.rolLaboral,
                estado = c.estado,
                disponibleParaMovilidad = c.disponibleParaMovilidad,
                skills = c.skills.map { s ->
                    SkillResponse(
                        nombre = s.nombre,
                        tipo = s.tipo,
                        nivel = s.nivel,
                        esCritico = s.esCritico
                    )
                },
                certificaciones = c.certificaciones.map { cr ->
                    CertificacionResponse(
                        certificacionId = cr.certificacionId,
                        nombre = cr.nombre,
                        institucion = cr.institucion,
                        fechaObtencion = null,
                        fechaVencimiento = null,
                        archivoPdfUrl = cr.archivoPdfUrl,
                        estado = cr.estado,
                        fechaRegistro = null,
                        fechaActualizacion = null,
                        proximaEvaluacion = null
                    )
                },
                fechaRegistro = null,
                fechaActualizacion = null
            )
        }

        // Convertir Vacante a VacanteResponse temporal
        val vacResp = VacanteResponse(
            _id = null,
            id = vac.id,
            nombrePerfil = vac.nombrePerfil,
            area = vac.area,
            rolLaboral = vac.rolLaboral,
            skillsRequeridos = vac.skillsRequeridos.map { sr ->
                SkillReqResponse(sr.nombre, sr.tipo, sr.nivelDeseado, sr.esCritico)
            },
            certificacionesRequeridas = vac.certificacionesRequeridas,
            fechaInicio = vac.fechaInicio,
            urgencia = vac.urgencia,
            estadoVacante = vac.estadoVacante,
            creadaPorUsuarioId = vac.creadaPorUsuarioId,
            fechaCreacion = vac.fechaCreacion,
            fechaActualizacion = vac.fechaActualizacion,
            usuarioActualizacion = vac.usuarioActualizacion
        )

        val resultados = MatchingEngine.runMatching(
            colaboradores = colaboradoresResp,
            vacante = vacResp,
            umbralPercent = umbralMatch.toInt()
        )

        // Actualizar estado interno también
        _resultados.value = resultados
        mensajeSistema = "Matching ejecutado: ${resultados.size} candidato(s)"
        return resultados
    }

    // ------------------------------------------------------------------
    // Funciones existentes para integración real (ya implementadas)
    // ------------------------------------------------------------------

    fun loadVacantes() = viewModelScope.launch {
        try {
            _loading.value = true
            val all = vacanteRepo.getVacantes()
            // Mostrar sólo vacantes con estadoVacante == "Activa" (insensible a mayúsculas)
            _vacantes.value = all.filter { it.estadoVacante.equals("Activa", true) }
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

    private fun mapSkillReadToSkillResponse(s: com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorDtos.SkillReadDto): SkillResponse {
        return SkillResponse(
            nombre = s.nombre,
            tipo = s.tipo,
            nivel = s.nivel,
            esCritico = s.esCritico
        )
    }

    private fun mapCertReadToCertResponse(c: com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorDtos.CertificacionReadDto): CertificacionResponse {
        return CertificacionResponse(
            certificacionId = c.certificacionId,
            nombre = c.nombre,
            institucion = c.institucion,
            fechaObtencion = null,
            fechaVencimiento = null,
            archivoPdfUrl = c.archivoPdfUrl,
            estado = c.estado,
            fechaRegistro = null,
            fechaActualizacion = null,
            proximaEvaluacion = null
        )
    }

    private fun mapColaboradorReadDtoToResponse(c: ColaboradorReadDto): ColaboradorResponse {
        return ColaboradorResponse(
            _id = null,
            id = c.id,
            nombres = c.nombres,
            apellidos = c.apellidos,
            correo = c.correo,
            area = c.area,
            rolLaboral = c.rolLaboral,
            estado = c.estado,
            disponibleParaMovilidad = c.disponibleParaMovilidad,
            skills = c.skills.map { s ->
                SkillResponse(
                    nombre = s.nombre,
                    tipo = s.tipo,
                    nivel = s.nivel,
                    esCritico = s.esCritico
                )
            },
            certificaciones = c.certificaciones.map { mapCertReadToCertResponse(it) },
            fechaRegistro = null,
            fechaActualizacion = null
        )
    }

    private fun mapSkillReqToSkillReqResponse(s: SkillRequerido): SkillReqResponse {
        return SkillReqResponse(
            nombre = s.nombre,
            tipo = s.tipo,
            nivelDeseado = s.nivelDeseado,
            esCritico = s.esCritico
        )
    }

    private fun mapVacanteToVacanteResponse(v: Vacante): VacanteResponse {
        return VacanteResponse(
            _id = null,
            id = v.id,
            nombrePerfil = v.nombrePerfil,
            area = v.area,
            rolLaboral = v.rolLaboral,
            skillsRequeridos = v.skillsRequeridos.map { mapSkillReqToSkillReqResponse(it) },
            certificacionesRequeridas = v.certificacionesRequeridas,
            fechaInicio = v.fechaInicio,
            urgencia = v.urgencia,
            estadoVacante = v.estadoVacante,
            creadaPorUsuarioId = v.creadaPorUsuarioId,
            fechaCreacion = v.fechaCreacion,
            fechaActualizacion = v.fechaActualizacion,
            usuarioActualizacion = v.usuarioActualizacion
        )
    }

    fun ejecutarMatching(
        vacante: Vacante,
        umbral: Int,
        guardarProceso: Boolean = true
    ) {
        viewModelScope.launch {
            try {
                _loading.value = true

                val vacResp = mapVacanteToVacanteResponse(vacante)
                val colaboradoresResp = _colaboradores.value.map { mapColaboradorReadDtoToResponse(it) }

                val resultados = MatchingEngine.runMatching(
                    colaboradores = colaboradoresResp,
                    vacante = vacResp,
                    umbralPercent = umbral
                )

                _resultados.value = resultados

                if (resultados.isEmpty()) {
                    _message.value = "No se encontraron candidatos con puntaje >= $umbral%"
                    // No guardar proceso si no hay resultados
                    return@launch
                }

                if (guardarProceso) {
                    val req = ProcesoMatchingRequest(
                        vacanteId = vacante.id,
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

    // ==================================================
    // CREAR ALERTA (STUB: el servicio actual solo expone GET)
    // ==================================================
    fun crearAlerta(
        tipo: String,
        descripcion: String,
        vacanteId: String?
    ) {
        // Como el API de alertas en este repo solo tiene GET, no hay POST disponible.
        // Implementamos un stub que registra el intento y notifica al UI.
        try {
            _message.value = "Crear alerta no implementado en backend: tipo=$tipo"
        } catch (e: Exception) {
            _message.value = "Error al crear alerta: ${e.message}"
        }
    }
}