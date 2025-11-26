package com.example.project_3_tcs_grupo4_dam.data.remote

import com.example.project_3_tcs_grupo4_dam.data.model.ApiResponse
import com.example.project_3_tcs_grupo4_dam.data.model.CatalogoDtos.CatalogoReadDto
import com.example.project_3_tcs_grupo4_dam.data.model.CatalogoDtos.NivelSkillDto
import com.example.project_3_tcs_grupo4_dam.data.model.CatalogoDtos.SkillCatalogGroupDto
import com.example.project_3_tcs_grupo4_dam.data.model.CatalogoResponse
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * API Service para el módulo de Catálogo
 * Endpoints para obtener opciones precargadas (áreas, roles, niveles, tipos)
 */
interface CatalogoApiService {

    /**
     * Obtiene el catálogo completo con todas las secciones
     * GET /api/catalogo
     * Respuesta: { "success": true, "data": CatalogoReadDto }
     */
    @GET("api/catalogo")
    suspend fun getCatalogoCompleto(): ApiResponse<CatalogoReadDto>

    /**
     * Obtiene una sección específica del catálogo
     * GET /api/catalogo/seccion/{nombreSeccion}
     *
     * @param nombreSeccion: "areas", "roleslaborales", "nivelesskill", "tiposskill" (en minúsculas)
     * Respuesta genérica basada en el tipo de sección
     */
    @GET("api/catalogo/seccion/{nombreSeccion}")
    suspend fun getSeccionCatalogo(@Path("nombreSeccion") nombreSeccion: String): ApiResponse<Any>
    suspend fun getCatalogo(): CatalogoResponse

    @GET("api/catalogo/seccion/{nombre}")
    suspend fun getCatalogoSeccion(
        @Path("nombre") nombre: String
    ): Any
    /**
     * Obtiene específicamente la lista de áreas
     * GET /api/catalogo/seccion/areas
     */
    @GET("api/catalogo/seccion/areas")
    suspend fun getAreas(): ApiResponse<List<String>>

    /**
     * Obtiene específicamente la lista de roles laborales
     * GET /api/catalogo/seccion/roleslaborales
     */
    @GET("api/catalogo/seccion/roleslaborales")
    suspend fun getRolesLaborales(): ApiResponse<List<String>>

    /**
     * Obtiene específicamente la lista de niveles de skill
     * GET /api/catalogo/seccion/nivelesskill
     */
    @GET("api/catalogo/seccion/nivelesskill")
    suspend fun getNivelesSkill(): ApiResponse<List<NivelSkillDto>>

    /**
     * Obtiene específicamente la lista de tipos de skill
     * GET /api/catalogo/seccion/tiposskill
     */
    @GET("api/catalogo/seccion/tiposskill")
    suspend fun getTiposSkill(): ApiResponse<List<String>>

    /**
     * Obtiene el catálogo de skills agrupados por tipo
     * GET /api/catalogo/seccion/skillsCatalogo
     */
    @GET("api/catalogo/seccion/skillsCatalogo")
    suspend fun getSkillsCatalogo(): ApiResponse<List<SkillCatalogGroupDto>>
}
