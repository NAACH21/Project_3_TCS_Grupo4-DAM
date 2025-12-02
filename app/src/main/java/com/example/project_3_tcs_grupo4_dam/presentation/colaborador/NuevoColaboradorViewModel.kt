package com.example.project_3_tcs_grupo4_dam.presentation.colaborador

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorDtos.ColaboradorCreateDto
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorDtos.SkillCreateDto
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorDtos.CertificacionCreateDto
import com.example.project_3_tcs_grupo4_dam.data.repository.ColaboradorRepository
import com.example.project_3_tcs_grupo4_dam.data.repository.ColaboradorRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NuevoColaboradorViewModel : ViewModel() {

    private val repository: ColaboradorRepository = ColaboradorRepositoryImpl()

    // Campos del formulario - Datos personales
    private val _nombres = MutableStateFlow("")
    val nombres = _nombres.asStateFlow()

    private val _apellidos = MutableStateFlow("")
    val apellidos = _apellidos.asStateFlow()

    private val _area = MutableStateFlow("")
    val area = _area.asStateFlow()

    private val _rolLaboral = MutableStateFlow("")
    val rolLaboral = _rolLaboral.asStateFlow()

    private val _disponibleParaMovilidad = MutableStateFlow(false)
    val disponibleParaMovilidad = _disponibleParaMovilidad.asStateFlow()

    // Skills embebidos
    private val _skills = MutableStateFlow<List<SkillCreateDto>>(emptyList())
    val skills = _skills.asStateFlow()

    // Certificaciones
    private val _certificaciones = MutableStateFlow<List<CertificacionCreateDto>>(emptyList())
    val certificaciones = _certificaciones.asStateFlow()

    // UI states
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving = _isSaving.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess = _saveSuccess.asStateFlow()

    // Métodos para actualizar campos
    fun onNombresChange(value: String) { _nombres.value = value }
    fun onApellidosChange(value: String) { _apellidos.value = value }
    fun onAreaChange(value: String) { _area.value = value }
    fun onRolLaboralChange(value: String) { _rolLaboral.value = value }
    fun onDisponibleParaMovilidadChange(value: Boolean) { _disponibleParaMovilidad.value = value }

    // Skills management
    fun addSkill() {
        // Regla de negocio: no permitir agregar skills si no existe al menos una certificación
        if (_certificaciones.value.isEmpty()) {
            _errorMessage.value = "Debe agregar al menos una certificación (nombre del PDF) antes de registrar skills"
            return
        }

        val current = _skills.value.toMutableList()
        current.add(SkillCreateDto(nombre = "", tipo = "TECNICO", nivel = 1, esCritico = false))
        _skills.value = current
    }

    fun updateSkillNombre(index: Int, nombre: String) {
        val current = _skills.value.toMutableList()
        if (index in current.indices) {
            current[index] = current[index].copy(nombre = nombre)
            _skills.value = current
        }
    }

    fun updateSkillTipo(index: Int, tipo: String) {
        val current = _skills.value.toMutableList()
        if (index in current.indices) {
            current[index] = current[index].copy(tipo = tipo)
            _skills.value = current
        }
    }

    fun updateSkillNivel(index: Int, nivel: Int) {
        val current = _skills.value.toMutableList()
        if (index in current.indices) {
            current[index] = current[index].copy(nivel = nivel)
            _skills.value = current
        }
    }

    fun updateSkillEsCritico(index: Int, esCritico: Boolean) {
        val current = _skills.value.toMutableList()
        if (index in current.indices) {
            current[index] = current[index].copy(esCritico = esCritico)
            _skills.value = current
        }
    }

    fun removeSkill(index: Int) {
        val current = _skills.value.toMutableList()
        if (index in current.indices) {
            current.removeAt(index)
            _skills.value = current
        }
    }

    // Certificaciones management
    fun addCertificacion() {
        val current = _certificaciones.value.toMutableList()
        current.add(CertificacionCreateDto(nombre = "", institucion = "", fechaObtencion = null, fechaVencimiento = null, archivoPdfUrl = null))
        _certificaciones.value = current
    }

    // Nuevo helper: permite agregar una certificación indicando solo el nombre del PDF (por ahora no hay uploader)
    fun addCertificacionConNombre(nombrePdf: String) {
        val current = _certificaciones.value.toMutableList()
        // Guardamos el "nombre del PDF" en el campo archivoPdfUrl para compatibilidad con el DTO y backend
        current.add(CertificacionCreateDto(nombre = "", institucion = "", fechaObtencion = null, fechaVencimiento = null, archivoPdfUrl = nombrePdf))
        _certificaciones.value = current
    }

    fun updateCertificacionNombre(index: Int, nombre: String) {
        val current = _certificaciones.value.toMutableList()
        if (index in current.indices) {
            current[index] = current[index].copy(nombre = nombre)
            _certificaciones.value = current
        }
    }

    fun updateCertificacionInstitucion(index: Int, institucion: String) {
        val current = _certificaciones.value.toMutableList()
        if (index in current.indices) {
            current[index] = current[index].copy(institucion = institucion)
            _certificaciones.value = current
        }
    }

    fun updateCertificacionFechaObtencion(index: Int, fecha: String?) {
        val current = _certificaciones.value.toMutableList()
        if (index in current.indices) {
            current[index] = current[index].copy(fechaObtencion = fecha)
            _certificaciones.value = current
        }
    }

    fun updateCertificacionFechaVencimiento(index: Int, fecha: String?) {
        val current = _certificaciones.value.toMutableList()
        if (index in current.indices) {
            current[index] = current[index].copy(fechaVencimiento = fecha)
            _certificaciones.value = current
        }
    }

    // Nota: este método ahora se usa también para guardar el nombre del PDF (si el uploader no está implementado)
    fun updateCertificacionArchivoUrl(index: Int, url: String?) {
        val current = _certificaciones.value.toMutableList()
        if (index in current.indices) {
            current[index] = current[index].copy(archivoPdfUrl = url)
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

    fun clearErrorMessage() { _errorMessage.value = null }

    fun saveColaborador() {
        viewModelScope.launch {
            _isSaving.value = true
            _errorMessage.value = null

            try {
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

                // Validaciones solicitadas: area y rol laboral obligatorios
                if (_area.value.isBlank()) {
                    _errorMessage.value = "El área es obligatoria"
                    _isSaving.value = false
                    return@launch
                }

                if (_rolLaboral.value.isBlank()) {
                    _errorMessage.value = "El rol laboral es obligatorio"
                    _isSaving.value = false
                    return@launch
                }

                // Regla de negocio: si hay skills pero no hay certificaciones -> error
                if (_skills.value.isNotEmpty() && _certificaciones.value.isEmpty()) {
                    _errorMessage.value = "Si agrega skills, debe agregar al menos una certificación"
                    _isSaving.value = false
                    return@launch
                }

                // Validar que cada certificación tenga nombre de PDF (archivoPdfUrl) no vacío
                _certificaciones.value.forEachIndexed { idx, cert ->
                    val nombrePdf = cert.archivoPdfUrl
                    if (nombrePdf.isNullOrBlank()) {
                        _errorMessage.value = "La certificación ${idx + 1} debe incluir el nombre del archivo PDF"
                        _isSaving.value = false
                        return@launch
                    }
                }

                val dto = ColaboradorCreateDto(
                    nombres = _nombres.value.trim(),
                    apellidos = _apellidos.value.trim(),
                    correo = "",
                    area = _area.value.trim(),
                    rolLaboral = _rolLaboral.value.trim(),
                    disponibleParaMovilidad = _disponibleParaMovilidad.value,
                    skills = _skills.value,
                    certificaciones = _certificaciones.value
                )

                repository.createColaborador(dto)
                _saveSuccess.value = true
            } catch (e: Exception) {
                Log.e("NuevoColaboradorVM", "Error al guardar colaborador", e)
                _errorMessage.value = e.message ?: "Error al guardar"
            } finally {
                _isSaving.value = false
            }
        }
    }
}