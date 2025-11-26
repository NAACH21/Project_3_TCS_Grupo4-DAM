package com.example.project_3_tcs_grupo4_dam.presentation.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
<<<<<<< HEAD
=======
import com.example.project_3_tcs_grupo4_dam.R
import com.example.project_3_tcs_grupo4_dam.data.local.SessionManager
import com.example.project_3_tcs_grupo4_dam.data.remote.RetrofitClient
import com.example.project_3_tcs_grupo4_dam.data.repository.AuthRepositoryImpl
>>>>>>> origin/fix/Alertas

/**
 * Colores Corporativos TATA/ESAN
 */
private val BluePrimary = Color(0xFF00549F)
private val BlueDark = Color(0xFF1E4F89)
private val TextGray = Color(0xFF666666)
private val InputBackground = Color(0xFFF5F5F5)
private val CircleBackground = Color(0xFFE8EFF5)

/**
 * Pantalla de Login - Diseño Corporativo TATA/ESAN
 * Portal del Colaborador – SIGTI
 */
@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onLoginSuccess: (role: String) -> Unit,
    onNavigateToRegister: () -> Unit = {}
) {
    val context = LocalContext.current

    // Usamos el AuthViewModel proporcionado desde MainActivity/RootApp
    val viewModel = authViewModel

    val uiState by viewModel.uiState.collectAsState()

    // Estados para los campos
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    // Efecto para manejar el resultado del login
    LaunchedEffect(uiState) {
        if (uiState.isSuccess && uiState.userRole != null) {

            val sessionManager = SessionManager(context)
            val userRole = uiState.userRole!!

            // ⭐ GUARDA SESIÓN COMPLETA CON USUARIO ID ⭐
            val usuarioId = viewModel.getUsuarioId() ?: ""
            val colaboradorId = viewModel.getColaboradorId()
            val token = viewModel.getToken() ?: ""

            android.util.Log.d("LoginScreen", "=== GUARDANDO SESIÓN ===")
            android.util.Log.d("LoginScreen", "UsuarioId a guardar: '$usuarioId'")
            android.util.Log.d("LoginScreen", "ColaboradorId a guardar: '$colaboradorId'")
            android.util.Log.d("LoginScreen", "Rol a guardar: '$userRole'")
            android.util.Log.d("LoginScreen", "Username a guardar: '$username'")
            android.util.Log.d("LoginScreen", "Token a guardar: '${token.take(20)}...'")

            // ⭐ VALIDACIÓN FINAL: No permitir guardar sesión sin usuarioId ⭐
            if (usuarioId.isBlank()) {
                android.util.Log.e("LoginScreen", "❌ ERROR CRÍTICO: Intentando guardar sesión con usuarioId vacío")
                snackbarHostState.showSnackbar("Error: No se pudo obtener el ID de usuario del servidor")
                viewModel.resetState()
                return@LaunchedEffect
            }

            sessionManager.saveSession(
                token = token,
                rolSistema = userRole,
                colaboradorId = colaboradorId,
                username = username,
                usuarioId = usuarioId
            )

            // ⭐ VERIFICACIÓN: Leer inmediatamente para confirmar que se guardó ⭐
            val usuarioIdGuardado = sessionManager.getUsuarioId()
            val colaboradorIdGuardado = sessionManager.getColaboradorId()
            val rolGuardado = sessionManager.getRol()

            android.util.Log.d("LoginScreen", "=== VERIFICACIÓN POST-GUARDADO ===")
            android.util.Log.d("LoginScreen", "UsuarioId leído de SessionManager: '$usuarioIdGuardado'")
            android.util.Log.d("LoginScreen", "ColaboradorId leído de SessionManager: '$colaboradorIdGuardado'")
            android.util.Log.d("LoginScreen", "Rol leído de SessionManager: '$rolGuardado'")

            if (usuarioIdGuardado.isNullOrBlank()) {
                android.util.Log.e("LoginScreen", "❌ ERROR: El usuarioId NO se guardó correctamente en SessionManager")
                snackbarHostState.showSnackbar("Error al guardar la sesión. Intenta nuevamente.")
                viewModel.resetState()
                return@LaunchedEffect
            }

            android.util.Log.d("LoginScreen", "✅ Sesión guardada y verificada correctamente")

            // Navegar según el rol
            onLoginSuccess(userRole)
            viewModel.resetState()
        }

        if (uiState.errorMessage != null) {
            snackbarHostState.showSnackbar(uiState.errorMessage!!)
            viewModel.resetState()
        }
    }



    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                // ========== A. CABECERA DE LOGOS ==========
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Logo TATA
                    Image(
                        painter = painterResource(id = R.drawable.auth_logo_tata),
                        contentDescription = "Logo TATA",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.height(40.dp)
                    )

                    // Separador "x"
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = TextGray,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    // Logo ESAN
                    Image(
                        painter = painterResource(id = R.drawable.auth_logo_esan),
                        contentDescription = "Logo ESAN",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.height(40.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ========== B. TÍTULOS ==========
                Text(
                    text = "Portal del Colaborador – SIGTI",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = BluePrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "TCS HR Manager",
                    fontSize = 16.sp,
                    color = TextGray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(32.dp))

                // ========== C. ÍCONO CENTRAL (CANDADO GEOMÉTRICO) ==========
                Box(
                    modifier = Modifier.size(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Fondo circular
                    Surface(
                        modifier = Modifier.size(120.dp),
                        shape = CircleShape,
                        color = CircleBackground
                    ) {}

                    // Ícono de candado
                    Icon(
                        imageVector = Icons.Outlined.Lock,
                        contentDescription = "Candado de seguridad",
                        tint = BluePrimary,
                        modifier = Modifier.size(48.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // ========== D. FORMULARIO ==========
                // Campo: Correo Corporativo
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Usuario",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("usuario@tcs.com", color = TextGray) },
                        singleLine = true,
                        enabled = !uiState.isLoading,
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = InputBackground,
                            focusedContainerColor = InputBackground,
                            disabledContainerColor = InputBackground,
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = BluePrimary
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Campo: Contraseña
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Contraseña",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("••••••••", color = TextGray) },
                        singleLine = true,
                        enabled = !uiState.isLoading,
                        visualTransformation = if (passwordVisible) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Ícono de contraseña",
                                tint = TextGray
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) {
                                        Icons.Filled.Visibility
                                    } else {
                                        Icons.Filled.VisibilityOff
                                    },
                                    contentDescription = if (passwordVisible) {
                                        "Ocultar contraseña"
                                    } else {
                                        "Mostrar contraseña"
                                    },
                                    tint = TextGray
                                )
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = InputBackground,
                            focusedContainerColor = InputBackground,
                            disabledContainerColor = InputBackground,
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = BluePrimary
                        )
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ========== E. BOTÓN DE ACCIÓN ==========
                Button(
                    onClick = {
                        if (username.isNotBlank() && password.isNotBlank()) {
                            viewModel.login(username, password)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = !uiState.isLoading && username.isNotBlank() && password.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BlueDark,
                        disabledContainerColor = TextGray
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Ingresar",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ========== F. FOOTER ==========
                TextButton(
                    onClick = { /* Implementar lógica de recuperación */ }
                ) {
                    Text(
                        text = "¿Olvidaste tu contraseña?",
                        color = BluePrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
