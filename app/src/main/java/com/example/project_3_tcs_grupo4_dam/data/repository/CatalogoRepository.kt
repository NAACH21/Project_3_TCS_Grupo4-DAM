package com.example.project_3_tcs_grupo4_dam.data.repository

import com.example.project_3_tcs_grupo4_dam.data.model.CatalogoResponse
import com.example.project_3_tcs_grupo4_dam.data.remote.RetrofitClient

class CatalogoRepository {
    private val api = RetrofitClient.catalogoApi
    suspend fun getCatalogo(): CatalogoResponse = api.getCatalogo()
}
