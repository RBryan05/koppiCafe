package com.grupo5.cafeteriaapp.ui.screens.perfil

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(onBack: () -> Unit) {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("cafeteria_prefs", Context.MODE_PRIVATE)

    var nuevaPassword by remember { mutableStateOf("") }
    var confirmarPassword by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }
    var mensajeColor by remember { mutableStateOf(Color(0xFF2E7D32)) }
    var showDialog by remember { mutableStateOf(false) }
    var mostrarCambioPassword by remember { mutableStateOf(false) }

    var fotoUri by remember {
        mutableStateOf(prefs.getString("foto_perfil", null)?.let { Uri.parse(it) })
    }

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        fotoUri = uri
        prefs.edit().putString("foto_perfil", uri?.toString()).apply()
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Cambiar contraseña") },
            text = { Text("¿Confirmas el cambio de contraseña?") },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    user?.updatePassword(nuevaPassword)
                        ?.addOnSuccessListener {
                            mensaje = "✅ Contraseña actualizada correctamente"
                            mensajeColor = Color(0xFF2E7D32)
                            nuevaPassword = ""; confirmarPassword = ""; mostrarCambioPassword = false
                        }
                        ?.addOnFailureListener { mensaje = "❌ Error: ${it.message}"; mensajeColor = Color.Red }
                }) { Text("Confirmar", color = Color(0xFF6D4C41)) }
            },
            dismissButton = { TextButton(onClick = { showDialog = false }) { Text("Cancelar") } }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF6D4C41),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar con botón de foto
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier.size(100.dp).clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(3.dp, Color(0xFF6D4C41), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (fotoUri != null) {
                        Image(painter = rememberAsyncImagePainter(fotoUri), contentDescription = null,
                            contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                    } else {
                        Icon(Icons.Default.Person, null, tint = Color(0xFF6D4C41), modifier = Modifier.size(52.dp))
                    }
                }
                IconButton(
                    onClick = { imagePicker.launch("image/*") },
                    modifier = Modifier.size(32.dp).background(Color(0xFF6D4C41), CircleShape)
                ) {
                    Icon(Icons.Default.CameraAlt, null, tint = Color.White, modifier = Modifier.size(18.dp))
                }
            }

            Text(user?.email ?: "Usuario", fontSize = 16.sp, fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground)

            // Info de cuenta
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Información de cuenta", fontWeight = FontWeight.Bold, fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface)
                    Divider(color = MaterialTheme.colorScheme.outline)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Email, null, tint = Color(0xFF6D4C41))
                        Spacer(Modifier.width(8.dp))
                        Text(user?.email ?: "Sin correo", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Person, null, tint = Color(0xFF6D4C41))
                        Spacer(Modifier.width(8.dp))
                        Text("UID: ${user?.uid?.take(12)}...", fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    }
                }
            }

            // Botón expandir/colapsar cambio de contraseña
            OutlinedButton(onClick = { mostrarCambioPassword = !mostrarCambioPassword },
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Icon(if (mostrarCambioPassword) Icons.Default.ExpandLess else Icons.Default.ExpandMore, null)
                Spacer(Modifier.width(8.dp))
                Text(if (mostrarCambioPassword) "Ocultar cambio de contraseña" else "Cambiar contraseña")
            }

            if (mostrarCambioPassword) {
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lock, null, tint = Color(0xFF6D4C41))
                            Spacer(Modifier.width(8.dp))
                            Text("Nueva contraseña", fontWeight = FontWeight.Bold, fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface)
                        }
                        Divider(color = MaterialTheme.colorScheme.outline)
                        OutlinedTextField(value = nuevaPassword, onValueChange = { nuevaPassword = it },
                            label = { Text("Nueva contraseña") }, visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(), singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF6D4C41), focusedLabelColor = Color(0xFF6D4C41)))
                        OutlinedTextField(value = confirmarPassword, onValueChange = { confirmarPassword = it },
                            label = { Text("Confirmar contraseña") }, visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(), singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF6D4C41), focusedLabelColor = Color(0xFF6D4C41)))
                        if (mensaje.isNotBlank()) Text(mensaje, color = mensajeColor, fontSize = 13.sp)
                        Button(
                            onClick = {
                                when {
                                    nuevaPassword.length < 6 -> { mensaje = "❌ Mínimo 6 caracteres"; mensajeColor = Color.Red }
                                    nuevaPassword != confirmarPassword -> { mensaje = "❌ Las contraseñas no coinciden"; mensajeColor = Color.Red }
                                    else -> showDialog = true
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6D4C41))
                        ) { Text("Actualizar contraseña", fontWeight = FontWeight.Bold) }
                    }
                }
            }
        }
    }
}