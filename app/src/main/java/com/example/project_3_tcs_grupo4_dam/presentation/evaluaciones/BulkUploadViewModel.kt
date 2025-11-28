package com.example.project_3_tcs_grupo4_dam.presentation.evaluaciones

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_3_tcs_grupo4_dam.data.model.EvaluacionCreateDto
import com.example.project_3_tcs_grupo4_dam.data.model.SkillEvaluadoCreateDto
import com.example.project_3_tcs_grupo4_dam.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

data class BulkUploadUiState(
    val fileUri: Uri? = null,
    val fileName: String? = null,
    val isProcessing: Boolean = false,
    val uploadSuccess: Boolean = false,
    val uploadError: String? = null
)

class BulkUploadViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(BulkUploadUiState())
    val uiState = _uiState.asStateFlow()

    fun onFileSelected(uri: Uri?, name: String?) {
        _uiState.update { it.copy(fileUri = uri, fileName = name, uploadError = null, uploadSuccess = false) }
    }

    fun processFile(context: Context) {
        val uri = _uiState.value.fileUri ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true, uploadError = null, uploadSuccess = false) }
            try {
                // Fetch collaborators to map names to IDs
                val collaborators = RetrofitClient.colaboradorApiService.getAllColaboradores()
                val collaboratorMap = collaborators.associateBy({ "${it.nombres} ${it.apellidos}".lowercase() }, { it.id })

                val evaluationsToCreate = mutableListOf<EvaluacionCreateDto>()

                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    BufferedReader(InputStreamReader(inputStream)).useLines { lines ->
                        lines.drop(1) // Skip header
                            .forEach { line ->
                                val tokens = line.split(",".toRegex()).map { it.trim() }
                                // Assuming format: Colaborador, Rol, Lider, Fecha, Skill, Nivel Actual, Nivel Rec, Tipo, Comentarios
                                if (tokens.size >= 8) {
                                    val collaboratorName = tokens[0]
                                    val collaboratorId = collaboratorMap[collaboratorName.lowercase()]

                                    if (collaboratorId != null) {
                                        val skill = SkillEvaluadoCreateDto(
                                            nombre = tokens[4],
                                            tipo = "TECNICO", // Assuming default, as it's not in CSV
                                            nivelActual = convertSkillLevelToInt(tokens[5]),
                                            nivelRecomendado = convertSkillLevelToInt(tokens[6]),
                                        )

                                        val newEvaluation = EvaluacionCreateDto(
                                            colaboradorId = collaboratorId,
                                            rolActual = tokens[1],
                                            liderEvaluador = tokens[2],
                                            fechaEvaluacion = toIso8601(tokens[3]),
                                            tipoEvaluacion = tokens[7],
                                            skillsEvaluados = listOf(skill),
                                            comentarios = tokens.getOrNull(8) ?: "",
                                            usuarioResponsable = "675000000000000000001001" // Placeholder ID
                                        )
                                        evaluationsToCreate.add(newEvaluation)
                                    } else {
                                        // Handle collaborator not found
                                        throw Exception("Colaborador no encontrado: $collaboratorName")
                                    }
                                }
                            }
                    }
                }
                RetrofitClient.evaluacionApiService.createEvaluationsBulk(evaluationsToCreate)
                _uiState.update { it.copy(isProcessing = false, uploadSuccess = true) }

            } catch (e: Exception) {
                _uiState.update { it.copy(isProcessing = false, uploadError = e.message ?: "Error procesando el archivo") }
            }
        }
    }

    fun onStatusConsumed() {
        _uiState.update { it.copy(uploadSuccess = false, uploadError = null) }
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
            dateString // Return original on error
        }
    }

    private fun convertSkillLevelToInt(level: String): Int {
        return when (level.lowercase()) {
            "básico" -> 1
            "intermedio" -> 2
            "avanzado" -> 3
            else -> 0 // No iniciado
        }
    }
}