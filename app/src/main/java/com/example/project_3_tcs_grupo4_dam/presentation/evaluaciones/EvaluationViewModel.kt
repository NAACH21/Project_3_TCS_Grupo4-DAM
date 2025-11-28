package com.example.project_3_tcs_grupo4_dam.presentation.evaluaciones

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorDtos

import com.example.project_3_tcs_grupo4_dam.data.model.EvaluacionCreateDto
import com.example.project_3_tcs_grupo4_dam.data.model.SkillEvaluadoCreateDto
import com.example.project_3_tcs_grupo4_dam.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import java.util.UUID

// Data class to represent the state of a single skill
data class Skill(
    val id: UUID = UUID.randomUUID(),
    val name: String = "",
    val type: String = "TECNICO", // "TECNICO" or "BLANDO"
    val currentLevel: String = "Básico",
    val recommendedLevel: String = "Básico",
    val comments: String = ""
)

// Data class to represent the entire UI state
data class EvaluationUiState(
    val selectedCollaboratorId: String? = null,
    val currentRole: String = "", // This will be derived from the selected collaborator
    val evaluatorLeader: String = "",
    val evaluationDate: String = "",
    val evaluationType: String = "",
    val skills: List<Skill> = listOf(Skill()),
    val comments: String = "",

    val collaboratorOptions: List<ColaboradorDtos.ColaboradorReadDto> = emptyList(), // Store full collaborator objects
    val evaluationTypeOptions: List<String> = listOf("ENTREVISTA", "TRIMESTRAL", "SEMESTRAL", "ANUAL"),
    val skillTypeOptions: List<String> = listOf("TECNICO", "BLANDO"),
    val skillLevelOptions: List<String> = listOf("Básico", "Intermedio", "Avanzado"),

    // Feedback states
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val saveError: String? = null
)

class EvaluationViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(EvaluationUiState())
    val uiState: StateFlow<EvaluationUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val collaborators = RetrofitClient.colaboradorApiService.getAllColaboradores()
                _uiState.update { it.copy(collaboratorOptions = collaborators, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(saveError = "Error al cargar colaboradores", isLoading = false) }
            }
        }
    }

    fun onCollaboratorChange(collaboratorId: String) {
        val selectedCollaborator = _uiState.value.collaboratorOptions.find { it.id == collaboratorId }
        _uiState.update { it.copy(
            selectedCollaboratorId = collaboratorId,
            currentRole = selectedCollaborator?.rolLaboral ?: "" // Corregido: usar rolLaboral
        ) }
    }

    fun onEvaluatorLeaderChange(newValue: String) {
        _uiState.update { it.copy(evaluatorLeader = newValue) }
    }

    fun onDateChange(newValue: String) {
        _uiState.update { it.copy(evaluationDate = newValue) }
    }

    fun onEvaluationTypeChange(newValue: String) {
        _uiState.update { it.copy(evaluationType = newValue) }
    }

    fun onCommentsChange(newValue: String) {
        _uiState.update { it.copy(comments = newValue) }
    }

    fun addSkill() {
        val newSkills = _uiState.value.skills.toMutableList().apply { add(Skill()) }
        _uiState.update { it.copy(skills = newSkills) }
    }

    fun removeSkill(skillId: UUID) {
        val updatedSkills = _uiState.value.skills.filterNot { it.id == skillId }
        _uiState.update { it.copy(skills = updatedSkills) }
    }

    fun onSkillChange(skillId: UUID, update: (Skill) -> Skill) {
        val updatedSkills = _uiState.value.skills.map {
            if (it.id == skillId) update(it) else it
        }
        _uiState.update { it.copy(skills = updatedSkills) }
    }

    fun saveEvaluation() {
        val uiStateValue = _uiState.value
        if (uiStateValue.selectedCollaboratorId == null) {
            _uiState.update { it.copy(saveError = "Por favor, seleccione un colaborador.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, saveError = null, saveSuccess = false) }
            try {
                val newEvaluation = EvaluacionCreateDto(
                    colaboradorId = uiStateValue.selectedCollaboratorId,
                    rolActual = uiStateValue.currentRole,
                    liderEvaluador = uiStateValue.evaluatorLeader,
                    fechaEvaluacion = toIso8601(uiStateValue.evaluationDate),
                    tipoEvaluacion = uiStateValue.evaluationType,
                    skillsEvaluados = uiStateValue.skills.map {
                        SkillEvaluadoCreateDto(
                            nombre = it.name,
                            tipo = it.type,
                            nivelActual = convertSkillLevelToInt(it.currentLevel),
                            nivelRecomendado = convertSkillLevelToInt(it.recommendedLevel)
                        )
                    },
                    comentarios = uiStateValue.comments,
                    // TODO: Reemplazar por el ID del usuario que ha iniciado sesión.
                    usuarioResponsable = "675000000000000000001001" // Placeholder ID
                )

                Log.d("SaveEvaluation", "Enviando: $newEvaluation")
                RetrofitClient.evaluacionApiService.createEvaluacion(newEvaluation)
                _uiState.update { it.copy(isSaving = false, saveSuccess = true) }

            } catch (e: Exception) {
                Log.e("SaveEvaluation", "Error al guardar", e)
                _uiState.update { it.copy(isSaving = false, saveError = e.message ?: "Ocurrió un error desconocido") }
            }
        }
    }

    fun onSaveStatusConsumed() {
        _uiState.update { it.copy(saveSuccess = false, saveError = null) }
    }

    private fun toIso8601(dateString: String): String {
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = parser.parse(dateString)
            val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            date?.let { formatter.format(it) } ?: dateString
        } catch (e: Exception) {
            dateString
        }
    }

    private fun convertSkillLevelToInt(level: String): Int {
        return when (level) {
            "Básico" -> 1
            "Intermedio" -> 2
            "Avanzado" -> 3
            else -> 0
        }
    }

    fun cancelEvaluation() {
        // TODO: Implementar la lógica para cancelar la operación.
    }
}
