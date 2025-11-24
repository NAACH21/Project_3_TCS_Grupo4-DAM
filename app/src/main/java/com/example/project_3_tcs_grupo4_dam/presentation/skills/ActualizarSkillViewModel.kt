package com.example.project_3_tcs_grupo4_dam.presentation.skills

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.project_3_tcs_grupo4_dam.data.remote.ColaboradorApiService
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorDtos.ColaboradorReadDto
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorDtos.SkillReadDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ActualizarSkillViewModel(
    private val apiService: ColaboradorApiService,
    private val colaboradorId: String,
    private val skillName: String
) : ViewModel() {

    private var currentColaborador: ColaboradorReadDto? = null
    private val _skillState = MutableStateFlow<SkillReadDto?>(null)
    val skillState = _skillState.asStateFlow()

    var selectedNivel by mutableStateOf(0)
    var selectedTipoEvidencia by mutableStateOf("URL")
    var urlEvidencia by mutableStateOf("")
    var notasAdicionales by mutableStateOf("")

    var isLoading by mutableStateOf(false)
    var isSuccess by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    init {
        cargarColaborador()
    }

    private fun cargarColaborador() {
        viewModelScope.launch {
            isLoading = true
            try {
                // La API devuelve ColaboradorReadDto directamente con skills embebidos
                val data = apiService.getColaboradorById(colaboradorId)
                currentColaborador = data
                val skill = data.skills.find {
                    it.nombre.equals(skillName, ignoreCase = true)
                }
                if (skill != null) {
                    _skillState.value = skill
                    selectedNivel = skill.nivel
                } else {
                    errorMessage = "Skill no encontrado"
                }
            } catch (e: Exception) {
                errorMessage = "Error de red: ${e.message}"
                Log.e("ActualizarSkillVM", "Error", e)
            } finally {
                isLoading = false
            }
        }
    }

    fun actualizarSkill() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val colab = currentColaborador ?: run {
                    errorMessage = "No hay colaborador cargado"
                    isLoading = false
                    return@launch
                }

                // Buscar el skill actual y crear una copia actualizada
                val skillActualizado = colab.skills.map { skill ->
                    if (skill.nombre.equals(skillName, ignoreCase = true)) {
                        com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorDtos.SkillCreateDto(
                            nombre = skill.nombre,
                            tipo = skill.tipo,
                            nivel = selectedNivel,
                            esCritico = skill.esCritico
                        )
                    } else {
                        com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorDtos.SkillCreateDto(
                            nombre = skill.nombre,
                            tipo = skill.tipo,
                            nivel = skill.nivel,
                            esCritico = skill.esCritico
                        )
                    }
                }

                // Convertir certificaciones de Read a Create
                val certificacionesActualizadas = colab.certificaciones.map { cert ->
                    com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorDtos.CertificacionCreateDto(
                        nombre = cert.nombre,
                        institucion = cert.institucion,
                        fechaObtencion = cert.fechaObtencion,
                        fechaVencimiento = cert.fechaVencimiento,
                        archivoPdfUrl = cert.archivoPdfUrl
                    )
                }

                // Crear DTO de actualización con los skills modificados
                val updateDto = com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorDtos.ColaboradorUpdateDto(
                    nombres = colab.nombres,
                    apellidos = colab.apellidos,
                    correo = colab.correo,
                    area = colab.area,
                    rolLaboral = colab.rolLaboral,
                    estado = colab.estado,
                    disponibleParaMovilidad = colab.disponibleParaMovilidad,
                    skills = skillActualizado,
                    certificaciones = certificacionesActualizadas
                )

                apiService.updateColaborador(colaboradorId, updateDto)
                isSuccess = true
                Log.d("ActualizarSkillVM", "Skill actualizado correctamente")
            } catch (e: Exception) {
                errorMessage = "Error al actualizar: ${e.message}"
                Log.e("ActualizarSkillVM", "Error al actualizar", e)
            } finally {
                isLoading = false
            }
        }
    }

    fun enviarValidacion() = actualizarSkill()
}

class ActualizarSkillViewModelFactory(
    private val apiService: ColaboradorApiService,
    private val colaboradorId: String,
    private val skillName: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ActualizarSkillViewModel(apiService, colaboradorId, skillName) as T
    }
}
