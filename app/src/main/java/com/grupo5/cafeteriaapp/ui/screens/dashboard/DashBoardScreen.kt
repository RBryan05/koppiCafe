package com.grupo5.cafeteriaapp.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.grupo5.cafeteriaapp.viewmodel.ProductoViewModel
import com.grupo5.cafeteriaapp.viewmodel.ThemeViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateProductos: () -> Unit,
    onNavigateInventario: () -> Unit,
    onNavigatePerfil: () -> Unit,
    onLogout: () -> Unit,
    productoViewModel: ProductoViewModel,
    themeViewModel: ThemeViewModel
) {
    val productos by productoViewModel.productos.collectAsState()
    LaunchedEffect(Unit) { productoViewModel.cargarProductos() }

    val totalProductos = productos.size
    val totalCategorias = productos.map { it.categoria }.distinct().size
    val totalStock = productos.sumOf { it.stock }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: "Usuario"
    val isDark by themeViewModel.isDarkMode.collectAsState()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.width(280.dp)) {
                // Header del drawer
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFF6D4C41), Color(0xFFD7A86E))
                            )
                        )
                        .padding(24.dp)
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(Color.White.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, null, tint = Color.White, modifier = Modifier.size(36.dp))
                        }
                        Spacer(Modifier.height(12.dp))
                        Text("☕ KoppiCafé", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(userEmail, color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                    }
                }

                Spacer(Modifier.height(8.dp))

                DrawerItem(Icons.Default.Home, "Inicio", Color(0xFF6D4C41)) {
                    scope.launch { drawerState.close() }
                }
                DrawerItem(Icons.Default.Coffee, "Productos", Color(0xFF6D4C41)) {
                    scope.launch { drawerState.close() }
                    onNavigateProductos()
                }
                DrawerItem(Icons.Default.Inventory, "Inventario", Color(0xFF2E7D32)) {
                    scope.launch { drawerState.close() }
                    onNavigateInventario()
                }
                DrawerItem(Icons.Default.Person, "Mi Perfil", Color(0xFF6A1B9A)) {
                    scope.launch { drawerState.close() }
                    onNavigatePerfil()
                }

                Divider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))

                // Toggle modo oscuro
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(40.dp)
                            .background(Color(0xFF37474F).copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                            null,
                            tint = if (isDark) Color(0xFFFFA000) else Color(0xFF37474F)
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    Text(
                        if (isDark) "Modo claro" else "Modo oscuro",
                        fontSize = 15.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = isDark,
                        onCheckedChange = { themeViewModel.toggleTheme() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF6D4C41),
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color.LightGray
                        )
                    )
                }

                Divider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))

                DrawerItem(Icons.Default.ExitToApp, "Cerrar sesión", Color(0xFFD32F2F)) {
                    scope.launch { drawerState.close() }
                    onLogout()
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("☕ KoppiCafé", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, null, tint = Color.White)
                        }
                    },
                    actions = {
                        IconButton(onClick = onLogout) {
                            Icon(Icons.Default.ExitToApp, null)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF6D4C41),
                        titleContentColor = Color.White,
                        actionIconContentColor = Color.White
                    )
                )
            }
        ) { padding ->
            Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                // Banner superior
                Box(
                    modifier = Modifier.fillMaxWidth().height(160.dp)
                        .background(Brush.verticalGradient(colors = listOf(Color(0xFF6D4C41), Color(0xFFD7A86E)))),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("☕", fontSize = 48.sp)
                        Spacer(Modifier.height(8.dp))
                        Text("¡Bienvenido a KoppiCafé!", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(Modifier.height(24.dp))

                Text("Módulos disponibles", fontSize = 16.sp, fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = 16.dp))

                Spacer(Modifier.height(12.dp))

                Column(modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    ModuloCard("Productos", "Gestionar menú y productos de la cafetería",
                        Icons.Default.Coffee, Color(0xFF6D4C41), onNavigateProductos)
                    ModuloCard("Inventario", "Ver stock y productos con bajo inventario",
                        Icons.Default.Inventory, Color(0xFF2E7D32), onNavigateInventario)
                    ModuloCard("Mi Perfil", "Ver cuenta y cambiar contraseña",
                        Icons.Default.Person, Color(0xFF6A1B9A), onNavigatePerfil)
                }

                Spacer(Modifier.height(24.dp))

                Text("Resumen", fontSize = 16.sp, fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = 16.dp))

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    EstadisticaCard("Productos", "$totalProductos", Color(0xFF6D4C41), Modifier.weight(1f))
                    EstadisticaCard("Categorías", "$totalCategorias", Color(0xFF6A1B9A), Modifier.weight(1f))
                    EstadisticaCard("En Stock", "$totalStock", Color(0xFF2E7D32), Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun DrawerItem(icono: ImageVector, titulo: String, color: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(40.dp).background(color.copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icono, null, tint = color, modifier = Modifier.size(22.dp))
        }
        Spacer(Modifier.width(16.dp))
        Text(titulo, fontSize = 15.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun ModuloCard(titulo: String, descripcion: String, icono: ImageVector, color: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Box(
                modifier = Modifier.size(56.dp).background(color.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icono, null, tint = color, modifier = Modifier.size(30.dp))
            }
            Column {
                Text(titulo, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(descripcion, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
        }
    }
}

@Composable
fun EstadisticaCard(titulo: String, valor: String, color: Color, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(12.dp), elevation = CardDefaults.cardElevation(3.dp),
        colors = CardDefaults.cardColors(containerColor = color)) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(valor, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(titulo, fontSize = 11.sp, color = Color.White.copy(alpha = 0.85f), textAlign = TextAlign.Center)
        }
    }
}