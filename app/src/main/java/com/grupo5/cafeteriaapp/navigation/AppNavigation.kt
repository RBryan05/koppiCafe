package com.grupo5.cafeteriaapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.grupo5.cafeteriaapp.ui.screens.auth.LoginScreen
import com.grupo5.cafeteriaapp.ui.screens.auth.RegisterScreen
import com.grupo5.cafeteriaapp.ui.screens.dashboard.DashboardScreen
import com.grupo5.cafeteriaapp.ui.screens.dashboard.InventarioScreen
import com.grupo5.cafeteriaapp.ui.screens.perfil.PerfilScreen
import com.grupo5.cafeteriaapp.ui.screens.producto.CrearProductoScreen
import com.grupo5.cafeteriaapp.ui.screens.producto.DetalleProductoScreen
import com.grupo5.cafeteriaapp.ui.screens.producto.EditarProductoScreen
import com.grupo5.cafeteriaapp.ui.screens.producto.ListaProductosScreen
import com.grupo5.cafeteriaapp.viewmodel.AuthViewModel
import com.grupo5.cafeteriaapp.viewmodel.ProductoViewModel
import com.grupo5.cafeteriaapp.viewmodel.ThemeViewModel

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val DASHBOARD = "dashboard"
    const val LISTA_PRODUCTOS = "lista_productos"
    const val CREAR_PRODUCTO = "crear_producto"
    const val EDITAR_PRODUCTO = "editar_producto/{id}"
    const val DETALLE_PRODUCTO = "detalle_producto/{id}"
    const val INVENTARIO = "inventario"
    const val PERFIL = "perfil"

    fun editarProducto(id: String) = "editar_producto/$id"
    fun detalleProducto(id: String) = "detalle_producto/$id"
}

@Composable
fun AppNavigation(authViewModel: AuthViewModel, themeViewModel: ThemeViewModel) {
    val navController = rememberNavController()
    val prodViewModel: ProductoViewModel = viewModel()
    val startDestination = if (authViewModel.isLoggedIn) Routes.DASHBOARD else Routes.LOGIN
    val isAdmin by authViewModel.isAdmin.collectAsState()

    NavHost(navController = navController, startDestination = startDestination) {

        composable(Routes.LOGIN) {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Routes.REGISTER)
                }
            )
        }

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

        // Solo admin puede llegar aquí
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

        composable(
            route = Routes.EDITAR_PRODUCTO,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStack ->
            val id = backStack.arguments?.getString("id") ?: ""
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
            InventarioScreen(
                viewModel = prodViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.PERFIL) {
            PerfilScreen(onBack = { navController.popBackStack() })
        }
    }
}