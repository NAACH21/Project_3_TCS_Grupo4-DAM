package com.example.project_3_tcs_grupo4_dam.data.repository

import com.example.project_3_tcs_grupo4_dam.data.model.CertificadoUploadResponse
import com.example.project_3_tcs_grupo4_dam.data.model.CertificadoDownloadUrlResponse
import java.io.File

/**
 * Interface Repository para gestión de certificados
 * Define operaciones de upload y descarga de PDFs certificados
 */
interface CertificadosRepository {

    /**
     * Sube un archivo PDF de certificación a Backblaze B2 mediante el backend .NET
     *
     * Flujo interno:
     * 1. Android selecciona PDF y llama a este método
     * 2. Se construye MultipartBody.Part con el archivo
     * 3. Backend recibe, valida y sube a B2
     * 4. Backend devuelve archivoPdfUrl (path en B2)
     * 5. Android guarda archivoPdfUrl en CertificacionCreateDto.archivoPdfUrl
     * 6. Al crear/editar colaborador, incluye archivoPdfUrl en la certificación
     *
     * @param file Archivo PDF a subir (máx 10MB)
     * @param colaboradorId ID del colaborador asociado (opcional)
     * @param nombreCertificacion Nombre descriptivo de la certificación (opcional)
     * @return Result<CertificadoUploadResponse> con archivoPdfUrl para guardar en Mongo
     */
    suspend fun uploadCertificado(
        file: File,
        colaboradorId: String? = null,
        nombreCertificacion: String? = null
    ): Result<CertificadoUploadResponse>

    /**
     * Obtiene la URL de descarga firmada para un PDF certificado desde Backblaze B2
     *
     * Flujo:
     * 1. ViewModel consulta archivoPdfUrl guardado en CertificacionReadDto
     * 2. Llama a este método pasando el archivoPdfUrl
     * 3. Backend consulta B2 y devuelve downloadUrl firmada
     * 4. Android abre downloadUrl en CustomTabs o navegador
     *
     * @param archivoPdfUrl Path almacenado en Mongo (ej: "userId/2025/12/abc123.pdf")
     * @return Result<CertificadoDownloadUrlResponse> con downloadUrl lista para usar
     */
    suspend fun getDownloadUrl(
        archivoPdfUrl: String
    ): Result<CertificadoDownloadUrlResponse>

    /**
     * Descarga un certificado PDF desde el bucket privado de Backblaze B2
     * El archivo se guarda temporalmente en cacheDir y se retorna para abrirlo con FileProvider
     *
     * Flujo:
     * 1. ViewModel llama a este método con el archivoPdfUrl
     * 2. Repository hace petición GET /api/certificados/download-file?archivoPdfUrl=...
     * 3. Backend autentica contra B2 y descarga el archivo
     * 4. Repository guarda el ResponseBody en un archivo temporal .pdf
     * 5. Retorna el File para que la UI lo abra con Intent + FileProvider
     *
     * @param archivoPdfUrl Path almacenado en Mongo (ej: "sin-asignar/2025/12/cert.pdf")
     * @return File temporal en cacheDir con el contenido del PDF
     * @throws Exception si falla la descarga o el archivo no se puede guardar
     */
    suspend fun downloadCertificadoPdf(archivoPdfUrl: String): File
}

