package com.example.project_3_tcs_grupo4_dam.presentation.matching

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_3_tcs_grupo4_dam.data.model.*
import com.example.project_3_tcs_grupo4_dam.data.remote.RetrofitClient
import com.example.project_3_tcs_grupo4_dam.data.repository.*
import com.example.project_3_tcs_grupo4_dam.domain.MatchingEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*

class MatchingViewModel : ViewModel() {

    private val vacanteRepo = VacanteRepository(RetrofitClient.vacanteApi)
    private val colaboradorApi = RetrofitClient.colaboradorApi
    private val procesosRepo = ProcesosMatchingRepository()

    private val _vacantes = MutableStateFlow<List<VacanteResponse>>(emptyList())
    val vacantes: StateFlow<List<VacanteResponse>> = _vacantes

    private val _colaboradores = MutableStateFlow<List<ColaboradorResponse>>(emptyList())
    val colaboradores: StateFlow<List<ColaboradorResponse>> = _colaboradores

    private val _resultados = MutableStateFlow<List<ResultadoMatchingItem>>(emptyList())
    val resultados: StateFlow<List<ResultadoMatchingItem>> = _resultados

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    fun loadVacantes() = viewModelScope.launch {
        try {
            _loading.value = true
            val all = vacanteRepo.getVacantes()
            _vacantes.value = all.filter { it.estadoVacante.equals("ABIERTA", true) }
        } finally {
            _loading.value = false
        }
    }

    fun loadColaboradores() = viewModelScope.launch {
        try {
            _loading.value = true
            val cols = colaboradorApi.getColaboradores()
            _colaboradores.value = cols
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

                if (guardarProceso) {
                    val req = ProcesoMatchingRequest(
                        vacanteId = vacante.id,
                                umbral = umbral,
                        fechaEjecucion = Date(),
                        resultados = resultados
                    )
                    procesosRepo.createProceso(req)
                }

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _loading.value = false
            }
        }
    }
}
