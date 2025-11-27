# Módulo de Autenticación - Sistema de Gestión de Talento

## 📁 Estructura Generada

```
com.example.project_3_tcs_grupo4_dam/
├── data/
│   ├── local/
│   │   └── SessionManager.kt          # Manejo seguro de sesiones (EncryptedSharedPreferences)
│   ├── model/
│   │   ├── ApiResponse.kt             # Wrapper genérico para respuestas del backend
│   │   └── AuthDtos.kt                # DTOs de Login y Register
│   ├── remote/
│   │   ├── AuthApiService.kt          # Interfaz Retrofit con endpoints
│   │   └── RetrofitClient.kt          # Configuración de Retrofit
│   └── repository/
│       ├── AuthRepository.kt          # Interfaz del repositorio
│       └── AuthRepositoryImpl.kt      # Implementación del repositorio
├── presentation/
│   ├── auth/
│   │   ├── AuthViewModel.kt           # Lógica de UI y estados
│   │   ├── AuthViewModelFactory.kt    # Factory para inyección de dependencias
│   │   └── LoginScreen.kt             # UI con Jetpack Compose
│   └── navigation/
│       ├── AppRoutes.kt               # Definición de rutas
│       └── AppNavigation.kt           # NavHost con navegación por roles
```

## 🚀 Uso Rápido

### 1. Integrar en MainActivity

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Project_3_TCS_Grupo4_DAMTheme {
                AppNavigation()
            }
        }
    }
}
```

### 2. Configurar Backend URL

El archivo `RetrofitClient.kt` está configurado con:
```kotlin
private const val BASE_URL = "http://10.0.2.2:5260/"
```

**Importante:**
- `10.0.2.2` apunta a `localhost` del host cuando usas el **emulador de Android**
- Si usas un **dispositivo físico**, cambia a tu IP local (ej: `http://10.0.2.2:5000/`)

### 3. Endpoints del Backend

#### Login
- **URL:** `POST /api/auth/login`
- **Request:**
  ```json
  {
    "username": "admin",
    "password": "password123"
  }
  ```
- **Response:**
  ```json
  {
    "success": true,
    "message": "Login exitoso",
    "data": {
      "token": "eyJhbGciOiJIUzI1NiIs...",
      "username": "admin",
      "rolSistema": "ADMIN",
      "colaboradorId": "123"
    }
  }
  ```

#### Register
- **URL:** `POST /api/auth/register`
- **Request:**
  ```json
  {
    "nombreCompleto": "Juan Pérez",
    "email": "juan@example.com",
    "password": "password123"
  }
  ```

## 🎯 Flujo de Navegación

```
Login Screen
    │
    ├─ Login Success (ADMIN) ─────────> Admin Home
    │
    ├─ Login Success (BUSINESS_MANAGER) ─> Manager Home
    │
    └─ Login Success (COLABORADOR) ────> Colaborador Home
```

## 🔐 Seguridad

### SessionManager
Usa **EncryptedSharedPreferences** para almacenar de forma segura:
- Token JWT
- Rol del usuario
- ID del colaborador
- Username

```kotlin
val sessionManager = SessionManager(context)

// Guardar sesión (se hace automáticamente después del login)
sessionManager.saveSession(token, rol, colaboradorId, username)

// Verificar sesión
if (sessionManager.isLoggedIn()) {
    val role = sessionManager.getRol()
    // Navegar al home correspondiente
}

// Cerrar sesión
sessionManager.clearSession()
```

## 📱 Características del LoginScreen

- ✅ Diseño moderno con gradiente (Material 3)
- ✅ Validación de campos
- ✅ Toggle de visibilidad de contraseña
- ✅ Indicador de carga (CircularProgressIndicator)
- ✅ Manejo de errores con Snackbar
- ✅ Navegación automática por rol
- ✅ Manejo de estados con StateFlow

## 🛠️ Patrones Implementados

### MVVM + Clean Architecture
```
UI (Compose) → ViewModel → Repository → API Service
                   ↓
              SessionManager
```

### Manejo de Estados
```kotlin
data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
    val userRole: String? = null
)
```

## 📝 Próximos Pasos (TODO)

1. **Implementar pantallas Home:**
   - `AdminHomeScreen.kt`
   - `ManagerHomeScreen.kt`
   - `ColaboradorHomeScreen.kt`

2. **Implementar RegisterScreen:**
   - Crear `RegisterScreen.kt` en `presentation/auth/`
   - Reutilizar `AuthViewModel.register()`

3. **Agregar interceptor de autenticación:**
   ```kotlin
   // En RetrofitClient.kt, agregar:
   class AuthInterceptor(private val sessionManager: SessionManager) : Interceptor {
       override fun intercept(chain: Interceptor.Chain): Response {
           val token = sessionManager.getToken()
           val request = chain.request().newBuilder()
               .apply {
                   if (token != null) {
                       addHeader("Authorization", "Bearer $token")
                   }
               }
               .build()
           return chain.proceed(request)
       }
   }
   ```

4. **Manejo de token expirado:**
   - Implementar refresh token
   - Redirigir al login si el token es inválido

## 🧪 Testing

### Credenciales de Prueba (según tu backend)
```
Admin:
- Username: admin
- Password: admin123

Manager:
- Username: manager
- Password: manager123

Colaborador:
- Username: colab1
- Password: colab123
```

## ⚠️ Notas Importantes

1. **Permisos de Internet:**
   Verifica que tengas en `AndroidManifest.xml`:
   ```xml
   <uses-permission android:name="android.permission.INTERNET" />
   ```

2. **Configuración de Red (solo para desarrollo):**
   Si tienes problemas de conexión, agrega en `AndroidManifest.xml`:
   ```xml
   <application
       android:usesCleartextTraffic="true"
       ...>
   ```

3. **Dependencias Requeridas:**
   Todas las dependencias ya están en tu `build.gradle.kts`:
   - Retrofit 2.9.0
   - Gson Converter
   - OkHttp Logging Interceptor
   - Security Crypto (EncryptedSharedPreferences)
   - Navigation Compose
   - Coroutines

## 📞 Soporte

Si encuentras algún problema:
1. Verifica que tu backend esté corriendo en `http://localhost:5000`
2. Revisa los logs de Retrofit en Logcat (búsqueda: "OkHttp")
3. Confirma que los endpoints coincidan con tu backend .NET

## 🎨 Personalización

### Cambiar colores del gradiente:
En `LoginScreen.kt`, líneas 70-75:
```kotlin
colors = listOf(
    Color(0xFF6366F1), // Cambiar color primario
    Color(0xFF8B5CF6)  // Cambiar color secundario
)
```

### Cambiar URL del backend:
En `RetrofitClient.kt`, línea 18:
```kotlin
private const val BASE_URL = "http://TU_IP:5260/"
```

---

**✨ Código generado con las mejores prácticas de Android Development**
- Kotlin Coroutines para operaciones asíncronas
- StateFlow para manejo reactivo de UI
- EncryptedSharedPreferences para seguridad
- Material 3 Design
- Clean Architecture

