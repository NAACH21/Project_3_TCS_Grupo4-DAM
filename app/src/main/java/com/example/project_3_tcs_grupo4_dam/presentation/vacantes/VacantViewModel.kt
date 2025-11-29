package com.example.project_3_tcs_grupo4_dam.presentation.vacantes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_3_tcs_grupo4_dam.data.model.VacanteResponse
import com.example.project_3_tcs_grupo4_dam.data.model.Vacante
import com.example.project_3_tcs_grupo4_dam.data.model.SkillReqResponse
import com.example.project_3_tcs_grupo4_dam.data.model.SkillRequerido
import com.example.project_3_tcs_grupo4_dam.data.remote.RetrofitClient
import com.example.project_3_tcs_grupo4_dam.data.repository.VacanteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class VacantViewModel : ViewModel() {

    private val repository = VacanteRepository(RetrofitClient.vacanteApi)

    // Exponer Vacante (modelo usado por la UI)
    private val _vacantes = MutableStateFlow<List<Vacante>>(emptyList())
    val vacantes: StateFlow<List<Vacante>> = _vacantes

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        fetchVacantes()
    }

    fun fetchVacantes() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // repository.getVacantes() devuelve List<VacanteResponse>
                val responses = repository.getVacantes()
                val mapped = responses.map { r -> vacanteResponseToVacante(r) }
                _vacantes.value = mapped
            } catch (e: Exception) {
                _errorMessage.value = "Error al obtener las vacantes: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Mapper: VacanteResponse -> Vacante (suficiente para UI)
    private fun vacanteResponseToVacante(r: VacanteResponse): Vacante {
        val skills = r.skillsRequeridos.map { sr ->
            SkillRequerido(
                nombre = sr.nombre,
                tipo = sr.tipo,
                nivelDeseado = sr.nivelDeseado,
                esCritico = sr.esCritico
            )
        }
        return Vacante(
            id = r.id ?: r._id?.value() ?: "",
            nombrePerfil = r.nombrePerfil,
            area = r.area,
            rolLaboral = r.rolLaboral ?: "",
            skillsRequeridos = skills,
            certificacionesRequeridas = r.certificacionesRequeridas,
            fechaInicio = r.fechaInicio ?: "",
            urgencia = r.urgencia ?: "",
            estadoVacante = r.estadoVacante ?: "",
            creadaPorUsuarioId = r.creadaPorUsuarioId ?: "",
            fechaCreacion = r.fechaCreacion ?: "",
            fechaActualizacion = r.fechaActualizacion ?: "",
            usuarioActualizacion = r.usuarioActualizacion ?: ""
        )
    }
}