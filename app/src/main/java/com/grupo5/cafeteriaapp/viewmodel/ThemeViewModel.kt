package com.grupo5.cafeteriaapp.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel // Subclase de ViewModel que tiene acceso al Application context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// Usa AndroidViewModel en lugar de ViewModel para acceder al Application context
// necesario para leer/escribir SharedPreferences fuera de un Composable
class ThemeViewModel(application: Application) : AndroidViewModel(application) {

    // SharedPreferences para persistir la preferencia del tema entre sesiones
    private val prefs = application.getSharedPreferences("cafeteria_prefs", Context.MODE_PRIVATE)

    // Se inicializa con el valor guardado; false (modo claro) si nunca se ha cambiado
    private val _isDarkMode = MutableStateFlow(prefs.getBoolean("dark_mode", false))
    val isDarkMode: StateFlow<Boolean> = _isDarkMode

    // Invierte el tema actual y lo persiste en SharedPreferences
    fun toggleTheme() {
        val nuevo = !_isDarkMode.value
        _isDarkMode.value = nuevo
        prefs.edit().putBoolean("dark_mode", nuevo).apply() // apply() es asíncrono y no bloquea el hilo
    }
}