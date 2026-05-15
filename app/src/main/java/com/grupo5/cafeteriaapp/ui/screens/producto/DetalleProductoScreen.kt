package com.grupo5.cafeteriaapp.ui.screens.producto

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter // Carga imagen desde ruta local o URL
import com.grupo5.cafeteriaapp.viewmodel.ProductoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleProductoScreen(
    productoId: String,
    viewModel: ProductoViewModel,
    onEditar: () -> Unit,
    onEliminar: () -> Unit,
    onBack: () -> Unit,
    isAdmin: Boolean
) {
    val productos by viewModel.productos.collectAsState()
    // Busca el producto por ID en la lista del ViewModel
    val prod = productos.find { it.id == productoId }
    var showDialog by remember { mutableStateOf(false) } // Controla el diálogo de confirmación de eliminación

    // Diálogo de confirmación antes de eliminar el producto
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Deseas eliminar ${prod?.nombre}?") },
            confirmButton = {
                TextButton(onClick = {
                    prod?.let { viewModel.eliminarProducto(it.id) }
                    showDialog = false; onEliminar()
                }) { Text("Eliminar", color = Color.Red) }
            },
            dismissButton = { TextButton(onClick = { showDialog = false }) { Text("Cancelar") } }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } },
                // Botones de editar y eliminar solo visibles para admin
                actions = {
                    if (isAdmin) {
                        IconButton(onClick = onEditar) { Icon(Icons.Default.Edit, null, tint = Color.White) }
                        IconButton(onClick = { showDialog = true }) { Icon(Icons.Default.Delete, null, tint = Color.White) }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF6D4C41),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        // Solo renderiza si el producto fue encontrado
        prod?.let {
            Column(modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())) {

                // Imagen grande del producto con badges superpuestos
                Box(
                    modifier = Modifier.fillMaxWidth().height(240.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    if (!it.imagenUrl.isNullOrBlank()) {
                        Image(painter = rememberAsyncImagePainter(it.imagenUrl), contentDescription = null,
                            contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                    } else {
                        Icon(Icons.Default.Coffee, null, tint = Color(0xFF6D4C41), modifier = Modifier.size(80.dp))
                    }

                    // Badge de categoría en la esquina inferior izquierda
                    Box(
                        modifier = Modifier.align(Alignment.BottomStart).padding(12.dp)
                            .clip(RoundedCornerShape(20.dp)).background(Color(0xFF6D4C41))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(it.categoria, color = Color.White, fontSize = 12.sp)
                    }

                    // Badge de disponibilidad en la esquina inferior derecha; verde o rojo según estado
                    Box(
                        modifier = Modifier.align(Alignment.BottomEnd).padding(12.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (it.disponible) Color(0xFF2E7D32) else Color.Red)
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(if (it.disponible) "Disponible" else "No disponible", color = Color.White, fontSize = 12.sp)
                    }
                }

                Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {

                    // Nombre del producto y precio en la misma fila
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically) {
                        Text(it.nombre, fontSize = 22.sp, fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.weight(1f))
                        // Precio formateado con separador de miles y 2 decimales
                        Text("$${"%,.2f".format(it.precio)}", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6D4C41))
                    }

                    Divider(color = MaterialTheme.colorScheme.outline)

                    // Tarjeta con descripción y stock
                    Card(
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            DetalleRow("Descripción", it.descripcion)
                            DetalleRow("Stock disponible", "${it.stock} unidades")
                        }
                    }

                    // Botones de editar/eliminar solo visibles para admin
                    if (isAdmin) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedButton(
                                onClick = onEditar,
                                modifier = Modifier.weight(1f).height(50.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Edit, null)
                                Spacer(Modifier.width(6.dp))
                                Text("Editar")
                            }
                            Button(
                                onClick = { showDialog = true },
                                modifier = Modifier.weight(1f).height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                            ) {
                                Icon(Icons.Default.Delete, null)
                                Spacer(Modifier.width(6.dp))
                                Text("Eliminar")
                            }
                        }
                    }
                }
            }
        }
    }
}

// Fila reutilizable de label + valor para mostrar datos del producto
@Composable
fun DetalleRow(label: String, value: String) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        Text(value, fontWeight = FontWeight.Normal, color = MaterialTheme.colorScheme.onSurface)
    }
}