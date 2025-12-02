---

## 🔧 Configuración del Backend Requerida

El backend .NET debe implementar estos endpoints:

### GET /api/alertas/dashboard/admin

**Response:**
```json
{
  "success": true,
  "message": "Dashboard obtenido correctamente",
  "data": [
    {
      "idReferencia": "alerta-001",
      "titulo": "Brecha crítica en React",
      "mensaje": "Tu nivel en React está 2 puntos por debajo del requerido",
      "fecha": "2025-12-02T10:30:00Z",
      "tipoOrigen": "SKILL_GAP",
      "activa": true,
      "colorPrioridad": "ROJO"
    }
  ]
}
```

### GET /api/alertas/dashboard/colaborador/{id}

**Response:** (igual estructura que admin)

### POST /api/alertas/anunciar-vacante

**Request:**
```json
{
  "vacanteId": "vacante-456"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Vacante anunciada a 15 colaboradores elegibles",
  "data": null
}
```

---

## ✅ Checklist de Implementación

- [x] Modelo `AlertaDashboard.kt` creado
- [x] Endpoints en `AlertasApiService.kt` implementados
- [x] `NotificacionesRepository.kt` implementado
- [x] `NotificacionesViewModel.kt` mejorado con manejo de dashboard
- [x] `NotificacionesDashboardScreen.kt` implementado con UI completa
- [x] `VacantesViewModel.kt` creado para anuncio de vacantes
- [x] Funcionalidad de marcar como leída implementada
- [x] Contador de notificaciones no leídas funcional
- [x] Manejo de errores y estados de carga
- [x] Documentación completa

---

## 🚀 Próximos Pasos Recomendados

1. **Sincronización con Backend:**
   - Implementar endpoint PUT para marcar notificaciones como leídas en el servidor
   - Agregar persistencia de estado "leído" en la base de datos

2. **Notificaciones Push:**
   - Integrar Firebase Cloud Messaging (FCM)
   - Enviar notificaciones push cuando se anuncia una vacante

3. **Filtros Avanzados:**
   - Agregar filtros por tipo de notificación
   - Ordenamiento por fecha/prioridad

4. **Historial:**
   - Pantalla de historial de notificaciones archivadas
   - Función de archivar notificaciones

---

## 📞 Soporte

Para dudas o problemas con la implementación, revisar:
- Los logs en Android Studio (Tag: "NotificacionesVM", "VacantesVM")
- El archivo `EJEMPLOS_DE_USO.kt` para casos de uso adicionales
- La consola del backend para verificar que los endpoints responden correctamente

---

**Última actualización:** 2025-12-02  
**Versión:** 1.0.0  
**Desarrollador:** TCS Grupo 4 - DAM
# 📱 Guía de Implementación: Dashboard de Notificaciones Unificado

