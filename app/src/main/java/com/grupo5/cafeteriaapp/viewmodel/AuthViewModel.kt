package com.grupo5.cafeteriaapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await // Permite usar las tareas de Firebase como corrutinas

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    companion object {
        // Email hardcodeado del administrador; se compara al iniciar sesión para asignar el rol
        const val ADMIN_EMAIL = "renelemus@cafeteria.com"
    }

    // Propiedad calculada: true si hay un usuario autenticado actualmente
    val isLoggedIn: Boolean get() = auth.currentUser != null

    // StateFlow de rol admin; expuesto como StateFlow inmutable para la UI
    private val _isAdmin = MutableStateFlow(false)
    val isAdmin: StateFlow<Boolean> = _isAdmin

    // StateFlow de carga para mostrar indicadores en la UI durante operaciones de auth
    private val _authLoading = MutableStateFlow(false)
    val authLoading: StateFlow<Boolean> = _authLoading

    init {
        // Si la app se abre con sesión ya activa, determina el rol sin necesidad de login
        auth.currentUser?.let {
            _isAdmin.value = it.email == ADMIN_EMAIL
        }
    }

    // Inicia sesión con Firebase Auth; el callback devuelve éxito y mensaje de error si aplica
    fun login(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            _authLoading.value = true
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                _isAdmin.value = auth.currentUser?.email == ADMIN_EMAIL
                _authLoading.value = false
                onResult(true, null)
            } catch (e: Exception) {
                _authLoading.value = false
                onResult(false, e.message) // Pasa el mensaje de error de Firebase a la UI
            }
        }
    }

    // Registra un nuevo usuario; los usuarios registrados siempre son clientes, nunca admin
    fun registrar(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            _authLoading.value = true
            try {
                auth.createUserWithEmailAndPassword(email, password).await()
                _isAdmin.value = false
                _authLoading.value = false
                onResult(true, null)
            } catch (e: Exception) {
                _authLoading.value = false
                onResult(false, e.message)
            }
        }
    }

    // Cierra sesión y resetea el rol a false
    fun logout() {
        auth.signOut()
        _isAdmin.value = false
    }
}