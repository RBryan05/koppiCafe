package com.grupo5.cafeteriaapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    companion object {
        const val ADMIN_EMAIL = "renelemus@cafeteria.com"
    }

    val isLoggedIn: Boolean get() = auth.currentUser != null

    private val _isAdmin = MutableStateFlow(false)
    val isAdmin: StateFlow<Boolean> = _isAdmin

    private val _authLoading = MutableStateFlow(false)
    val authLoading: StateFlow<Boolean> = _authLoading

    init {
        // Si ya hay sesión activa, determina el rol
        auth.currentUser?.let {
            _isAdmin.value = it.email == ADMIN_EMAIL
        }
    }

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
                onResult(false, e.message)
            }
        }
    }

    fun registrar(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            _authLoading.value = true
            try {
                auth.createUserWithEmailAndPassword(email, password).await()
                _isAdmin.value = false // los registrados siempre son clientes
                _authLoading.value = false
                onResult(true, null)
            } catch (e: Exception) {
                _authLoading.value = false
                onResult(false, e.message)
            }
        }
    }

    fun logout() {
        auth.signOut()
        _isAdmin.value = false
    }
}