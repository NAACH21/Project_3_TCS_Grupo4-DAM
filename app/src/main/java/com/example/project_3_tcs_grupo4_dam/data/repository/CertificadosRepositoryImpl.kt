package com.example.project_3_tcs_grupo4_dam.data.repository

import android.content.Context
import android.util.Log
import com.example.project_3_tcs_grupo4_dam.data.model.CertificadoUploadResponse
import com.example.project_3_tcs_grupo4_dam.data.model.CertificadoDownloadUrlResponse
import com.example.project_3_tcs_grupo4_dam.data.remote.CertificadosApiService
import com.example.project_3_tcs_grupo4_dam.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import java.io.File
import java.io.FileOutputStream

/**
 * Implementación del Repository para gestión de certificados
 * Maneja upload y descarga de PDFs certificados mediante Backblaze B2 (gestionado por backend .NET)
 *
 * Patrón: Try-catch con Result<T> wrapper
 * Logging: INFO en éxito, ERROR en fallos
 *
 * @param context Context de la aplicación para guardar archivos temporales en cacheDir (opcional)
 * @param api Servicio Retrofit para llamadas HTTP al backend
 */
class CertificadosRepositoryImpl(
    private val context: Context? = null,
    private val api: CertificadosApiService = RetrofitClient.certificadosApi
) : CertificadosRepository {

    override suspend fun uploadCertificado(
        file: File,
        colaboradorId: String?,
        nombreCertificacion: String?
    ): Result<CertificadoUploadResponse> {
        return withContext(Dispatchers.IO) {
            try {
                // Validar que el archivo existe y es un PDF
                if (!file.exists()) {
                    Log.e(TAG, "❌ Archivo no existe: ${file.absolutePath}")
                    return@withContext Result.failure(
                        Exception("El archivo no existe: ${file.name}")
                    )
                }

                if (file.extension != "pdf" && !file.name.endsWith(".pdf")) {
                    Log.w(TAG, "⚠️  El archivo puede no ser PDF: ${file.name}")
                }

                if (file.length() > 10 * 1024 * 1024) { // 10MB
                    Log.e(TAG, "❌ Archivo demasiado grande: ${file.length()} bytes")
                    return@withContext Result.failure(
                        Exception("El archivo es demasiado grande (máx 10MB)")
                    )
                }

                // Construir MultipartBody.Part para el PDF
                val requestBody = file.asRequestBody("application/pdf".toMediaType())
                val filePart = MultipartBody.Part.createFormData(
                    "file",
                    file.name,
                    requestBody
                )

                Log.i(
                    TAG,
                    "📤 Iniciando upload de PDF: ${file.name} (${file.length()} bytes)"
                )

                // Llamar al endpoint (suspending)
                val response = api.uploadCertificado(
                    file = filePart,
                    colaboradorId = colaboradorId,
                    nombreCertificacion = nombreCertificacion
                )

                Log.i(
                    TAG,
                    "✅ PDF subido exitosamente. archivoPdfUrl: ${response.archivoPdfUrl}"
                )

                Result.success(response)

            } catch (e: HttpException) {
                // Manejo específico de errores HTTP del backend
                val errorMessage = when (e.code()) {
                    in 400..499 -> {
                        // Errores del cliente (datos inválidos, archivo muy grande, etc.)
                        Log.e(TAG, "❌ Error 4xx al subir certificado: ${e.code()} - ${e.message()}", e)
                        "Error al subir el certificado (datos inválidos o archivo muy grande)"
                    }
                    in 500..599 -> {
                        // Errores del servidor
                        Log.e(TAG, "❌ Error 5xx del servidor: ${e.code()} - ${e.message()}", e)
                        "No se pudo subir el certificado. Intenta de nuevo más tarde"
                    }
                    else -> {
                        Log.e(TAG, "❌ Error HTTP desconocido: ${e.code()}", e)
                        "Error al subir el certificado: ${e.message()}"
                    }
                }
                Result.failure(Exception(errorMessage))

            } catch (e: Exception) {
                // Otros errores (red, timeout, etc.)
                Log.e(TAG, "❌ Error al subir PDF certificado", e)
                val errorMessage = when {
                    e.message?.contains("timeout", ignoreCase = true) == true ->
                        "Tiempo de espera agotado. Verifica tu conexión e intenta nuevamente"
                    e.message?.contains("network", ignoreCase = true) == true ->
                        "Error de red. Verifica tu conexión a internet"
                    else ->
                        "Error al subir el certificado: ${e.message ?: "Error desconocido"}"
                }
                Result.failure(Exception(errorMessage))
            }
        }
    }

    override suspend fun getDownloadUrl(
        archivoPdfUrl: String
    ): Result<CertificadoDownloadUrlResponse> {
        return withContext(Dispatchers.IO) {
            try {
                if (archivoPdfUrl.isBlank()) {
                    Log.e(TAG, "❌ archivoPdfUrl vacío")
                    return@withContext Result.failure(
                        Exception("archivoPdfUrl no puede estar vacío")
                    )
                }

                Log.i(TAG, "🔗 Obteniendo URL de descarga para: $archivoPdfUrl")

                val response = api.getDownloadUrl(archivoPdfUrl)

                Log.i(TAG, "✅ URL de descarga obtenida exitosamente")

                Result.success(response)

            } catch (e: Exception) {
                Log.e(TAG, "❌ Error al obtener URL de descarga", e)
                Result.failure(e)
            }
        }
    }

    /**
     * Descarga un certificado PDF desde el bucket privado de B2 y lo guarda en cacheDir
     *
     * @param archivoPdfUrl Path relativo guardado en Mongo (ej: "sin-asignar/2025/12/cert.pdf")
     * @return File temporal con el contenido del PDF
     * @throws Exception si falla la descarga o no se puede guardar el archivo
     */
    override suspend fun downloadCertificadoPdf(archivoPdfUrl: String): File {
        return withContext(Dispatchers.IO) {
            try {
                if (context == null) {
                    throw Exception("Context no disponible para descargar archivo")
                }

                if (archivoPdfUrl.isBlank()) {
                    throw Exception("archivoPdfUrl no puede estar vacío")
                }

                Log.i(TAG, "📥 Descargando PDF desde B2: $archivoPdfUrl")

                // Llamar al endpoint que descarga desde B2 (bucket privado)
                val response = api.downloadCertificado(archivoPdfUrl)

                if (!response.isSuccessful) {
                    val errorMsg = when (response.code()) {
                        404 -> "Certificado no encontrado en el servidor"
                        403 -> "No tienes permisos para acceder a este certificado"
                        in 500..599 -> "Error del servidor al descargar el certificado"
                        else -> "Error al descargar certificado (código ${response.code()})"
                    }
                    throw Exception(errorMsg)
                }

                val body = response.body()
                    ?: throw Exception("Respuesta vacía del servidor")

                // Crear archivo temporal en cacheDir
                val tempFile = File(context.cacheDir, "cert_${System.currentTimeMillis()}.pdf")

                Log.i(TAG, "💾 Guardando PDF en: ${tempFile.absolutePath}")

                // Copiar el stream del ResponseBody al archivo
                body.byteStream().use { inputStream ->
                    FileOutputStream(tempFile).use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }

                val fileSize = tempFile.length()
                Log.i(TAG, "✅ PDF descargado exitosamente: ${fileSize / 1024} KB")

                tempFile

            } catch (e: HttpException) {
                val errorMsg = when (e.code()) {
                    404 -> "Certificado no encontrado"
                    403 -> "Sin permisos para acceder al certificado"
                    in 500..599 -> "Error del servidor al descargar"
                    else -> "Error HTTP ${e.code()}"
                }
                Log.e(TAG, "❌ Error HTTP al descargar PDF: ${e.code()}", e)
                throw Exception(errorMsg)

            } catch (e: Exception) {
                Log.e(TAG, "❌ Error al descargar PDF certificado", e)
                throw Exception("Error al descargar certificado: ${e.message}")
            }
        }
    }

    companion object {
        private const val TAG = "CertificadosRepository"
    }
}

