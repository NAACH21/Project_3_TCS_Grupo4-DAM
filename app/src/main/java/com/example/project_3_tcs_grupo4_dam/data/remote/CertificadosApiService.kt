package com.example.project_3_tcs_grupo4_dam.data.remote

import com.example.project_3_tcs_grupo4_dam.data.model.CertificadoUploadResponse
import com.example.project_3_tcs_grupo4_dam.data.model.CertificadoDownloadUrlResponse
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

/**
 * Interface Retrofit para gestión de certificados
 * Endpoints: POST /api/certificados/upload, GET /api/certificados/download-url
 * La lógica de Backblaze B2 corre en el backend .NET; Android solo consume estos endpoints REST
 */
interface CertificadosApiService {

    /**
     * Sube un archivo PDF de certificación a Backblaze B2 mediante el backend
     *
     * @param file Parte del multipart con el PDF (application/pdf, máx 10MB)
     * @param colaboradorId ID del colaborador asociado (opcional, para referencia)
     * @param nombreCertificacion Nombre descriptivo de la certificación (opcional)
     * @return CertificadoUploadResponse con archivoPdfUrl que se guarda en Mongo
     */
    @Multipart
    @POST("api/certificados/upload")
    suspend fun uploadCertificado(
        @Part file: MultipartBody.Part,
        @Query("colaboradorId") colaboradorId: String? = null,
        @Query("nombreCertificacion") nombreCertificacion: String? = null
    ): CertificadoUploadResponse

    /**
     * Obtiene la URL de descarga (firmada por B2) para un PDF certificado
     *
     * @param archivoPdfUrl Path almacenado en Mongo (ej: "675000000000000000000002/2025/12/abc123-azure.pdf")
     * @return CertificadoDownloadUrlResponse con downloadUrl lista para usar en CustomTabs o navegador
     */
    @GET("api/certificados/download-url")
    suspend fun getDownloadUrl(
        @Query("archivoPdfUrl") archivoPdfUrl: String
    ): CertificadoDownloadUrlResponse

    /**
     * Descarga el archivo PDF desde el bucket privado de Backblaze B2
     * El backend autentica contra B2 y devuelve el archivo como stream
     *
     * @param archivoPdfUrl Path almacenado en Mongo (ej: "sin-asignar/2025/12/3fbd3a9f-certificado-1.pdf")
     * @return Response<ResponseBody> con el contenido del PDF (Content-Type: application/pdf)
     */
    @GET("api/certificados/download-file")
    @Streaming
    suspend fun downloadCertificado(
        @Query("archivoPdfUrl") archivoPdfUrl: String
    ): Response<ResponseBody>
}

