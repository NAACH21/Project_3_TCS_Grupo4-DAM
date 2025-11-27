// EJEMPLO DE USO - MainActivity.kt
// Copia este código en tu MainActivity existente o úsalo como referencia

package com.example.project_3_tcs_grupo4_dam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.example.project_3_tcs_grupo4_dam.presentation.navigation.AppNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                AppNavigation()
            }
        }
    }
}

/* ============================================================
   EJEMPLO DE VERIFICACIÓN DE SESIÓN EN CUALQUIER PANTALLA
   ============================================================ */

/*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.project_3_tcs_grupo4_dam.data.local.SessionManager

@Composable
fun MiPantalla() {
    val context = LocalContext.current
    val sessionManager = SessionManager(context)

    // Verificar si hay sesión
    if (sessionManager.isLoggedIn()) {
        val token = sessionManager.getToken()
        val rol = sessionManager.getRol()
        val username = sessionManager.getUsername()
        val colaboradorId = sessionManager.getColaboradorId()

        // Usar los datos...
    } else {
        // Redirigir al login
    }
}
*/

/* ============================================================
   EJEMPLO DE USO DIRECTO DEL VIEWMODEL
   ============================================================ */

/*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project_3_tcs_grupo4_dam.presentation.auth.AuthViewModel
import com.example.project_3_tcs_grupo4_dam.presentation.auth.AuthViewModelFactory
import com.example.project_3_tcs_grupo4_dam.data.repository.AuthRepositoryImpl
import com.example.project_3_tcs_grupo4_dam.data.remote.RetrofitClient

@Composable
fun MiCustomLogin() {
    val context = LocalContext.current
    val repository = AuthRepositoryImpl(
        apiService = RetrofitClient.authApi,
        sessionManager = SessionManager(context)
    )

    val viewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(repository)
    )

    val uiState by viewModel.uiState.collectAsState()

    // Tu UI personalizada aquí
    Button(onClick = {
        viewModel.login("usuario", "contraseña")
    }) {
        if (uiState.isLoading) {
            CircularProgressIndicator()
        } else {
            Text("Login")
        }
    }

    // Manejar resultados
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            // Navegar según uiState.userRole
        }
    }
}
*/

/* ============================================================
   EJEMPLO DE INTERCEPTOR PARA AGREGAR TOKEN A TODAS LAS PETICIONES
   ============================================================ */

/*
// Agregar esto a RetrofitClient.kt si necesitas enviar el token en otras APIs

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val sessionManager: SessionManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = sessionManager.getToken()
        val request = chain.request().newBuilder().apply {
            if (token != null) {
                addHeader("Authorization", "Bearer $token")
            }
        }.build()
        return chain.proceed(request)
    }
}

// En RetrofitClient, modificar okHttpClient:
private val okHttpClient: OkHttpClient by lazy {
    OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(AuthInterceptor(sessionManager)) // Agregar esto
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
}
*/

/* ============================================================
   VERIFICAR EN AndroidManifest.xml
   ============================================================ */

/*
Asegúrate de tener estos permisos:

<manifest ...>
    <uses-permission android:name="android.permission.INTERNET" />

    <!-- Solo para desarrollo, permitir HTTP en vez de HTTPS -->
    <application
        android:usesCleartextTraffic="true"
        ...>
    </application>
</manifest>
*/

/* ============================================================
   CREDENCIALES DE PRUEBA
   ============================================================ */

/*
Según tu backend .NET, usa credenciales como:

Admin:
- Username: admin
- Password: admin123

Manager:
- Username: manager
- Password: manager123

Colaborador:
- Username: colaborador
- Password: colab123

Ajusta según tu base de datos.
*/

