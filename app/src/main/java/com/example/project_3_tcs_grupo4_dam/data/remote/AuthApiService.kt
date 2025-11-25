package com.example.project_3_tcs_grupo4_dam.data.remote

import com.example.project_3_tcs_grupo4_dam.data.model.ApiResponse
import com.example.project_3_tcs_grupo4_dam.data.model.AuthDtos
import com.example.project_3_tcs_grupo4_dam.data.model.RoleDto
import com.example.project_3_tcs_grupo4_dam.data.model.UsuarioDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * Interfaz Retrofit para los endpoints de autenticación y usuarios
 */
interface AuthApiService {

    @POST("api/auth/login")
    suspend fun login(
        @Body request: AuthDtos.LoginRequest
    ): Response<ApiResponse<AuthDtos.LoginResponse>>

    @POST("api/auth/register")
    suspend fun register(
        @Body request: AuthDtos.RegisterRequest
    ): Response<ApiResponse<AuthDtos.RegisterResponse>>

    // --- NUEVOS ENDPOINTS PARA OBTENER ADMINS ---
    // Cambiamos a minúsculas para evitar errores 404 comunes
    
    @GET("api/roles")
    suspend fun getRoles(): ApiResponse<List<RoleDto>>

    @GET("api/usuarios")
    suspend fun getUsuarios(): ApiResponse<List<UsuarioDto>>
}
