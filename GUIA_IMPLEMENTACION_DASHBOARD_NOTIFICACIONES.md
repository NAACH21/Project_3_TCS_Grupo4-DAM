# 📱 GUÍA DE IMPLEMENTACIÓN - DASHBOARD DE NOTIFICACIONES Y GESTIÓN DE VACANTES

## ✅ Estado de Implementación

**Fecha:** 2 de Diciembre, 2025  
**Estado:** ✅ COMPLETADO - Listo para integración

---

## 📋 Resumen de Cambios

Se ha implementado exitosamente:

### 🆕 Nuevos Componentes Creados

#### **1. Data Layer (Modelos)**
- ✅ `AlertaDashboard.kt` - Modelo simplificado para UI de notificaciones
- ✅ `AnuncioVacanteRequest.kt` - Request para anuncio de vacantes

#### **2. API Services (Endpoints)**
- ✅ `AlertasApiService.kt` - 3 nuevos endpoints agregados:
  - `GET /api/alertas/dashboard/admin`
  - `GET /api/alertas/dashboard/colaborador/{id}`
  - `POST /api/alertas/anunciar-vacante`
- ✅ `VacanteApiService.kt` - Endpoint con filtro agregado:
  - `GET /api/vacantes?activa={boolean}`

#### **3. Repositorios**
- ✅ `NotificacionesRepository.kt` - Repositorio para dashboard de notificaciones
- ✅ `VacanteRepository.kt` - Actualizado con soporte para Result y filtros

#### **4. ViewModels**
- ✅ `NotificacionesViewModel.kt` - Actualizado con función `cargarNotificaciones()`
- ✅ `NewVacantViewModel.kt` - Actualizado con función `notificarAhora()`

#### **5. UI Components (Compose)**
- ✅ `NotificacionesDashboardScreen.kt` - Pantalla completa del dashboard
- ✅ `NewVacantScreen.kt` - Actualizado con diálogo de confirmación de anuncio
- ✅ `NotificacionesAdapter.kt` - Adapter RecyclerView (por si se necesita XML)

#### **6. Recursos (Drawables)**
- ✅ `ic_trending_up.xml` - Icono para Skill Gap
- ✅ `ic_school.xml` - Icono para Certificación
- ✅ `ic_work.xml` - Icono para Vacante Disponible
- ✅ `ic_notifications.xml` - Icono genérico
- ✅ `bg_indicator_unread.xml` - Indicador de no leída
- ✅ `item_notificacion_dashboard.xml` - Layout XML para RecyclerView

---

## 🚀 Cómo Integrar en la Navegación

### **Opción 1: Usar el Dashboard de Notificaciones (Recomendado)**

En tu archivo de navegación (probablemente en `presentation/navigation/`), agrega la ruta:

```kotlin
// En tu NavHost
composable("notificaciones_dashboard") {
    NotificacionesDashboardScreen(navController)
}
```

### **Opción 2: Actualizar la Pantalla Existente**

Si prefieres mantener `NotificacionesScreen.kt` pero agregar la funcionalidad del dashboard:

```kotlin
// Dentro de NotificacionesScreen.kt, agrega un botón o Tab
Button(onClick = { navController.navigate("notificaciones_dashboard") }) {
    Text("Ver Dashboard Unificado")
}
```

---

## 📊 Flujo de Uso del Dashboard

