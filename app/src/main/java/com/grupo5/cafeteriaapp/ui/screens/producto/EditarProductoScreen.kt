package com.grupo5.cafeteriaapp.ui.screens.producto

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.grupo5.cafeteriaapp.data.model.Producto
import com.grupo5.cafeteriaapp.utils.guardarImagenInterna
import com.grupo5.cafeteriaapp.viewmodel.EstadoOperacion
import com.grupo5.cafeteriaapp.viewmodel.ProductoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarProductoScreen(
    productoId: String,
    viewModel: ProductoViewModel,
    onSuccess: () -> Unit,
    onBack: () -> Unit
) {
    val productos by viewModel.productos.collectAsState()
    val prod = productos.find { it.id == productoId }
    val estado by viewModel.estado.collectAsState()
    val context = LocalContext.current

    var nombre by remember { mutableStateOf(prod?.nombre ?: "") }
    var descripcion by remember { mutableStateOf(prod?.descripcion ?: "") }
    var precio by remember { mutableStateOf(prod?.precio?.toString() ?: "") }
    var stock by remember { mutableStateOf(prod?.stock?.toString() ?: "") }
    var categoria by remember { mutableStateOf(prod?.categoria ?: "") }
    var disponible by remember { mutableStateOf(prod?.disponible ?: true) }
    var expandedCategoria by remember { mutableStateOf(false) }
    var imagenUri by remember {
        mutableStateOf(prod?.imagenUrl?.let { if (it.isNotBlank()) Uri.parse(it) else null })
    }

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imagenUri = uri
    }

    LaunchedEffect(estado) {
        if (estado is EstadoOperacion.Success) { viewModel.resetEstado(); onSuccess() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Producto", fontWeight = FontWeight.Bold) },
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
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Imagen del producto", fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onBackground)
            Box(
                modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(12.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (imagenUri != null) {
                    Image(painter = rememberAsyncImagePainter(imagenUri), contentDescription = null,
                        contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.AddPhotoAlternate, null,
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), modifier = Modifier.size(48.dp))
                        Text("Sin imagen", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    }
                }
            }
            OutlinedButton(onClick = { imagePicker.launch("image/*") }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.AddPhotoAlternate, null)
                Spacer(Modifier.width(8.dp))
                Text(if (imagenUri != null) "Cambiar imagen" else "Seleccionar imagen")
            }

            OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth(), maxLines = 3)
            OutlinedTextField(value = precio, onValueChange = { precio = it }, label = { Text("Precio ($)") },
                modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
            OutlinedTextField(value = stock, onValueChange = { stock = it }, label = { Text("Stock") },
                modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))

            ExposedDropdownMenuBox(expanded = expandedCategoria, onExpandedChange = { expandedCategoria = !expandedCategoria }) {
                OutlinedTextField(value = categoria, onValueChange = {}, readOnly = true, label = { Text("Categoría") },
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) }, modifier = Modifier.fillMaxWidth().menuAnchor())
                ExposedDropdownMenu(expanded = expandedCategoria, onDismissRequest = { expandedCategoria = false }) {
                    categoriasCafeteria.forEach { cat ->
                        DropdownMenuItem(text = { Text(cat) }, onClick = { categoria = cat; expandedCategoria = false })
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Disponible en menú", fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onBackground)
                Switch(checked = disponible, onCheckedChange = { disponible = it },
                    colors = SwitchDefaults.colors(checkedTrackColor = Color(0xFF6D4C41)))
            }

            if (estado is EstadoOperacion.Error)
                Text((estado as EstadoOperacion.Error).mensaje, color = Color.Red)

            Button(
                onClick = {
                    val rutaFinal = if (imagenUri != null && !imagenUri.toString().startsWith("/"))
                        guardarImagenInterna(context, imagenUri!!)
                    else imagenUri?.toString() ?: ""
                    viewModel.editarProducto(productoId, Producto(
                        id = productoId, nombre = nombre, descripcion = descripcion,
                        precio = precio.toDoubleOrNull() ?: 0.0, stock = stock.toIntOrNull() ?: 0,
                        categoria = categoria, disponible = disponible, imagenUrl = rutaFinal
                    ))
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = nombre.isNotBlank() && estado !is EstadoOperacion.Loading,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6D4C41))
            ) {
                if (estado is EstadoOperacion.Loading)
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                else Text("Actualizar Producto", fontWeight = FontWeight.Bold)
            }
        }
    }
}