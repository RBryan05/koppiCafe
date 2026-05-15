package com.grupo5.cafeteriaapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
// Componentes principales de Navigation Compose
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
// Para declarar argumentos dinámicos en rutas (ej: {id})
import androidx.navigation.navArgument
// Todas las pantallas de la app
import com.grupo5.cafeteriaapp.ui.screens.auth.LoginScreen
import com.grupo5.cafeteriaapp.ui.screens.auth.RegisterScreen
import com.grupo5.cafeteriaapp.ui.screens.dashboard.DashboardScreen
import com.grupo5.cafeteriaapp.ui.screens.dashboard.InventarioScreen
import com.grupo5.cafeteriaapp.ui.screens.perfil.PerfilScreen
import com.grupo5.cafeteriaapp.ui.screens.producto.CrearProductoScreen
import com.grupo5.cafeteriaapp.ui.screens.producto.DetalleProductoScreen
import com.grupo5.cafeteriaapp.ui.screens.producto.EditarProductoScreen
import com.grupo5.cafeteriaapp.ui.screens.producto.ListaProductosScreen
// ViewModels necesarios
import com.grupo5.cafeteriaapp.viewmodel.AuthViewModel
import com.grupo5.cafeteriaapp.viewmodel.ProductoViewModel
import com.grupo5.cafeteriaapp.viewmodel.ThemeViewModel

// Objeto singleton que centraliza todas las rutas como constantes,
// evitando strings sueltos por toda la app y facilitando el mantenimiento.
object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val DASHBOARD = "dashboard"
    const val LISTA_PRODUCTOS = "lista_productos"
    const val CREAR_PRODUCTO = "crear_producto"
    const val EDITAR_PRODUCTO = "editar_producto/{id}"   // {id} es argumento dinámico
    const val DETALLE_PRODUCTO = "detalle_producto/{id}" // {id} es argumento dinámico
    const val INVENTARIO = "inventario"
    const val PERFIL = "perfil"

    // Helpers que construyen la ruta con el ID real para navegar
    fun editarProducto(id: String) = "editar_producto/$id"
    fun detalleProducto(id: String) = "detalle_producto/$id"
}

@Composable
fun AppNavigation(authViewModel: AuthViewModel, themeViewModel: ThemeViewModel) {
    val navController = rememberNavController()
    val prodViewModel: ProductoViewModel = viewModel() // ViewModel compartido entre pantallas

    // Decide la pantalla inicial según si el usuario ya tiene sesión activa
    val startDestination = if (authViewModel.isLoggedIn) Routes.DASHBOARD else Routes.LOGIN

    // Observa el rol de admin como StateFlow para reaccionar a cambios en tiempo real
    val isAdmin by authViewModel.isAdmin.collectAsState()

    NavHost(navController = navController, startDestination = startDestination) {

        // LOGIN: al entrar exitosamente limpia el backstack para que no se pueda regresar
        composable(Routes.LOGIN) {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate(Routes.REGISTER) }
            )
        }

        // REGISTER: al registrarse también limpia el backstack hasta LOGIN
        composable(Routes.REGISTER) {
            RegisterScreen(
                viewModel = authViewModel,
                onRegisterSuccess = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onBackToLogin = { navController.popBackStack() }
            )
        }

        // DASHBOARD: pantalla principal; el logout limpia todo el backstack
        composable(Routes.DASHBOARD) {
            DashboardScreen(
                onNavigateProductos = { navController.navigate(Routes.LISTA_PRODUCTOS) },
                onNavigateInventario = { navController.navigate(Routes.INVENTARIO) },
                onNavigatePerfil = { navController.navigate(Routes.PERFIL) },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.DASHBOARD) { inclusive = true }
                    }
                },
                productoViewModel = prodViewModel,
                themeViewModel = themeViewModel,
                isAdmin = isAdmin
            )
        }

        composable(Routes.LISTA_PRODUCTOS) {
            ListaProductosScreen(
                viewModel = prodViewModel,
                onAgregar = { navController.navigate(Routes.CREAR_PRODUCTO) },
                onDetalle = { id -> navController.navigate(Routes.detalleProducto(id)) },
                onBack = { navController.popBackStack() },
                isAdmin = isAdmin
            )
        }

        // CREAR PRODUCTO: protegido por rol; si no es admin regresa inmediatamente
        composable(Routes.CREAR_PRODUCTO) {
            if (isAdmin) {
                CrearProductoScreen(
                    viewModel = prodViewModel,
                    onSuccess = { navController.popBackStack() },
                    onBack = { navController.popBackStack() }
                )
            } else {
                navController.popBackStack()
            }
        }

        // EDITAR PRODUCTO: recibe el ID como argumento de navegación tipo String
        composable(
            route = Routes.EDITAR_PRODUCTO,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStack ->
            val id = backStack.arguments?.getString("id") ?: "" // "" como fallback si es null
            if (isAdmin) {
                EditarProductoScreen(
                    productoId = id,
                    viewModel = prodViewModel,
                    onSuccess = { navController.popBackStack() },
                    onBack = { navController.popBackStack() }
                )
            } else {
                navController.popBackStack()
            }
        }

        // DETALLE PRODUCTO: accesible para todos; editar/eliminar solo visible si isAdmin
        composable(
            route = Routes.DETALLE_PRODUCTO,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStack ->
            val id = backStack.arguments?.getString("id") ?: ""
            DetalleProductoScreen(
                productoId = id,
                viewModel = prodViewModel,
                onEditar = { navController.navigate(Routes.editarProducto(id)) },
                onEliminar = { navController.popBackStack() },
                onBack = { navController.popBackStack() },
                isAdmin = isAdmin
            )
        }

        composable(Routes.INVENTARIO) {
            InventarioScreen(viewModel = prodViewModel, onBack = { navController.popBackStack() })
        }

        composable(Routes.PERFIL) {
            PerfilScreen(onBack = { navController.popBackStack() })
        }
    }
}