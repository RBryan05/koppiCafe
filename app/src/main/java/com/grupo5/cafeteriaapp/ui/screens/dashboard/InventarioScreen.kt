package com.grupo5.cafeteriaapp.ui.screens.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.grupo5.cafeteriaapp.R
import com.grupo5.cafeteriaapp.viewmodel.ProductoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventarioScreen(viewModel: ProductoViewModel, onBack: () -> Unit) {
    val productos by viewModel.productos.collectAsState()
    LaunchedEffect(Unit) { viewModel.cargarProductos() }

    val stockBajo = productos.filter { it.stock < 5 }
    val stockNormal = productos.filter { it.stock in 5..20 }
    val stockAlto = productos.filter { it.stock > 20 }

    val stockPorCategoria = productos.groupBy { it.categoria }
        .mapValues { entry -> entry.value.sumOf { it.stock } }
        .entries.sortedByDescending { it.value }
    val maxStock = stockPorCategoria.maxOfOrNull { it.value } ?: 1

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inventario", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2E7D32),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Chips resumen
            Row(
                modifier = Modifier.fillMaxWidth().background(Color(0xFFF1F8E9)).padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StockChip(R.drawable.ic_circle_red, "Bajo", stockBajo.size, Color(0xFFFFEBEE), Color(0xFFC62828))
                StockChip(R.drawable.ic_circle_yellow, "Normal", stockNormal.size, Color(0xFFFFFDE7), Color(0xFFF9A825))
                StockChip(R.drawable.ic_circle_green, "Alto", stockAlto.size, Color(0xFFE8F5E9), Color(0xFF2E7D32))
            }

            if (productos.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay productos en inventario", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Gráfica de barras por categoría
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(3.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    "Stock por Categoría", fontWeight = FontWeight.Bold, fontSize = 16.sp,
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )
                                stockPorCategoria.forEach { (categoria, total) ->
                                    val porcentaje = total.toFloat() / maxStock.toFloat()
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            categoria, fontSize = 11.sp, color = Color.Gray, maxLines = 1,
                                            overflow = TextOverflow.Ellipsis, modifier = Modifier.width(110.dp)
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Box(
                                            modifier = Modifier.weight(1f).height(20.dp)
                                                .background(Color(0xFFFBE9E7), RoundedCornerShape(4.dp))
                                        ) {
                                            Box(
                                                modifier = Modifier.fillMaxHeight().fillMaxWidth(porcentaje)
                                                    .background(Color(0xFF6D4C41), RoundedCornerShape(4.dp))
                                            )
                                        }
                                        Spacer(Modifier.width(8.dp))
                                        Text(
                                            "$total", fontSize = 11.sp, fontWeight = FontWeight.Bold,
                                            color = Color(0xFF6D4C41), modifier = Modifier.width(30.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    items(productos.sortedBy { it.stock }) { prod ->
                        val (bgColor, textColor, labelTexto, iconRes) = when {
                            prod.stock < 5 -> Triple(Color(0xFFFFEBEE), Color(0xFFC62828), "Stock bajo")
                                .let { (a, b, c) -> Quad(a, b, c, R.drawable.ic_warning) }
                            prod.stock <= 20 -> Triple(Color(0xFFFFFDE7), Color(0xFFF9A825), "Stock normal")
                                .let { (a, b, c) -> Quad(a, b, c, R.drawable.ic_circle_yellow) }
                            else -> Triple(Color(0xFFE8F5E9), Color(0xFF2E7D32), "Stock alto")
                                .let { (a, b, c) -> Quad(a, b, c, R.drawable.ic_circle_green) }
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(3.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier.size(56.dp).clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFFFBE9E7)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (prod.imagenUrl.isNotEmpty()) {
                                        AsyncImage(
                                            model = prod.imagenUrl, contentDescription = null,
                                            contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize()
                                        )
                                    } else {
                                        Icon(Icons.Default.Coffee, null, tint = Color(0xFF6D4C41), modifier = Modifier.size(28.dp))
                                    }
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(prod.nombre, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Text(prod.categoria, fontSize = 12.sp, color = Color.Gray)
                                    // Disponibilidad con PNG
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Image(
                                            painter = painterResource(
                                                id = if (prod.disponible) R.drawable.ic_check else R.drawable.ic_close
                                            ),
                                            contentDescription = null,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(Modifier.width(3.dp))
                                        Text(
                                            if (prod.disponible) "Disponible" else "No disponible",
                                            fontSize = 11.sp,
                                            color = if (prod.disponible) Color(0xFF2E7D32) else Color.Red
                                        )
                                    }
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Box(
                                        modifier = Modifier.background(bgColor, RoundedCornerShape(8.dp))
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text("${prod.stock} uds", color = textColor, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                    Spacer(Modifier.height(4.dp))
                                    // Etiqueta de stock con PNG
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Image(
                                            painter = painterResource(id = iconRes),
                                            contentDescription = null,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(Modifier.width(3.dp))
                                        Text(labelTexto, fontSize = 10.sp, color = textColor)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Data class auxiliar para 4 valores
data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@Composable
fun StockChip(iconRes: Int, label: String, cantidad: Int, bgColor: Color, textColor: Color) {
    Box(
        modifier = Modifier
            .background(bgColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(14.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text("$label: $cantidad", color = textColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}