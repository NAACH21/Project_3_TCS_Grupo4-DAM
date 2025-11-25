package com.example.project_3_tcs_grupo4_dam.presentation.skills

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.project_3_tcs_grupo4_dam.data.local.SessionManager
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorDtos.ColaboradorUpdateDto
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorDtos.SkillCreateDto
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorDtos.SkillReadDto
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorDtos.CertificacionCreateDto
import com.example.project_3_tcs_grupo4_dam.data.remote.ColaboradorApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ColaboradorSkillsUiState(
    val isLoading: Boolean = false,
    val skills: List<SkillReadDto> = emptyList(),
    val errorMessage: String? = null
)

class ColaboradorSkillsViewModel(
    private val apiService: ColaboradorApiService,
    private val sessionManager: SessionManager,
    private val colaboradorId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(ColaboradorSkillsUiState())
    val uiState: StateFlow<ColaboradorSkillsUiState> = _uiState.asStateFlow()

    init {
        fetchSkills()
    }

    private fun fetchSkills() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val colaborador = apiService.getColaboradorById(colaboradorId)
                _uiState.value = _uiState.value.copy(
                    skills = colaborador.skills,
                    isLoading = false
                )
            } catch (e: Exception) {
                Log.e("ColaboradorSkillsVM", "Error al obtener skills: ${e.message}", e)
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Error al cargar skills: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    // --- AGREGAR SKILL DIRECTAMENTE (PUT /api/colaboradores/{id}) ---
    // Cumpliendo regla: Agregar skill nuevo actualiza directamente el colaborador
    fun crearSkill(nombre: String, tipo: String, nivel: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                // 1. Obtener el colaborador actual completo para no perder datos
                val colaboradorActual = apiService.getColaboradorById(colaboradorId)

                // 2. Verificar si ya tiene el skill
                val existe = colaboradorActual.skills.any { it.nombre.equals(nombre, ignoreCase = true) }
                if (existe) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "El skill '$nombre' ya existe en tu perfil."
                    )
                    return@launch
                }

                // 3. Preparar la lista de skills actualizada
                // Mapeamos los skills existentes de ReadDto a CreateDto
                val skillsActualizados = colaboradorActual.skills.map { s ->
                    SkillCreateDto(
                        nombre = s.nombre,
                        tipo = s.tipo,
                        nivel = s.nivel,
                        esCritico = s.esCritico
                    )
                }.toMutableList()

                // Agregamos el nuevo skill
                skillsActualizados.add(
                    SkillCreateDto(
                        nombre = nombre,
                        tipo = tipo,
                        nivel = nivel,
                        esCritico = false // Por defecto false al crear
                    )
                )

                // 4. Preparar lista de certificaciones (requerido por el UpdateDto)
                // Debemos mapear las certificaciones existentes para no perderlas
                val certificacionesActualizadas = colaboradorActual.certificaciones.map { c ->
                    CertificacionCreateDto(
                        nombre = c.nombre,
                        institucion = c.institucion,
                        fechaObtencion = c.fechaObtencion,
                        fechaVencimiento = c.fechaVencimiento,
                        archivoPdfUrl = c.archivoPdfUrl
                    )
                }

                // 5. Construir el DTO de actualización completo
                val updateDto = ColaboradorUpdateDto(
                    nombres = colaboradorActual.nombres,
                    apellidos = colaboradorActual.apellidos,
                    correo = colaboradorActual.correo,
                    area = colaboradorActual.area,
                    rolLaboral = colaboradorActual.rolLaboral,
                    disponibleParaMovilidad = colaboradorActual.disponibleParaMovilidad,
                    estado = colaboradorActual.estado,
                    skills = skillsActualizados,
                    certificaciones = certificacionesActualizadas
                )

                // 6. Llamar al PUT para guardar los cambios
                val colaboradorActualizado = apiService.updateColaborador(colaboradorId, updateDto)

                // 7. Actualizar UI con los nuevos datos devueltos por el backend
                _uiState.value = _uiState.value.copy(
                    skills = colaboradorActualizado.skills,
                    isLoading = false
                )

                Log.d("ColaboradorSkillsVM", "Skill agregado exitosamente: $nombre")

            } catch (e: Exception) {
                Log.e("ColaboradorSkillsVM", "Error al agregar skill", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error al guardar skill: ${e.message}"
                )
            }
        }
    }

    // Funciones de filtro se mantienen igual
    fun filterSkills(searchQuery: String, selectedType: String, selectedStatus: String): List<SkillReadDto> {
        var result = _uiState.value.skills
        if (searchQuery.isNotBlank()) {
            result = result.filter { it.nombre.contains(searchQuery, ignoreCase = true) }
        }
        if (selectedType != "Todos") {
            result = result.filter { it.tipo == selectedType }
        }
        return result
    }
}

class ColaboradorSkillsViewModelFactory(
    private val apiService: ColaboradorApiService,
    private val sessionManager: SessionManager,
    private val colaboradorId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ColaboradorSkillsViewModel(apiService, sessionManager, colaboradorId) as T
    }
}
