package com.grupo5.cafeteriaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels  // Delegado para obtener ViewModels con ciclo de vida de la Activity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.grupo5.cafeteriaapp.navigation.AppNavigation
import com.grupo5.cafeteriaapp.ui.theme.CafeteriaAppTheme
import com.grupo5.cafeteriaapp.viewmodel.AuthViewModel
import com.grupo5.cafeteriaapp.viewmodel.ThemeViewModel

class MainActivity : ComponentActivity() {

    // ViewModels con ciclo de vida atado a la Activity; sobreviven rotaciones de pantalla
    private val authViewModel: AuthViewModel by viewModels()
    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Observa el modo oscuro para recomponer el tema cuando cambie
            val isDark by themeViewModel.isDarkMode.collectAsState()

            // Aplica el tema global; todo el árbol de composables hereda esta paleta de colores
            CafeteriaAppTheme(darkTheme = isDark) {
                // Punto de entrada de la navegación; recibe los ViewModels compartidos
                AppNavigation(
                    authViewModel = authViewModel,
                    themeViewModel = themeViewModel
                )
            }
        }
    }
}