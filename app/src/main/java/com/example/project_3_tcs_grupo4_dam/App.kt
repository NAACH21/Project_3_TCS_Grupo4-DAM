package com.example.project_3_tcs_grupo4_dam

import android.app.Application
import com.example.project_3_tcs_grupo4_dam.data.remote.RetrofitClient

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        // Ajusta aquí la base URL del backend local
        // Si tu backend está en http://192.168.1.8:5260 úsalo tal cual
        RetrofitClient.setBaseUrl("http://192.168.1.8:5260")

        // Si necesitas inyectar un JWT conocido al arrancar (por ejemplo desde SharedPreferences):
        // val token = getSharedPreferences("auth", MODE_PRIVATE).getString("jwt", null)
        // token?.let { RetrofitClient.setJwtToken(it) }
    }
}

