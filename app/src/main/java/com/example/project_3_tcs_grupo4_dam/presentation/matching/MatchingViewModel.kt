package com.example.project_3_tcs_grupo4_dam.presentation.matching

import android.app.Application
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

class MatchingViewModel(application: Application) : AndroidViewModel(application) {

    private val vacanteRepo = VacanteRepository(RetrofitClient.vacanteApi)
    private val colaboradorRepo = ColaboradorRepositoryImpl()
    private val procesosRepo = ProcesosMatchingRepository()

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
            // La API ya devuelve VacanteResponse, no es necesario mapear
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

                // Mapear ColaboradorReadDto -> ColaboradorResponse esperado por MatchingEngine
                val colaboradoresResp: List<ColaboradorResponse> = _colaboradores.value.map { colaboradorDtoToResponse(it) }

                val resultados = MatchingEngine.runMatching(
                    colaboradores = colaboradoresResp,
                    vacante = vacante,
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

    private fun colaboradorDtoToResponse(dto: ColaboradorReadDto): ColaboradorResponse {
        val skills = dto.skills.map { s ->
            SkillResponse(
                nombre = s.nombre,
                tipo = s.tipo,
                nivel = s.nivel,
                esCritico = s.esCritico
            )
        }
        val certs = dto.certificaciones.map { c ->
            CertificacionResponse(
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
        return ColaboradorResponse(
            _id = null,
            id = dto.id,
            nombres = dto.nombres,
            apellidos = dto.apellidos,
            correo = dto.correo,
            area = dto.area,
            rolLaboral = dto.rolLaboral,
            estado = dto.estado,
            disponibleParaMovilidad = dto.disponibleParaMovilidad,
            skills = skills,
            certificaciones = certs,
            fechaRegistro = null,
            fechaActualizacion = null
        )
    }
}