## 📋 Índice
1. [Resumen de Implementación](#resumen)
2. [Arquitectura](#arquitectura)
3. [Modelos de Datos](#modelos)
4. [Endpoints API](#endpoints)
5. [Componentes UI](#componentes)
6. [Uso y Ejemplos](#uso)
7. [Testing](#testing)

---

## 🎯 Resumen de Implementación

Se ha implementado exitosamente un **Dashboard de Notificaciones Unificado** que:

- ✅ Consume el endpoint simplificado de alertas del backend
- ✅ Muestra notificaciones categorizadas por tipo y prioridad
- ✅ Permite marcar notificaciones como leídas
- ✅ Integra contador de notificaciones no leídas
- ✅ Soporta anuncio de vacantes disponibles a colaboradores elegibles
- ✅ Maneja estados de carga, error y éxito

---

## 🏗️ Arquitectura

### Estructura de Paquetes

```
app/src/main/java/com/example/project_3_tcs_grupo4_dam/
├── data/
│   ├── model/
│   │   ├── AlertaDto.kt              # DTO complejo (MongoDB) - NO MODIFICADO
│   │   ├── AlertaDashboard.kt        # DTO simplificado para UI (NUEVO)
│   │   ├── AnuncioVacanteRequest.kt  # Request para anunciar vacantes
│   │   └── ApiResponse.kt            # Wrapper genérico
│   ├── remote/
│   │   └── AlertasApiService.kt      # Endpoints Retrofit
│   └── repository/
│       └── NotificacionesRepository.kt # Lógica de negocio
└── presentation/
    ├── notificaciones/
    │   ├── NotificacionesDashboardScreen.kt  # UI Dashboard (MEJORADA)
    │   └── NotificacionesViewModel.kt        # ViewModel (MEJORADA)
    └── vacantes/
        └── VacantesViewModel.kt              # ViewModel para anuncio (NUEVO)
```

---

## 📦 Modelos de Datos

### 1. AlertaDashboard.kt (Modelo Simplificado para UI)

```kotlin
data class AlertaDashboard(
    val idReferencia: String,        // ID único de la alerta
    val titulo: String,              // Título corto
    val mensaje: String,             // Descripción completa
    val fecha: String,               // Fecha formateada (ISO 8601)
    val tipoOrigen: TipoOrigenAlerta, // Categoría
    val activa: Boolean,             // Si está sin leer
    val colorPrioridad: ColorPrioridad // Color de prioridad
)

enum class TipoOrigenAlerta {
    SKILL_GAP,              // Brecha de habilidades
    CERTIFICACION,          // Certificación vencida/próxima
    GENERICA,              // Notificación general
    VACANTE_DISPONIBLE     // Nueva vacante disponible
}

enum class ColorPrioridad {
    ROJO,      // Alta prioridad
    AMARILLO,  // Media prioridad
    VERDE      // Baja prioridad
}
```

### 2. ApiResponse.kt (Wrapper Genérico)

```kotlin
data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T?
)
```

### 3. AnuncioVacanteRequest.kt

```kotlin
data class AnuncioVacanteRequest(
    val vacanteId: String
)
```

---

## 🌐 Endpoints API

### AlertasApiService.kt

```kotlin
interface AlertasApiService {
    
    // Dashboard para Administradores
    @GET("api/alertas/dashboard/admin")
    suspend fun getDashboardAdmin(): Response<ApiResponse<List<AlertaDashboard>>>
    
    // Dashboard para Colaboradores
    @GET("api/alertas/dashboard/colaborador/{id}")
    suspend fun getDashboardColaborador(@Path("id") id: String): Response<ApiResponse<List<AlertaDashboard>>>
    
    // Anunciar Vacante Disponible
    @POST("api/alertas/anunciar-vacante")
    suspend fun anunciarVacante(@Body request: AnuncioVacanteRequest): Response<ApiResponse<Unit>>
}
```

---

## 🎨 Componentes UI

### NotificacionesDashboardScreen.kt

**Características:**
- Carga automática de notificaciones según rol (Admin/Colaborador)
- Visualización con iconos y colores según tipo y prioridad
- Indicador visual de notificaciones no leídas (punto rojo)
- Al hacer clic en una notificación, se marca automáticamente como leída
- Badge con contador en la barra superior
- Manejo de estados: Loading, Error, Empty, Success

**Colores por Prioridad:**
- 🔴 **ROJO**: Fondo #FFEBEE (Alta prioridad)
- 🟡 **AMARILLO**: Fondo #FFFDE7 (Media prioridad)
- 🟢 **VERDE**: Fondo #E8F5E9 (Baja prioridad)

**Iconos por Tipo:**
- 📈 **SKILL_GAP**: TrendingUp (Naranja)
- 🎓 **CERTIFICACION**: School (Azul)
- 💼 **VACANTE_DISPONIBLE**: Work (Verde)
- 🔔 **GENERICA**: Notifications (Gris)

---

## 💻 Uso y Ejemplos

### 1. Cargar Dashboard de Notificaciones

**En NotificacionesDashboardScreen.kt:**

```kotlin
@Composable
fun NotificacionesDashboardScreen(navController: NavController) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    
    // Determinar rol
    val rolUsuario = sessionManager.getRol() ?: "COLABORADOR"
    val esAdmin = rolUsuario.equals("ADMIN", ignoreCase = true)
    val userId = sessionManager.getColaboradorId()
    
    // Instanciar ViewModel
    val viewModel: NotificacionesViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return NotificacionesViewModel(sessionManager, context) as T
            }
        }
    )
    
    // Cargar notificaciones
    LaunchedEffect(Unit) {
        viewModel.cargarNotificaciones(esAdmin, userId)
    }
    
    // Observar estados
    val alertasDashboard by viewModel.alertasDashboard.collectAsState()
    val unreadCount by viewModel.unreadCount.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    
    // UI...
}
```

### 2. Anunciar Vacante Disponible

**En VacantesScreen.kt (o cualquier pantalla de administración de vacantes):**

```kotlin
@Composable
fun VacantesScreen(navController: NavController) {
    val vacantesViewModel: VacantesViewModel = viewModel()
    
    // Observar estados
    val isAnunciando by vacantesViewModel.isAnunciando.collectAsState()
    val successMessage by vacantesViewModel.successMessage.collectAsState()
    val errorMessage by vacantesViewModel.errorMessage.collectAsState()
    
    // Mostrar snackbar de éxito
    LaunchedEffect(successMessage) {
        successMessage?.let { message ->
            // Mostrar Snackbar con el mensaje
            // vacantesViewModel.clearSuccessMessage()
        }
    }
    
    // Botón de anuncio
    Button(
        onClick = { 
            vacantesViewModel.anunciarVacante(vacanteId = "12345") 
        },
        enabled = !isAnunciando
    ) {
        if (isAnunciando) {
            CircularProgressIndicator(modifier = Modifier.size(20.dp))
        } else {
            Text("Anunciar Vacante")
        }
    }
}
```

### 3. Marcar Notificación como Leída

```kotlin
// Automático al hacer clic en una notificación
AlertaDashboardCard(
    alerta = alerta,
    onClick = { 
        selectedAlerta = alerta
        if (alerta.activa) {
            viewModel.marcarDashboardComoLeida(alerta.idReferencia)
        }
    }
)

// Manual desde el ViewModel
viewModel.marcarDashboardComoLeida(idReferencia = "alerta-123")
```

---

## 🧪 Testing

### Probar el Dashboard

1. **Como Administrador:**
   ```kotlin
   // Debe mostrar todas las notificaciones del sistema
   viewModel.cargarNotificaciones(esAdmin = true, userId = null)
   ```

2. **Como Colaborador:**
   ```kotlin
   // Debe mostrar solo las notificaciones del colaborador específico
   viewModel.cargarNotificaciones(esAdmin = false, userId = "colaborador-123")
   ```

### Probar Anuncio de Vacante

```kotlin
// Llamar al endpoint
vacantesViewModel.anunciarVacante(vacanteId = "vacante-456")

// Verificar en el Dashboard que se crearon las notificaciones
viewModel.cargarNotificaciones(esAdmin = false, userId = "colaborador-123")
```


