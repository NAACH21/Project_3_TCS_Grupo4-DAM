package com.example.project_3_tcs_grupo4_dam.presentation.colaborador

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_3_tcs_grupo4_dam.data.model.*
import com.example.project_3_tcs_grupo4_dam.data.repository.ColaboradorRepository
import com.example.project_3_tcs_grupo4_dam.data.repository.ColaboradorRepositoryImpl
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ColaboradorFormViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val repository: ColaboradorRepository = ColaboradorRepositoryImpl()

    // ID del colaborador (null si es modo crear, con valor si es modo editar)
    private val colaboradorId: String? = savedStateHandle.get<String>("colaboradorId")

    // Campos del formulario - Datos personales
    private val _nombres = MutableStateFlow("")
    val nombres = _nombres.asStateFlow()

    private val _apellidos = MutableStateFlow("")
    val apellidos = _apellidos.asStateFlow()

    private val _area = MutableStateFlow("")
    val area = _area.asStateFlow()

    private val _rolActual = MutableStateFlow("")
    val rolActual = _rolActual.asStateFlow()

    // Skills
    private val _allSkills = MutableStateFlow<List<SkillDto>>(emptyList())
    val allSkills = _allSkills.asStateFlow()

    private val _skillSearchText = MutableStateFlow("")
    val skillSearchText = _skillSearchText.asStateFlow()

    private val _selectedSkills = MutableStateFlow<List<SkillDto>>(emptyList())
    val selectedSkills = _selectedSkills.asStateFlow()

    // Niveles
    private val _niveles = MutableStateFlow<List<NivelSkillDto>>(emptyList())
    val niveles = _niveles.asStateFlow()

    private val _selectedNivel = MutableStateFlow<NivelSkillDto?>(null)
    val selectedNivel = _selectedNivel.asStateFlow()

    // Certificaciones
    private val _certificaciones = MutableStateFlow<List<CertificacionCreateDto>>(emptyList())
    val certificaciones = _certificaciones.asStateFlow()

    // Disponibilidad
    private val _disponibilidadEstado = MutableStateFlow("Disponible")
    val disponibilidadEstado = _disponibilidadEstado.asStateFlow()

    private val _disponibilidadDias = MutableStateFlow<Int?>(null)
    val disponibilidadDias = _disponibilidadDias.asStateFlow()

    // Estados de UI
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving = _isSaving.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess = _saveSuccess.asStateFlow()

    // Determinar si es modo edición
    val isEditMode: Boolean = colaboradorId != null

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Cargar skills y niveles en paralelo
                val skillsDeferred = async { repository.getAllSkills() }
                val nivelesDeferred = async { repository.getAllNiveles() }

                _allSkills.value = skillsDeferred.await()
                _niveles.value = nivelesDeferred.await()

                Log.d("ColaboradorFormVM", "Skills cargados: ${_allSkills.value.size}")
                Log.d("ColaboradorFormVM", "Niveles cargados: ${_niveles.value.size}")

                // Si es modo edición, cargar los datos del colaborador
                if (colaboradorId != null) {
                    loadColaboradorData(colaboradorId)
                }
            } catch (e: Exception) {
                Log.e("ColaboradorFormVM", "Error al cargar datos iniciales", e)
                _errorMessage.value = "Error al cargar datos: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun loadColaboradorData(id: String) {
        try {
            val colaborador = repository.getColaboradorById(id)

            // Rellenar campos básicos
            _nombres.value = colaborador.nombres
            _apellidos.value = colaborador.apellidos
            _area.value = colaborador.area ?: "" // Manejo seguro de nulo
            _rolActual.value = colaborador.rolActual

            // Comparar por nombre, ya que ColaboradorReadDto.skills son objetos ColaboradorSkillDto
            val skillsSeleccionados = _allSkills.value.filter { skill ->
                colaborador.skills.any { it.nombre.equals(skill.nombre, ignoreCase = true) }
            }
            _selectedSkills.value = skillsSeleccionados

            // Seleccionar nivel
            val nivelSeleccionado = _niveles.value.find { it.codigo == colaborador.nivelCodigo }
            _selectedNivel.value = nivelSeleccionado

            // Cargar certificaciones
            _certificaciones.value = colaborador.certificaciones.map {
                CertificacionCreateDto(
                    nombre = it.nombre ?: "",
                    imagenUrl = it.imagenUrl,
                    fechaObtencion = it.fechaObtencion
                )
            }

            // Cargar disponibilidad (Manejo seguro de nulos)
            _disponibilidadEstado.value = colaborador.disponibilidad?.estado ?: "Disponible"
            _disponibilidadDias.value = colaborador.disponibilidad?.dias

            Log.d("ColaboradorFormVM", "Datos del colaborador cargados para edición")
        } catch (e: Exception) {
            Log.e("ColaboradorFormVM", "Error al cargar colaborador", e)
            _errorMessage.value = "Error al cargar colaborador: ${e.message}"
        }
    }

    // Métodos para actualizar campos
    fun onNombresChange(value: String) {
        _nombres.value = value
    }

    fun onApellidosChange(value: String) {
        _apellidos.value = value
    }

    fun onAreaChange(value: String) {
        _area.value = value
    }

    fun onRolActualChange(value: String) {
        _rolActual.value = value
    }

    fun onSkillSearchChange(value: String) {
        _skillSearchText.value = value
    }

    fun toggleSkillSelection(skill: SkillDto) {
        val currentSelected = _selectedSkills.value.toMutableList()
        if (currentSelected.any { it.id == skill.id }) {
            currentSelected.removeAll { it.id == skill.id }
        } else {
            currentSelected.add(skill)
        }
        _selectedSkills.value = currentSelected
    }

    fun removeSkill(skill: SkillDto) {
        _selectedSkills.value = _selectedSkills.value.filter { it.id != skill.id }
    }

    fun onNivelSelected(nivel: NivelSkillDto) {
        _selectedNivel.value = nivel
    }

    // Métodos para certificaciones
    fun addCertificacion() {
        val current = _certificaciones.value.toMutableList()
        current.add(CertificacionCreateDto("", null, null))
        _certificaciones.value = current
    }

    fun updateCertificacionNombre(index: Int, nombre: String) {
        val current = _certificaciones.value.toMutableList()
        if (index in current.indices) {
            current[index] = current[index].copy(nombre = nombre)
            _certificaciones.value = current
        }
    }

    fun updateCertificacionUrl(index: Int, url: String) {
        val current = _certificaciones.value.toMutableList()
        if (index in current.indices) {
            current[index] = current[index].copy(imagenUrl = url.ifBlank { null })
            _certificaciones.value = current
        }
    }

    fun updateCertificacionFecha(index: Int, fecha: String) {
        val current = _certificaciones.value.toMutableList()
        if (index in current.indices) {
            current[index] = current[index].copy(fechaObtencion = fecha.ifBlank { null })
            _certificaciones.value = current
        }
    }

    fun removeCertificacion(index: Int) {
        val current = _certificaciones.value.toMutableList()
        if (index in current.indices) {
            current.removeAt(index)
            _certificaciones.value = current
        }
    }

    fun onDisponibilidadEstadoChange(estado: String) {
        _disponibilidadEstado.value = estado
    }

    fun onDisponibilidadDiasChange(dias: String) {
        _disponibilidadDias.value = dias.toIntOrNull()
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    // Método principal para guardar (crear o actualizar)
    fun guardarColaborador() {
        viewModelScope.launch {
            _isSaving.value = true
            _errorMessage.value = null

            try {
                // Validaciones
                if (_nombres.value.isBlank()) {
                    _errorMessage.value = "El nombre es obligatorio"
                    _isSaving.value = false
                    return@launch
                }

                if (_apellidos.value.isBlank()) {
                    _errorMessage.value = "Los apellidos son obligatorios"
                    _isSaving.value = false
                    return@launch
                }

                if (_area.value.isBlank()) {
                    _errorMessage.value = "El área es obligatoria"
                    _isSaving.value = false
                    return@launch
                }

                if (_rolActual.value.isBlank()) {
                    _errorMessage.value = "El rol actual es obligatorio"
                    _isSaving.value = false
                    return@launch
                }

                if (_selectedSkills.value.isEmpty()) {
                    _errorMessage.value = "Debe seleccionar al menos un skill"
                    _isSaving.value = false
                    return@launch
                }

                if (_selectedNivel.value == null) {
                    _errorMessage.value = "Debe seleccionar un nivel"
                    _isSaving.value = false
                    return@launch
                }

                // Construir el DTO
                val dto = ColaboradorCreateDto(
                    nombres = _nombres.value.trim(),
                    apellidos = _apellidos.value.trim(),
                    area = _area.value.trim(),
                    rolActual = _rolActual.value.trim(),
                    skills = _selectedSkills.value.map { it.id }, // Asumiendo que para crear/editar se envían los IDs
                    nivelCodigo = _selectedNivel.value?.codigo,
                    certificaciones = _certificaciones.value.filter { it.nombre.isNotBlank() },
                    disponibilidad = DisponibilidadCreateDto(
                        estado = _disponibilidadEstado.value,
                        dias = _disponibilidadDias.value
                    )
                )

                // Decidir entre POST o PUT según el modo
                val result = if (colaboradorId == null) {
                    // Modo crear (POST)
                    repository.createColaborador(dto)
                } else {
                    // Modo editar (PUT)
                    repository.updateColaborador(colaboradorId, dto)
                }

                Log.d("ColaboradorFormVM", "Colaborador guardado: ${result.id}")

                // Éxito
                _saveSuccess.value = true

            } catch (e: Exception) {
                Log.e("ColaboradorFormVM", "Error al guardar colaborador", e)
                _errorMessage.value = "Error al guardar: ${e.message}"
            } finally {
                _isSaving.value = false
            }
        }
    }
}
