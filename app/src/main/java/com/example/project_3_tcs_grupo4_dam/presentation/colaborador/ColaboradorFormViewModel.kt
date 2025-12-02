package com.example.project_3_tcs_grupo4_dam.presentation.colaborador

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorDtos.ColaboradorCreateDto
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorDtos.ColaboradorReadDto
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorDtos.ColaboradorUpdateDto
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorDtos.SkillCreateDto
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorDtos.CertificacionCreateDto
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorDtos.SkillReadDto
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorDtos.CertificacionReadDto
import com.example.project_3_tcs_grupo4_dam.data.model.CatalogoDtos.NivelSkillDto
import com.example.project_3_tcs_grupo4_dam.data.model.CatalogoDtos.SkillCatalogItemDto
import com.example.project_3_tcs_grupo4_dam.data.repository.ColaboradorRepository
import com.example.project_3_tcs_grupo4_dam.data.repository.ColaboradorRepositoryImpl
import com.example.project_3_tcs_grupo4_dam.data.repository.CatalogoRepository
import com.example.project_3_tcs_grupo4_dam.data.repository.CatalogoRepositoryImpl
import com.example.project_3_tcs_grupo4_dam.data.repository.CertificadosRepository
import com.example.project_3_tcs_grupo4_dam.data.repository.CertificadosRepositoryImpl
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

class ColaboradorFormViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val repository: ColaboradorRepository = ColaboradorRepositoryImpl()
    private val catalogoRepository: CatalogoRepository = CatalogoRepositoryImpl()
    private val certificadosRepository: CertificadosRepository = CertificadosRepositoryImpl()

    // ID del colaborador (null si es modo crear, con valor si es modo editar)
    private val colaboradorId: String? = savedStateHandle.get<String>("colaboradorId")

    // Campos del formulario - Datos personales
    private val _nombres = MutableStateFlow("")
    val nombres = _nombres.asStateFlow()

    private val _apellidos = MutableStateFlow("")
    val apellidos = _apellidos.asStateFlow()

    private val _correo = MutableStateFlow("")
    val correo = _correo.asStateFlow()

    private val _area = MutableStateFlow("")
    val area = _area.asStateFlow()

    private val _rolLaboral = MutableStateFlow("")
    val rolLaboral = _rolLaboral.asStateFlow()

    private val _disponibleParaMovilidad = MutableStateFlow(false)
    val disponibleParaMovilidad = _disponibleParaMovilidad.asStateFlow()

    // Estado del colaborador (ACTIVO/INACTIVO) - solo visible en modo edición
    private val _estado = MutableStateFlow("ACTIVO")
    val estado = _estado.asStateFlow()

    // Catálogos
    private val _areas = MutableStateFlow<List<String>>(emptyList())
    val areas = _areas.asStateFlow()

    private val _rolesLaborales = MutableStateFlow<List<String>>(emptyList())
    val rolesLaborales = _rolesLaborales.asStateFlow()

    private val _tiposSkill = MutableStateFlow<List<String>>(emptyList())
    val tiposSkill = _tiposSkill.asStateFlow()

    private val _nivelesSkill = MutableStateFlow<List<NivelSkillDto>>(emptyList())
    val nivelesSkill = _nivelesSkill.asStateFlow()

    // Catálogo de skills para el diálogo de selección
    private val _skillCatalog = MutableStateFlow<List<SkillCatalogItemDto>>(emptyList())
    val skillCatalog = _skillCatalog.asStateFlow()

    // Estados del diálogo de selección de skill
    private val _showSkillPickerDialog = MutableStateFlow(false)
    val showSkillPickerDialog = _showSkillPickerDialog.asStateFlow()

    private val _selectedTipoSkill = MutableStateFlow("TECNICO")
    val selectedTipoSkill = _selectedTipoSkill.asStateFlow()

    private val _skillSearchText = MutableStateFlow("")
    val skillSearchText = _skillSearchText.asStateFlow()

    private val _filteredSkillSuggestions = MutableStateFlow<List<SkillCatalogItemDto>>(emptyList())
    val filteredSkillSuggestions = _filteredSkillSuggestions.asStateFlow()

    // Skills embebidos (editable)
    private val _skills = MutableStateFlow<List<SkillCreateDto>>(emptyList())
    val skills = _skills.asStateFlow()

    // Certificaciones embebidas (editable)
    private val _certificaciones = MutableStateFlow<List<CertificacionCreateDto>>(emptyList())
    val certificaciones = _certificaciones.asStateFlow()

    // Estados de UI
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving = _isSaving.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess = _saveSuccess.asStateFlow()

    // Estado de upload de PDFs por certificación (índice)
    private val _isUploadingCertificaciones = MutableStateFlow<List<Boolean>>(emptyList())
    val isUploadingCertificaciones = _isUploadingCertificaciones.asStateFlow()

    // Determinar si es modo edición
    val isEditMode: Boolean = colaboradorId != null

    init {
        loadCatalogos()
        if (isEditMode) {
            loadColaborador(colaboradorId!!)
        }
    }

    private fun loadCatalogos() {
        viewModelScope.launch {
            try {
                val catalogo = catalogoRepository.getCatalogoCompleto()
                _areas.value = catalogo.areas
                _rolesLaborales.value = catalogo.rolesLaborales
                _tiposSkill.value = catalogo.tiposSkill
                _nivelesSkill.value = catalogo.nivelesSkill
                Log.d("ColaboradorFormVM", "Catálogos cargados exitosamente")
            } catch (e: Exception) {
                Log.e("ColaboradorFormVM", "Error al cargar catálogos", e)
                // Fallback a valores por defecto
                _areas.value = listOf("Finanzas", "Tecnología", "Recursos Humanos", "Marketing")
                _rolesLaborales.value = listOf("Tech Lead", "Backend Developer", "Frontend Developer")
                _tiposSkill.value = listOf("TECNICO", "BLANDO")
                _nivelesSkill.value = listOf(
                    NivelSkillDto(1, "No iniciado"),
                    NivelSkillDto(2, "Básico"),
                    NivelSkillDto(3, "Intermedio"),
                    NivelSkillDto(4, "Avanzado")
                )
            }
        }
    }

    private fun loadColaborador(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val col = repository.getColaboradorById(id)
                mapReadDtoToState(col)
                Log.d("ColaboradorFormVM", "Colaborador cargado para edición: ${col.id}")
            } catch (e: Exception) {
                Log.e("ColaboradorFormVM", "Error al cargar colaborador", e)
                _errorMessage.value = e.message ?: "Error al cargar colaborador"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun mapReadDtoToState(col: ColaboradorReadDto) {
        _nombres.value = col.nombres
        _apellidos.value = col.apellidos
        _correo.value = col.correo
        _area.value = col.area
        _rolLaboral.value = col.rolLaboral
        _disponibleParaMovilidad.value = col.disponibleParaMovilidad
        _estado.value = col.estado ?: "ACTIVO" // Valor por defecto en caso de que sea nulo

        // Mapear skills read -> create
        _skills.value = col.skills.map { s: SkillReadDto ->
            SkillCreateDto(
                nombre = s.nombre,
                tipo = s.tipo,
                nivel = s.nivel,
                esCritico = s.esCritico
            )
        }

        // Mapear certificaciones read -> create
        _certificaciones.value = col.certificaciones.map { c: CertificacionReadDto ->
            CertificacionCreateDto(
                nombre = c.nombre,
                institucion = c.institucion,
                fechaObtencion = c.fechaObtencion,
                fechaVencimiento = c.fechaVencimiento,
                archivoPdfUrl = c.archivoPdfUrl
            )
        }
    }

    // Métodos para actualizar campos
    fun onNombresChange(value: String) { _nombres.value = value }
    fun onApellidosChange(value: String) { _apellidos.value = value }
    fun onCorreoChange(value: String) { _correo.value = value }
    fun onAreaChange(value: String) { _area.value = value }
    fun onRolLaboralChange(value: String) { _rolLaboral.value = value }
    fun onDisponibleParaMovilidadChange(value: Boolean) { _disponibleParaMovilidad.value = value }
    fun onEstadoChange(value: String) { _estado.value = value }

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

        // Sincronizar flags de upload: agregar false para la nueva certificación
        val uploadFlags = _isUploadingCertificaciones.value.toMutableList()
        uploadFlags.add(false)
        _isUploadingCertificaciones.value = uploadFlags
    }

    // Helper: agregar certificación indicando solo el nombre del PDF (uploader no implementado aún)
    fun addCertificacionConNombre(nombrePdf: String) {
        val current = _certificaciones.value.toMutableList()
        current.add(CertificacionCreateDto(nombre = "", institucion = "", fechaObtencion = null, fechaVencimiento = null, archivoPdfUrl = nombrePdf))
        _certificaciones.value = current

        // Sincronizar flags de upload: agregar false para la nueva certificación
        val uploadFlags = _isUploadingCertificaciones.value.toMutableList()
        uploadFlags.add(false)
        _isUploadingCertificaciones.value = uploadFlags
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

            // Sincronizar flags: remover el flag en la posición eliminada
            val uploadFlags = _isUploadingCertificaciones.value.toMutableList()
            if (index in uploadFlags.indices) {
                uploadFlags.removeAt(index)
                _isUploadingCertificaciones.value = uploadFlags
            }
        }
    }

    fun clearErrorMessage() { _errorMessage.value = null }

    /**
     * Sube un archivo PDF de certificación a Backblaze B2 mediante el backend
     * Flujo: File -> MultipartBody -> Backend -> B2 -> archivoPdfUrl en Mongo
     *
     * @param index Índice de la certificación en la lista
     * @param file Archivo PDF seleccionado por el usuario
     */
    fun uploadCertificacionPdf(index: Int, file: File) {
        if (index !in _certificaciones.value.indices) {
            _errorMessage.value = "Certificación no encontrada"
            return
        }

        viewModelScope.launch {
            // Marcar como subiendo
            val uploadFlags = _isUploadingCertificaciones.value.toMutableList()
            if (index in uploadFlags.indices) {
                uploadFlags[index] = true
                _isUploadingCertificaciones.value = uploadFlags
            }

            try {
                val nombreCert = _certificaciones.value[index].nombre
                val idColaborador = if (isEditMode) colaboradorId else null

                Log.i("ColaboradorFormVM", "📤 Iniciando upload de certificado (índice $index)")

                // Llamar al repositorio de certificados
                val result = certificadosRepository.uploadCertificado(
                    file = file,
                    colaboradorId = idColaborador,
                    nombreCertificacion = nombreCert.ifBlank { null }
                )

                result.onSuccess { uploadResponse ->
                    // Actualizar la URL del PDF en el DTO
                    val cert = _certificaciones.value[index]
                    val updated = cert.copy(archivoPdfUrl = uploadResponse.archivoPdfUrl)

                    val certsList = _certificaciones.value.toMutableList()
                    certsList[index] = updated
                    _certificaciones.value = certsList

                    Log.i("ColaboradorFormVM", "✅ PDF subido: ${uploadResponse.archivoPdfUrl}")
                    // Mensaje de éxito temporal (se mostrará en Snackbar)
                    _errorMessage.value = "✓ PDF subido exitosamente"
                }

                result.onFailure { exception ->
                    Log.e("ColaboradorFormVM", "❌ Error al subir PDF", exception)
                    // Usar el mensaje específico del repositorio (ya diferencia 4xx vs 5xx)
                    _errorMessage.value = exception.message ?: "Error desconocido al subir PDF"
                }

            } finally {
                // Marcar como NO subiendo
                val uploadFlags = _isUploadingCertificaciones.value.toMutableList()
                if (index in uploadFlags.indices) {
                    uploadFlags[index] = false
                    _isUploadingCertificaciones.value = uploadFlags
                }
            }
        }
    }

    // ========== Funciones para el diálogo de selección de skills ==========

    /**
     * Carga el catálogo de skills desde el backend
     */
    fun loadSkillsCatalogo() {
        viewModelScope.launch {
            try {
                val catalog = catalogoRepository.getSkillsCatalogo()
                _skillCatalog.value = catalog
                // Inicializar con el primer tipo disponible o TECNICO por defecto
                val firstTipo = _tiposSkill.value.firstOrNull() ?: "TECNICO"
                _selectedTipoSkill.value = firstTipo
                // Filtrar inmediatamente
                updateFilteredSkills()
                Log.d("ColaboradorFormVM", "Catálogo de skills cargado: ${catalog.size} items")
            } catch (e: Exception) {
                Log.e("ColaboradorFormVM", "Error al cargar catálogo de skills", e)
                _skillCatalog.value = emptyList()
            }
        }
    }

    /**
     * Abre el diálogo de selección de skill
     */
    fun openSkillPicker() {
        // No permitir abrir selector de skills si no hay certificaciones registradas
        if (_certificaciones.value.isEmpty()) {
            _errorMessage.value = "Debe agregar al menos una certificación antes de seleccionar skills"
            return
        }

        _skillSearchText.value = ""
        _showSkillPickerDialog.value = true
        // Cargar catálogo si aún no está cargado
        if (_skillCatalog.value.isEmpty()) {
            loadSkillsCatalogo()
        } else {
            updateFilteredSkills()
        }
    }

    /**
     * Cierra el diálogo de selección de skill
     */
    fun closeSkillPicker() {
        _showSkillPickerDialog.value = false
        _skillSearchText.value = ""
    }

    /**
     * Cambia el tipo de skill seleccionado en el diálogo
     */
    fun onTipoSkillSelected(tipo: String) {
        _selectedTipoSkill.value = tipo
        updateFilteredSkills()
    }

    /**
     * Actualiza el texto de búsqueda de skills
     */
    fun onSkillSearchTextChange(text: String) {
        _skillSearchText.value = text
        updateFilteredSkills()
    }

    /**
     * Recalcula la lista filtrada de skills según tipo y búsqueda
     */
    private fun updateFilteredSkills() {
        val tipo = _selectedTipoSkill.value
        val searchText = _skillSearchText.value.trim().lowercase()

        _filteredSkillSuggestions.value = _skillCatalog.value
            .filter { it.tipo == tipo }
            .filter {
                searchText.isEmpty() || it.nombre.lowercase().contains(searchText)
            }
            .sortedBy { it.nombre }
    }

    /**
     * Maneja el clic en una sugerencia de skill del catálogo
     */
    fun onSkillSuggestionClick(item: SkillCatalogItemDto) {
        // Verificar si el skill ya existe en la lista
        val alreadyExists = _skills.value.any {
            it.nombre.equals(item.nombre, ignoreCase = true) && it.tipo == item.tipo
        }

        if (alreadyExists) {
            _errorMessage.value = "El skill '${item.nombre}' ya está agregado"
            return
        }

        // Crear el nuevo skill con valores por defecto
        val newSkill = SkillCreateDto(
            nombre = item.nombre,
            tipo = item.tipo,
            nivel = 1, // Nivel por defecto
            esCritico = false
        )

        // Agregar a la lista
        val current = _skills.value.toMutableList()
        current.add(newSkill)
        _skills.value = current

        // Cerrar el diálogo
        closeSkillPicker()

        Log.d("ColaboradorFormVM", "Skill agregado: ${item.nombre} (${item.tipo})")
    }

    // Guardar colaborador (crear o actualizar)
    fun guardarColaborador() {
        viewModelScope.launch {
            _isSaving.value = true
            _errorMessage.value = null

            try {
                // Validaciones mínimas
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

                if (_correo.value.isBlank()) {
                    _errorMessage.value = "El correo es obligatorio"
                    _isSaving.value = false
                    return@launch
                }

                // Validar area y rol laboral obligatorios
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

                // Validación: si hay skills, debe haber AL MENOS UNA certificación con PDF subido
                if (_skills.value.isNotEmpty()) {
                    val tieneCertConPdf = _certificaciones.value.any { !it.archivoPdfUrl.isNullOrBlank() }
                    if (!tieneCertConPdf) {
                        _errorMessage.value = "Debes subir al menos un certificado en PDF para poder registrar skills"
                        _isSaving.value = false
                        return@launch
                    }
                }

                // Validar que cada certificación tenga archivoPdfUrl (nombre del PDF)
                _certificaciones.value.forEachIndexed { idx, cert ->
                    val nombrePdf = cert.archivoPdfUrl
                    if (nombrePdf.isNullOrBlank()) {
                        _errorMessage.value = "La certificación ${idx + 1} debe incluir el nombre del archivo PDF"
                        _isSaving.value = false
                        return@launch
                    }
                }

                // Construir DTO
                val dtoCreate = ColaboradorCreateDto(
                    nombres = _nombres.value.trim(),
                    apellidos = _apellidos.value.trim(),
                    correo = _correo.value.trim(),
                    area = _area.value.trim(),
                    rolLaboral = _rolLaboral.value.trim(),
                    disponibleParaMovilidad = _disponibleParaMovilidad.value,
                    skills = _skills.value,
                    certificaciones = _certificaciones.value
                )

                if (isEditMode && colaboradorId != null) {
                    val updateDto = ColaboradorUpdateDto(
                        nombres = dtoCreate.nombres,
                        apellidos = dtoCreate.apellidos,
                        correo = dtoCreate.correo,
                        area = dtoCreate.area,
                        rolLaboral = dtoCreate.rolLaboral,
                        disponibleParaMovilidad = dtoCreate.disponibleParaMovilidad,
                        skills = dtoCreate.skills,
                        certificaciones = dtoCreate.certificaciones,
                        estado = _estado.value // Enviar el estado seleccionado
                    )
                    repository.updateColaborador(colaboradorId, updateDto)
                } else {
                    repository.createColaborador(dtoCreate)
                }

                _saveSuccess.value = true

            } catch (e: Exception) {
                Log.e("ColaboradorFormVM", "Error al guardar colaborador", e)
                _errorMessage.value = e.message ?: "Error al guardar colaborador"
            } finally {
                _isSaving.value = false
            }
        }
    }
}
