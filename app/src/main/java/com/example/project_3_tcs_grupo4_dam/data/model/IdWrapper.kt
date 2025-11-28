package com.example.project_3_tcs_grupo4_dam.data.model

import com.google.gson.annotations.SerializedName

data class IdWrapper(
    @SerializedName("\$oid")
    val oid: String?
) {
    fun value(): String = oid ?: ""
}