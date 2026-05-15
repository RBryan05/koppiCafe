package com.grupo5.cafeteriaapp.ui.screens.auth

// Fondo con gradiente
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
// Configura el tipo de teclado (email, numérico, etc.)
import androidx.compose.foundation.text.KeyboardOptions
// Iconos de email y candado para los campos de texto
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
// Para el gradiente vertical del fondo
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
// Oculta el texto de la contraseña con puntos
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grupo5.cafeteriaapp.viewmodel.AuthViewModel
// Para cargar imágenes desde recursos locales (drawable)
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import com.grupo5.cafeteriaapp.R

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,       // Maneja la lógica de autenticación
    onLoginSuccess: () -> Unit,     // Callback al iniciar sesión exitosamente
    onNavigateToRegister: () -> Unit // Callback para ir a la pantalla de registro
) {
    // Estados locales del formulario, se recuerdan durante recomposiciones
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }    // Mensaje de error visible al usuario
    var loading by remember { mutableStateOf(false) } // Controla el indicador de carga

    // Contenedor raíz que ocupa toda la pantalla con gradiente café de fondo
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF6D4C41), Color(0xFFD7A86E)) // Café oscuro → café claro
                )
            ),
        contentAlignment = Alignment.Center // Centra la Card en la pantalla
    ) {
        // Tarjeta blanca que contiene el formulario
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(8.dp) // Sombra para efecto de profundidad
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp) // Espacio uniforme entre elementos
            ) {
                // Logo de la app desde recursos drawable
                Image(
                    painter = painterResource(id = R.drawable.ic_coffee),
                    contentDescription = "Logo café",
                    modifier = Modifier.size(64.dp)
                )

                // Nombre de la app
                Text("KoppiCafé", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6D4C41))

                // Subtítulo
                Text(
                    "Inicia sesión para continuar",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) // Gris semi-transparente
                )

                // Campo de correo electrónico con ícono y teclado tipo email
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

                // Campo de contraseña; PasswordVisualTransformation reemplaza caracteres con "•"
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

                // Muestra el error solo si hay un mensaje (isNotBlank ignora espacios vacíos)
                if (error.isNotBlank()) {
                    Text(error, color = Color.Red, fontSize = 13.sp)
                }

                // Botón principal; deshabilitado si los campos están vacíos o está cargando
                Button(
                    onClick = {
                        loading = true
                        error = ""
                        // Llama al ViewModel; el callback devuelve si fue exitoso y un mensaje
                        viewModel.login(email, password) { success, msg ->
                            loading = false
                            if (success) onLoginSuccess()
                            else error = msg ?: "Error al iniciar sesión"
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6D4C41)),
                    enabled = email.isNotBlank() && password.isNotBlank() && !loading
                ) {
                    // Muestra spinner mientras carga, texto cuando está listo
                    if (loading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                    else Text("Iniciar Sesión", fontWeight = FontWeight.Bold)
                }

                // Enlace para ir al registro
                TextButton(onClick = onNavigateToRegister) {
                    Text("¿No tienes cuenta? Regístrate", color = Color(0xFF6D4C41))
                }
            }
        }
    }
}