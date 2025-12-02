package com.example.project_3_tcs_grupo4_dam.presentation.colaborador

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.project_3_tcs_grupo4_dam.data.model.ColaboradorDtos.ColaboradorReadDto
import com.example.project_3_tcs_grupo4_dam.data.repository.ColaboradorRepository
import com.example.project_3_tcs_grupo4_dam.data.repository.ColaboradorRepositoryImpl
import com.example.project_3_tcs_grupo4_dam.data.repository.CertificadosRepository
import com.example.project_3_tcs_grupo4_dam.data.repository.CertificadosRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class ColaboradorDetalleViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val repository: ColaboradorRepository = ColaboradorRepositoryImpl()
    private val certificadosRepository: CertificadosRepository =
        CertificadosRepositoryImpl(context = application.applicationContext)

    // Obtener el ID del colaborador de los argumentos de navegación
    private val colaboradorId: String = savedStateHandle.get<String>("colaboradorId") ?: ""

    // Estado: colaborador actual
    private val _colaborador = MutableStateFlow<ColaboradorReadDto?>(null)
    val colaborador = _colaborador.asStateFlow()

    // Estado: cargando
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    // Estado: error
    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    // Estado: URL de certificado a abrir
    private val _certificadoUrlToOpen = MutableStateFlow<String?>(null)
    val certificadoUrlToOpen = _certificadoUrlToOpen.asStateFlow()

    // Estado: mensaje de error al abrir certificado
    private val _certificadoError = MutableStateFlow<String?>(null)
    val certificadoError = _certificadoError.asStateFlow()

    // Estado: descargando PDF
    private val _isDownloadingPdf = MutableStateFlow(false)
    val isDownloadingPdf = _isDownloadingPdf.asStateFlow()

    // Estado: archivo PDF a abrir con FileProvider
    private val _pdfFileToOpen = MutableStateFlow<File?>(null)
    val pdfFileToOpen = _pdfFileToOpen.asStateFlow()

    // Estado: error al descargar PDF
    private val _pdfErrorMessage = MutableStateFlow<String?>(null)
    val pdfErrorMessage = _pdfErrorMessage.asStateFlow()

    init {
        fetchColaboradorDetalle()
    }

    private fun fetchColaboradorDetalle() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val colaborador = repository.getColaboradorById(colaboradorId)
                _colaborador.value = colaborador
                Log.d("ColaboradorDetalleVM", "Colaborador cargado: ${colaborador.nombres}")
            } catch (e: Exception) {
                Log.e("ColaboradorDetalleVM", "Error al cargar colaborador", e)
                _error.value = e.message ?: "Error desconocido"
                _colaborador.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Maneja el clic en el botón "Ver PDF" de una certificación
     * Obtiene la URL de descarga y la expone para que la UI la abra
     */
    fun onVerCertificadoClick(archivoPdfUrl: String?) {
        if (archivoPdfUrl.isNullOrBlank()) {
            _certificadoError.value = "No hay archivo PDF asociado a esta certificación"
            return
        }

        viewModelScope.launch {
            try {
                _certificadoError.value = null

                // Si la URL ya es absoluta (empieza con http), usarla directamente
                val finalUrl = if (archivoPdfUrl.startsWith("http", ignoreCase = true)) {
                    Log.d("ColaboradorDetalleVM", "URL absoluta detectada: $archivoPdfUrl")
                    archivoPdfUrl
                } else {
                    // Si es relativa, obtener URL firmada desde el backend
                    Log.d("ColaboradorDetalleVM", "Obteniendo URL de descarga para: $archivoPdfUrl")
                    val result = certificadosRepository.getDownloadUrl(archivoPdfUrl)

                    result.getOrElse {
                        Log.e("ColaboradorDetalleVM", "Error al obtener URL", it)
                        _certificadoError.value = "Error al obtener URL de descarga: ${it.message}"
                        return@launch
                    }.downloadUrl
                }

                Log.d("ColaboradorDetalleVM", "Abriendo PDF: $finalUrl")
                _certificadoUrlToOpen.value = finalUrl

            } catch (e: Exception) {
                Log.e("ColaboradorDetalleVM", "Error al procesar certificado", e)
                _certificadoError.value = "Error al abrir certificado: ${e.message}"
            }
        }
    }

    /**
     * Marca la URL del certificado como consumida después de abrirla
     */
    fun onCertificadoUrlConsumed() {
        _certificadoUrlToOpen.value = null
    }

    /**
     * Limpia el error de certificado
     */
    fun clearCertificadoError() {
        _certificadoError.value = null
    }

    /**
     * Descarga un certificado PDF desde el bucket privado de B2 y lo prepara para abrir con FileProvider
     *
     * @param archivoPdfUrl Path relativo guardado en Mongo (ej: "sin-asignar/2025/12/cert.pdf")
     */
    fun onVerCertificadoPdfClick(archivoPdfUrl: String?) {
        if (archivoPdfUrl.isNullOrBlank()) {
            _pdfErrorMessage.value = "Este certificado no tiene PDF asociado"
            return
        }

        viewModelScope.launch {
            try {
                _isDownloadingPdf.value = true
                _pdfErrorMessage.value = null

                Log.d("ColaboradorDetalleVM", "📥 Iniciando descarga de PDF: $archivoPdfUrl")

                // Descargar PDF desde B2 y guardarlo en cacheDir
                val file = certificadosRepository.downloadCertificadoPdf(archivoPdfUrl)

                Log.d("ColaboradorDetalleVM", "✅ PDF descargado: ${file.absolutePath}")

                // Exponer el archivo para que la UI lo abra con FileProvider
                _pdfFileToOpen.value = file

            } catch (e: Exception) {
                Log.e("ColaboradorDetalleVM", "❌ Error al descargar PDF", e)
                _pdfErrorMessage.value = e.message ?: "Error al descargar PDF"
            } finally {
                _isDownloadingPdf.value = false
            }
        }
    }

    /**
     * Marca el PDF como abierto (limpia el estado)
     */
    fun onPdfOpened() {
        _pdfFileToOpen.value = null
    }

    /**
     * Limpia el mensaje de error de descarga de PDF
     */
    fun clearPdfError() {
        _pdfErrorMessage.value = null
    }
}
