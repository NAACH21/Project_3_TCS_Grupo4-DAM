package com.example.project_3_tcs_grupo4_dam.presentation.colaborador

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorDtos.ColaboradorReadDto
import com.example.project_3_tcs_grupo4_dam.data.repository.ColaboradorRepository
import com.example.project_3_tcs_grupo4_dam.data.repository.ColaboradorRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ColaboradoresViewModel : ViewModel() {

    // Repositorio que llama a la API
    private val repository: ColaboradorRepository = ColaboradorRepositoryImpl()

    // Estado: lista completa de colaboradores (sin filtrar)
    private val _colaboradoresCompletos = MutableStateFlow<List<ColaboradorReadDto>>(emptyList())

    // Estado: lista de colaboradores filtrada
    private val _colaboradores = MutableStateFlow<List<ColaboradorReadDto>>(emptyList())
    val colaboradores = _colaboradores.asStateFlow()

    // Estado: cargando
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    // Estado: error
    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    // Estados de filtros
    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _selectedArea = MutableStateFlow<String?>(null)
    val selectedArea = _selectedArea.asStateFlow()

    private val _selectedTipoSkill = MutableStateFlow<String?>(null)
    val selectedTipoSkill = _selectedTipoSkill.asStateFlow()

    private val _selectedSkill = MutableStateFlow<String?>(null)
    val selectedSkill = _selectedSkill.asStateFlow()

    private val _fechaInicio = MutableStateFlow<String?>(null)
    val fechaInicio = _fechaInicio.asStateFlow()

    private val _fechaFin = MutableStateFlow<String?>(null)
    val fechaFin = _fechaFin.asStateFlow()

    init {
        fetchColaboradores()
    }

    fun refresh() {
        fetchColaboradores()
    }

    private fun fetchColaboradores() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val lista = repository.getAllColaboradores()
                _colaboradoresCompletos.value = lista
                aplicarFiltros()
                Log.d("ColaboradoresVM", "Datos cargados: ${lista.size}")
            } catch (e: Exception) {
                Log.e("ColaboradoresVM", "Error al cargar colaboradores", e)
                _error.value = e.message ?: "Error desconocido"
                _colaboradores.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Aplica los filtros activos a la lista completa de colaboradores
     */
    private fun aplicarFiltros() {
        var listaFiltrada = _colaboradoresCompletos.value

        // Filtro por texto de búsqueda (nombre o skill)
        val textoBusqueda = _searchText.value.trim()
        if (textoBusqueda.isNotEmpty()) {
            listaFiltrada = listaFiltrada.filter { colaborador ->
                val nombreCompleto = "${colaborador.nombres} ${colaborador.apellidos}"
                val coincideNombre = nombreCompleto.contains(textoBusqueda, ignoreCase = true)

                val coincideSkill = colaborador.skills.any { skill ->
                    skill.nombre.contains(textoBusqueda, ignoreCase = true)
                }

                coincideNombre || coincideSkill
            }
        }

        // Filtro por área
        _selectedArea.value?.let { area ->
            if (area.isNotBlank()) {
                listaFiltrada = listaFiltrada.filter { it.area.equals(area, ignoreCase = true) }
            }
        }

        // Filtro por tipo de skill (Técnica o Blanda)
        _selectedTipoSkill.value?.let { tipo ->
            if (tipo.isNotBlank()) {
                listaFiltrada = listaFiltrada.filter { colaborador ->
                    colaborador.skills.any { skill ->
                        skill.tipo.equals(tipo, ignoreCase = true)
                    }
                }
            }
        }

        // Filtro por skill específica
        _selectedSkill.value?.let { skillNombre ->
            if (skillNombre.isNotBlank()) {
                listaFiltrada = listaFiltrada.filter { colaborador ->
                    colaborador.skills.any { skill ->
                        skill.nombre.equals(skillNombre, ignoreCase = true)
                    }
                }
            }
        }

        // Filtro por rango de fechas (usa fechaRegistro)
        val inicio = _fechaInicio.value
        val fin = _fechaFin.value

        if (inicio != null || fin != null) {
            listaFiltrada = listaFiltrada.filter { colaborador ->
                val fechaRegistro = colaborador.fechaRegistro ?: return@filter true

                var cumpleFiltro = true

                if (inicio != null) {
                    cumpleFiltro = cumpleFiltro && fechaRegistro >= inicio
                }

                if (fin != null) {
                    cumpleFiltro = cumpleFiltro && fechaRegistro <= fin
                }

                cumpleFiltro
            }
        }

        _colaboradores.value = listaFiltrada
        Log.d("ColaboradoresVM", "Filtros aplicados: ${listaFiltrada.size} de ${_colaboradoresCompletos.value.size}")
    }

    // Métodos para actualizar filtros
    fun onSearchTextChange(text: String) {
        _searchText.value = text
        aplicarFiltros()
    }

    fun onAreaSelected(area: String?) {
        _selectedArea.value = area
        aplicarFiltros()
    }

    fun onTipoSkillSelected(tipo: String?) {
        _selectedTipoSkill.value = tipo
        aplicarFiltros()
    }

    fun onSkillSelected(skill: String?) {
        _selectedSkill.value = skill
        aplicarFiltros()
    }

    fun onFechaInicioSelected(fecha: String?) {
        _fechaInicio.value = fecha
        aplicarFiltros()
    }

    fun onFechaFinSelected(fecha: String?) {
        _fechaFin.value = fecha
        aplicarFiltros()
    }

    fun limpiarFiltros() {
        _searchText.value = ""
        _selectedArea.value = null
        _selectedTipoSkill.value = null
        _selectedSkill.value = null
        _fechaInicio.value = null
        _fechaFin.value = null
        aplicarFiltros()
    }

    /**
     * Obtiene lista única de áreas disponibles
     */
    fun getAreasDisponibles(): List<String> {
        return _colaboradoresCompletos.value
            .map { it.area }
            .distinct()
            .sorted()
    }

    /**
     * Obtiene lista única de skills disponibles
     */
    fun getSkillsDisponibles(): List<String> {
        return _colaboradoresCompletos.value
            .flatMap { it.skills }
            .map { it.nombre }
            .distinct()
            .sorted()
    }

    fun eliminarColaborador(id: String) {
        viewModelScope.launch {
            try {
                repository.deleteColaborador(id)
                // Refrescar la lista completa desde el API para obtener el estado actualizado (INACTIVO)
                fetchColaboradores()
                Log.d("ColaboradoresVM", "Colaborador eliminado y lista refrescada: $id")
            } catch (e: Exception) {
                Log.e("ColaboradoresVM", "Error al eliminar colaborador", e)
                _error.value = e.message ?: "Error al eliminar"
            }
        }
    }
}