### **Para Administradores:**
1. Al abrir `NotificacionesDashboardScreen`, se carga automáticamente con `esAdmin = true`
2. Muestra todas las notificaciones del sistema
3. Visualización por colores según prioridad:
   - 🔴 **ROJO** - Alta prioridad (#FFEBEE)
   - 🟡 **AMARILLO** - Media prioridad (#FFFDE7)
   - 🟢 **VERDE** - Baja prioridad (#E8F5E9)

### **Para Colaboradores:**
1. Al abrir, se carga con `esAdmin = false` y su `userId`
2. Muestra solo sus notificaciones personalizadas
3. Tipos de notificaciones visualizadas:
   - 📈 **SKILL_GAP** - Brechas de habilidades detectadas
   - 🎓 **CERTIFICACION** - Certificaciones pendientes
   - 💼 **VACANTE_DISPONIBLE** - Nuevas vacantes disponibles
   - 🔔 **GENERICA** - Notificaciones generales

---

## 💼 Flujo de Creación de Vacantes con Notificación

### **Proceso Actual (Ya Implementado):**

1. Usuario completa el formulario de nueva vacante
2. Presiona "Guardar vacante"
3. **NUEVO:** Si la vacante se crea exitosamente, aparece un diálogo:
   ```
   🔔 ¿Notificar Vacante?
   
   La vacante ha sido creada exitosamente. 
   ¿Deseas enviar una notificación por correo 
   a los colaboradores elegibles ahora?
   
   [No, notificar después]  [Sí, notificar ahora]
   ```
4. **Si elige "Sí, notificar ahora":**
   - Se ejecuta `notificarAhora(vacanteId)`
   - Se envía el anuncio al backend
   - Muestra confirmación: "Notificación enviada exitosamente"
   - Regresa a la pantalla anterior

5. **Si elige "No, notificar después":**
   - Solo cierra el diálogo
   - Regresa a la pantalla anterior
   - Puede notificar manualmente más tarde

---

## 🔧 Configuración del Backend

### **Endpoints Necesarios en el Backend .NET:**

Tu backend debe implementar estos endpoints:

```csharp
// GET /api/alertas/dashboard/admin
[HttpGet("dashboard/admin")]
public async Task<IActionResult> GetDashboardAdmin()
{
    var alertas = await _alertasService.GetAlertasDashboardAdmin();
    return Ok(new ApiResponse<List<AlertaDashboard>>
    {
        Success = true,
        Message = "Dashboard cargado",
        Data = alertas
    });
}

// GET /api/alertas/dashboard/colaborador/{id}
[HttpGet("dashboard/colaborador/{id}")]
public async Task<IActionResult> GetDashboardColaborador(string id)
{
    var alertas = await _alertasService.GetAlertasDashboardColaborador(id);
    return Ok(new ApiResponse<List<AlertaDashboard>>
    {
        Success = true,
        Message = "Dashboard cargado",
        Data = alertas
    });
}

// POST /api/alertas/anunciar-vacante
[HttpPost("anunciar-vacante")]
public async Task<IActionResult> AnunciarVacante([FromBody] AnuncioVacanteRequest request)
{
    await _notificacionesService.EnviarAnuncioVacante(request.VacanteId);
    return Ok(new ApiResponse<object>
    {
        Success = true,
        Message = "Notificación enviada correctamente",
        Data = null
    });
}
```

### **Formato JSON de Respuesta Esperado:**

```json
{
  "success": true,
  "message": "Dashboard cargado",
  "data": [
    {
      "idReferencia": "675f2a3b1234567890abcdef",
      "titulo": "Brecha de Habilidades Detectada",
      "mensaje": "Necesitas mejorar en Java y Spring Boot",
      "fecha": "2025-12-01",
      "tipoOrigen": "SKILL_GAP",
      "activa": true,
      "colorPrioridad": "ROJO"
    },
    {
      "idReferencia": "675f2a3b1234567890abcd00",
      "titulo": "Nueva Vacante Disponible",
      "mensaje": "Backend Developer Senior - Área Tecnología",
      "fecha": "2025-12-02",
      "tipoOrigen": "VACANTE_DISPONIBLE",
      "activa": true,
      "colorPrioridad": "VERDE"
    }
  ]
}
```

---

## 🧪 Testing Rápido

### **Test 1: Verificar Dashboard de Notificaciones**

```kotlin
// En una activity o composable de prueba:
@Composable
fun TestDashboard() {
    val navController = rememberNavController()
    NotificacionesDashboardScreen(navController)
}
```

### **Test 2: Verificar Diálogo de Anuncio de Vacante**

1. Navega a "Nueva Vacante"
2. Completa el formulario
3. Presiona "Guardar vacante"
4. Verifica que aparezca el diálogo
5. Prueba ambos botones ("Sí" y "No")

---

## 📝 Notas Importantes

### **Advertencias del Compilador (Normales):**
- ⚠️ "Function is never used" - Son funciones que se usarán cuando integres la navegación
- ⚠️ "Class is never used" - El repositorio se usa en los ViewModels
- ✅ **No hay errores de compilación**

### **Archivos NO Modificados (Como se solicitó):**
- ✅ `AlertaDto.kt` - Modelo original intacto
- ✅ `NotificacionesScreen.kt` - Screen original preservado (se creó uno nuevo)
- ✅ Toda la lógica existente funciona igual

### **Compatibilidad:**
- ✅ **Jetpack Compose** - Implementación completa
- ✅ **Material Design 3** - UI moderna y consistente
- ✅ **Coroutines** - Operaciones asíncronas eficientes
- ✅ **StateFlow** - Manejo de estado reactivo

---

## 🎨 Personalización de Colores

Si deseas cambiar los colores del dashboard, edita estas constantes en `NotificacionesDashboardScreen.kt`:

```kotlin
private val BackgroundColor = Color(0xFFF7F4F2)  // Fondo general
private val TCSBlue = Color(0xFF00549F)          // Color corporativo
private val TextGray = Color(0xFF6D6D6D)         // Texto secundario

// Colores de prioridad (en AlertaDashboardCard)
ColorPrioridad.ROJO -> Color(0xFFFFEBEE)
ColorPrioridad.AMARILLO -> Color(0xFFFFFDE7)
ColorPrioridad.VERDE -> Color(0xFFE8F5E9)
```

---

## 🐛 Troubleshooting

### **Problema: "No se muestran las notificaciones"**
- ✅ Verifica que el backend esté corriendo en `http://10.242.111.84:5260/`
- ✅ Confirma que los endpoints estén implementados
- ✅ Revisa los logs del ViewModel: busca "NotificacionesVM" en Logcat

### **Problema: "Error al anunciar vacante"**
- ✅ Verifica que el ID de la vacante no sea vacío
- ✅ Confirma que el endpoint POST esté funcionando
- ✅ Revisa el cuerpo de la petición en los logs de Retrofit

### **Problema: "El diálogo no aparece"**
- ✅ Verifica que `SaveResult.Success` incluya el `vacanteId`
- ✅ Confirma que `showAnuncioDialog` se esté activando en el `LaunchedEffect`

---

## 📞 Soporte

Si encuentras algún problema:
1. Revisa los logs de Android Studio (filtro: "Notificaciones", "Vacante")
2. Verifica la respuesta del backend en Retrofit logs
3. Consulta esta guía para configuración correcta

---

## ✨ Próximos Pasos Sugeridos

1. **Integrar en la navegación principal** del proyecto
2. **Probar con datos reales** del backend
3. **Agregar Pull-to-Refresh** en el dashboard (opcional)
4. **Implementar notificaciones push** (Firebase - opcional)
5. **Agregar filtros avanzados** por tipo de notificación (opcional)

---

**¡Implementación Completa! 🎉**

El sistema está listo para usar. Solo falta integrar `NotificacionesDashboardScreen` en tu navegación principal.

