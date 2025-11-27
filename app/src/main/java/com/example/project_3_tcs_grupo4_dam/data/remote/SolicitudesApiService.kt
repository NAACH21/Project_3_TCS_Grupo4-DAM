package com.example.project_3_tcs_grupo4_dam.data.remote

import com.example.project_3_tcs_grupo4_dam.data.model.DeleteSolicitudResponse
import com.example.project_3_tcs_grupo4_dam.data.model.SolicitudCreateDto
import com.example.project_3_tcs_grupo4_dam.data.model.SolicitudReadDto
import com.example.project_3_tcs_grupo4_dam.data.model.SolicitudUpdateEstadoDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface SolicitudesApiService {

    @GET("api/Solicitudes")
    suspend fun getSolicitudes(): Response<List<SolicitudReadDto>>

    @GET("api/Solicitudes/{id}")
    suspend fun getSolicitudById(@Path("id") id: String): Response<SolicitudReadDto>

    @GET("api/Solicitudes/colaborador/{colaboradorId}")
    suspend fun getSolicitudesByColaborador(@Path("colaboradorId") colaboradorId: String): Response<List<SolicitudReadDto>>

    @POST("api/Solicitudes")
    suspend fun createSolicitud(@Body body: SolicitudCreateDto): Response<SolicitudReadDto>

    @PUT("api/Solicitudes/{id}/estado")
    suspend fun updateEstadoSolicitud(
        @Path("id") id: String,
        @Body body: SolicitudUpdateEstadoDto
    ): Response<SolicitudReadDto>

    @DELETE("api/Solicitudes/{id}")
    suspend fun deleteSolicitud(@Path("id") id: String): Response<DeleteSolicitudResponse>
}