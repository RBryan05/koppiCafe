package com.grupo5.cafeteriaapp.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions         // Configura tipo de teclado
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person            // Ícono de persona para el header
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush                       // Para el gradiente de fondo
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation // Oculta texto con "•"
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grupo5.cafeteriaapp.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onRegisterSuccess: () -> Unit, // Callback al registrarse exitosamente
    onBackToLogin: () -> Unit      // Callback para volver al login
) {
    // Estados locales del formulario
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmarPassword by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    // A diferencia de LoginScreen, el loading viene del ViewModel como StateFlow,
    // no como estado local; así el ViewModel controla directamente el indicador
    val loading by viewModel.authLoading.collectAsState()

    // Fondo con gradiente café oscuro → café claro, igual que LoginScreen
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(
                colors = listOf(Color(0xFF6D4C41), Color(0xFFD7A86E))
            )),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Ícono de persona como header (en lugar de logo como en Login)
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = Color(0xFF6D4C41),
                    modifier = Modifier.size(56.dp)
                )
                Text("Crear cuenta", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6D4C41))
                Text(
                    "Regístrate para acceder a KoppiCafé",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )

                // Campo de correo con teclado tipo email
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo electrónico") },
                    leadingIcon = { Icon(Icons.Default.Email, null, tint = Color(0xFF6D4C41)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF6D4C41),
                        focusedLabelColor = Color(0xFF6D4C41)
                    )
                )

                // Campo de contraseña
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    leadingIcon = { Icon(Icons.Default.Lock, null, tint = Color(0xFF6D4C41)) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF6D4C41),
                        focusedLabelColor = Color(0xFF6D4C41)
                    )
                )

                // Campo de confirmación; se valida contra 'password' antes de registrar
                OutlinedTextField(
                    value = confirmarPassword,
                    onValueChange = { confirmarPassword = it },
                    label = { Text("Confirmar contraseña") },
                    leadingIcon = { Icon(Icons.Default.Lock, null, tint = Color(0xFF6D4C41)) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF6D4C41),
                        focusedLabelColor = Color(0xFF6D4C41)
                    )
                )

                // Muestra el error solo si hay mensaje
                if (error.isNotBlank()) {
                    Text(error, color = Color.Red, fontSize = 13.sp)
                }

                // Botón con validaciones en cadena usando 'when'
                Button(
                    onClick = {
                        when {
                            // Validación 1: campos vacíos
                            email.isBlank() || password.isBlank() -> error = "Completa todos los campos"
                            // Validación 2: contraseña mínimo 6 caracteres (requisito de Firebase Auth)
                            password.length < 6 -> error = "La contraseña debe tener al menos 6 caracteres"
                            // Validación 3: ambas contraseñas deben coincidir
                            password != confirmarPassword -> error = "Las contraseñas no coinciden"
                            // Validación 4: bloquea el registro con el correo del admin
                            email == AuthViewModel.ADMIN_EMAIL -> error = "Este correo no está disponible"
                            else -> {
                                error = ""
                                viewModel.registrar(email, password) { success, msg ->
                                    if (success) onRegisterSuccess()
                                    else error = msg ?: "Error al registrarse"
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6D4C41)),
                    enabled = !loading // Deshabilitado mientras carga
                ) {
                    if (loading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                    else Text("Crear cuenta", fontWeight = FontWeight.Bold)
                }

                // Enlace para volver al login
                TextButton(onClick = onBackToLogin) {
                    Text("¿Ya tienes cuenta? Inicia sesión", color = Color(0xFF6D4C41))
                }
            }
        }
    }
}