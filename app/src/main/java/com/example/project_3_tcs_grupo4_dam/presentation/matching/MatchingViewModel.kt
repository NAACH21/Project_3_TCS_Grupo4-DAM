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
import java.io.File
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

    private val _pdfGenerado = MutableStateFlow<File?>(null)
    val pdfGenerado: StateFlow<File?> = _pdfGenerado

    // =====================================================
    // GENERAR REPORTE MATCHING
    // =====================================================
    fun generarReporte(vacante: VacanteResponse, resultados: List<ResultadoMatchingItem>, umbral: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val context = getApplication<Application>()
                val file = PdfReportHelper.generarReporteMatching(
                    context = context,
                    vacante = vacante,
                    lista = resultados
                )
                _pdfGenerado.value = file

            } catch (e: Exception) {
                _message.value = "Error generando PDF: ${e.message}"
            }
        }
    }

    // =====================================================
    // GENERAR BRECHA SKILLS
    // =====================================================
    fun generarBrecha(vacante: VacanteResponse, umbral: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val context = getApplication<Application>()
                val file = PdfReportHelper.generarReporteBrechaSkills(context, vacante, umbral)
                _pdfGenerado.value = file

            } catch (e: Exception) {
                _message.value = "Error generando brecha: ${e.message}"
            }
        }
    }

    // =====================================================
    // CARGAR VACANTES
    // =====================================================
    fun loadVacantes() = viewModelScope.launch {
        try {
            _loading.value = true
            val all = vacanteRepo.getVacantes()

            _vacantes.value = all.filter { it.estadoVacante?.equals("Activa", true) == true }
        } catch (e: Exception) {
            _message.value = "Error cargando vacantes: ${e.message}"
        } finally {
            _loading.value = false
        }
    }

    // =====================================================
    // CARGAR COLABORADORES
    // =====================================================
    fun loadColaboradores() = viewModelScope.launch {
        try {
            _loading.value = true
            _colaboradores.value = colaboradorRepo.getAllColaboradores()
        } catch (e: Exception) {
            _message.value = "Error cargando colaboradores: ${e.message}"
        } finally {
            _loading.value = false
        }
    }

    // =====================================================
    // EJECUTAR MATCHING
    // =====================================================
    fun ejecutarMatching(
        vacante: VacanteResponse,
        umbral: Int,
        guardarProceso: Boolean = true
    ) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _message.value = null

                // Convertir DTO a ColaboradorResponse para MatchingEngine
                val colaboradoresResp = _colaboradores.value.map { colaboradorDtoToResponse(it) }

                val resultadosCalc: List<ResultadoMatchingItem> =
                    MatchingEngine.runMatching(
                        colaboradores = colaboradoresResp,
                        vacante = vacante,
                        umbralPercent = umbral
                    )

                _resultados.value = resultadosCalc

                if (resultadosCalc.isEmpty()) {
                    _message.value = "No se encontraron candidatos >= $umbral%"
                    return@launch
                }

                if (guardarProceso) {
                    val req = ProcesoMatchingRequest(
                        vacanteId = vacante.getIdValue(),
                        umbral = umbral,
                        fechaEjecucion = Date(),
                        resultados = resultadosCalc
                    )
                    try {
                        procesosRepo.createProceso(req)
                    } catch (e: Exception) {
                        _message.value = "Matching ejecutado, pero error guardando proceso: ${e.message}"
                    }
                }

                _message.value = "Matching ejecutado: ${resultadosCalc.size} candidatos encontrados"

            } catch (e: Exception) {
                e.printStackTrace()
                _message.value = "Error ejecutando matching: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    // =====================================================
    // MAPEO DTO -> RESPONSE
    // =====================================================
    private fun colaboradorDtoToResponse(dto: ColaboradorReadDto): ColaboradorResponse {

        val skills = dto.skills.map {
            SkillResponse(
                nombre = it.nombre,
                tipo = it.tipo,
                nivel = it.nivel,
                esCritico = it.esCritico
            )
        }

        val certs = dto.certificaciones.map {
            CertificacionResponse(
                certificacionId = it.certificacionId,
                nombre = it.nombre,
                institucion = it.institucion,
                fechaObtencion = null,
                fechaVencimiento = null,
                archivoPdfUrl = it.archivoPdfUrl,
                estado = it.estado,
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
