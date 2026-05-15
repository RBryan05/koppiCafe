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
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.grupo5.cafeteriaapp.data.model.Producto
import com.grupo5.cafeteriaapp.utils.guardarImagenInterna
import com.grupo5.cafeteriaapp.viewmodel.EstadoOperacion
import com.grupo5.cafeteriaapp.viewmodel.ProductoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarProductoScreen(
    productoId: String,          // ID del producto a editar, recibido desde la navegación
    viewModel: ProductoViewModel,
    onSuccess: () -> Unit,       // Callback que se ejecuta al guardar exitosamente
    onBack: () -> Unit           // Callback para regresar a la pantalla anterior
) {
    // Obtiene la lista actualizada de productos desde el ViewModel
    val productos by viewModel.productos.collectAsState()

    // Busca el producto específico por su ID para pre-rellenar el formulario
    val prod = productos.find { it.id == productoId }

    // Estados inicializados con los valores actuales del producto encontrado
    var stock by remember { mutableStateOf(prod?.stock?.toString() ?: "") }
    var disponible by remember { mutableStateOf(prod?.disponible ?: true) }

    val estado by viewModel.estado.collectAsState()
    val context = LocalContext.current

    // Si el stock es 0, el producto se fuerza como no disponible sin importar el switch
    val stockFinal = stock.toIntOrNull() ?: 0
    val disponibleFinal = if (stockFinal == 0) false else disponible

    // Cuando el stock cambia a un valor mayor que 0, reactiva la disponibilidad automáticamente
    LaunchedEffect(stockFinal) {
        if (stockFinal > 0 && !disponible) {
            disponible = true
        }
    }

    var nombre by remember { mutableStateOf(prod?.nombre ?: "") }
    var descripcion by remember { mutableStateOf(prod?.descripcion ?: "") }
    var precio by remember { mutableStateOf(prod?.precio?.toString() ?: "") }
    var categoria by remember { mutableStateOf(prod?.categoria ?: "") }
    var expandedCategoria by remember { mutableStateOf(false) }

    // Si el producto ya tiene imagen guardada, la convierte a Uri para mostrarla
    var imagenUri by remember {
        mutableStateOf(prod?.imagenUrl?.let { if (it.isNotBlank()) Uri.parse(it) else null })
    }

    // Lanzador para abrir el selector de imágenes del dispositivo
    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imagenUri = uri
    }

    // Cuando la operación es exitosa, resetea el estado y navega hacia atrás
    LaunchedEffect(estado) {
        if (estado is EstadoOperacion.Success) { viewModel.resetEstado(); onSuccess() }
    }

    // Validaciones en tiempo real para precio y stock
    val precioError = precio.isNotEmpty() && precio.toDoubleOrNull() == null
    val stockError = stock.isNotEmpty() && stock.toIntOrNull() == null

    // El botón guardar solo se habilita si nombre no está vacío,
    // no hay errores de validación y no hay una operación en curso
    val formularioValido = nombre.isNotBlank()
            && !precioError && !stockError
            && estado !is EstadoOperacion.Loading

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

            // Preview de imagen: muestra la actual o una nueva seleccionada; placeholder si no hay
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

            // Botón para abrir la galería; el texto cambia según si ya hay imagen cargada
            OutlinedButton(onClick = { imagePicker.launch("image/*") }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.AddPhotoAlternate, null)
                Spacer(Modifier.width(8.dp))
                Text(if (imagenUri != null) "Cambiar imagen" else "Seleccionar imagen")
            }

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )

            // Campo precio: el Regex bloquea cualquier caracter que no sea dígito o punto decimal
            OutlinedTextField(
                value = precio,
                onValueChange = { nuevoValor ->
                    if (nuevoValor.isEmpty() || nuevoValor.matches(Regex("^\\d*\\.?\\d*$"))) {
                        precio = nuevoValor
                    }
                },
                label = { Text("Precio (\$)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                isError = precioError, // Borde rojo si el valor no es un número válido
                supportingText = {
                    if (precioError) {
                        Text("Solo se permiten números", color = Color.Red, fontSize = 11.sp)
                    }
                }
            )

            // Campo stock: el Regex solo permite dígitos enteros, sin punto decimal
            OutlinedTextField(
                value = stock,
                onValueChange = { nuevoValor ->
                    if (nuevoValor.isEmpty() || nuevoValor.matches(Regex("^\\d+$"))) {
                        stock = nuevoValor
                    }
                },
                label = { Text("Stock") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                isError = stockError, // Borde rojo si el valor no es un entero válido
                supportingText = {
                    if (stockError) {
                        Text("Solo se permiten números enteros", color = Color.Red, fontSize = 11.sp)
                    }
                }
            )

            // Aviso naranja cuando el stock es 0: informa que se guardará como no disponible
            if (stockFinal == 0 && stock.isNotEmpty()) {
                Text(
                    "El producto se guardará como no disponible por tener stock 0",
                    fontSize = 11.sp,
                    color = Color(0xFFF9A825)
                )
            }

            // Dropdown de categorías; reutiliza la lista definida en CrearProductoScreen
            ExposedDropdownMenuBox(expanded = expandedCategoria, onExpandedChange = { expandedCategoria = !expandedCategoria }) {
                OutlinedTextField(value = categoria, onValueChange = {}, readOnly = true, label = { Text("Categoría") },
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) }, modifier = Modifier.fillMaxWidth().menuAnchor())
                ExposedDropdownMenu(expanded = expandedCategoria, onDismissRequest = { expandedCategoria = false }) {
                    categoriasCafeteria.forEach { cat ->
                        DropdownMenuItem(text = { Text(cat) }, onClick = { categoria = cat; expandedCategoria = false })
                    }
                }
            }

            // Toggle de disponibilidad; si el stock es 0 este valor será ignorado por disponibleFinal
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Disponible en menú", fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onBackground)
                Switch(checked = disponible, onCheckedChange = { disponible = it },
                    colors = SwitchDefaults.colors(checkedTrackColor = Color(0xFF6D4C41)))
            }

            // Muestra el mensaje de error si la operación en Firestore falló
            if (estado is EstadoOperacion.Error)
                Text((estado as EstadoOperacion.Error).mensaje, color = Color.Red)

            Button(
                onClick = {
                    // Si la URI no empieza con "/" es una imagen nueva del picker; se guarda internamente
                    // Si ya empieza con "/" es una ruta local existente; se usa directamente
                    val rutaFinal = if (imagenUri != null && !imagenUri.toString().startsWith("/"))
                        guardarImagenInterna(context, imagenUri!!)
                    else imagenUri?.toString() ?: ""

                    viewModel.editarProducto(productoId, Producto(
                        id = productoId,
                        nombre = nombre,
                        descripcion = descripcion,
                        precio = precio.toDoubleOrNull() ?: 0.0,
                        stock = stock.toIntOrNull() ?: 0,
                        categoria = categoria,
                        disponible = disponibleFinal, // Usa el valor calculado, no el del switch directamente
                        imagenUrl = rutaFinal
                    ))
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = formularioValido,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6D4C41))
            ) {
                if (estado is EstadoOperacion.Loading)
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                else Text("Actualizar Producto", fontWeight = FontWeight.Bold)
            }
        }
    }
}