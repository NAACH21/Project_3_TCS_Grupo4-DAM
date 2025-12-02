package com.example.project_3_tcs_grupo4_dam.data.model

import com.google.gson.annotations.SerializedName

/**
 * DTOs para la gestión de certificados con Backblaze B2
 * (La lógica de B2 está en el backend .NET; Android solo consume endpoints REST)
 */

/**
 * Respuesta del endpoint POST /api/certificados/upload
 * Contiene la URL del PDF subido en Backblaze B2 (almacenado en archivoPdfUrl en Mongo)
 */
data class CertificadoUploadResponse(
    @SerializedName("fileName")
    val fileName: String,

    @SerializedName("archivoPdfUrl")
    val archivoPdfUrl: String,

    @SerializedName("contentType")
    val contentType: String,

    @SerializedName("fileSizeBytes")
    val fileSizeBytes: Long,

    @SerializedName("uploadedAt")
    val uploadedAt: String
)

/**
 * Respuesta del endpoint GET /api/certificados/download-url
 * Contiene la URL firmada para descargar desde Backblaze B2
 */
data class CertificadoDownloadUrlResponse(
    @SerializedName("downloadUrl")
    val downloadUrl: String,

    @SerializedName("isExternalUrl")
    val isExternalUrl: Boolean
)

