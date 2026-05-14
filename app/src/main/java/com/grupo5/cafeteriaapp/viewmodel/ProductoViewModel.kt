package com.grupo5.cafeteriaapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.grupo5.cafeteriaapp.data.model.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class EstadoOperacion {
    object Idle : EstadoOperacion()
    object Loading : EstadoOperacion()
    object Success : EstadoOperacion()
    data class Error(val mensaje: String) : EstadoOperacion()
}

class ProductoViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val coleccion = db.collection("productos")

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos

    private val _estado = MutableStateFlow<EstadoOperacion>(EstadoOperacion.Idle)
    val estado: StateFlow<EstadoOperacion> = _estado

    fun cargarProductos() {
        viewModelScope.launch {
            try {
                val snapshot = coleccion.get().await()
                _productos.value = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Producto::class.java)?.copy(id = doc.id)
                }
            } catch (e: Exception) {
                _estado.value = EstadoOperacion.Error("Error al cargar: ${e.message}")
            }
        }
    }

    fun agregarProducto(producto: Producto) {
        viewModelScope.launch {
            _estado.value = EstadoOperacion.Loading
            try {
                coleccion.add(producto).await()
                _estado.value = EstadoOperacion.Success
                cargarProductos()
            } catch (e: Exception) {
                _estado.value = EstadoOperacion.Error("Error al agregar: ${e.message}")
            }
        }
    }

    fun editarProducto(id: String, producto: Producto) {
        viewModelScope.launch {
            _estado.value = EstadoOperacion.Loading
            try {
                coleccion.document(id).set(producto).await()
                _estado.value = EstadoOperacion.Success
                cargarProductos()
            } catch (e: Exception) {
                _estado.value = EstadoOperacion.Error("Error al editar: ${e.message}")
            }
        }
    }

    fun eliminarProducto(id: String) {
        viewModelScope.launch {
            try {
                coleccion.document(id).delete().await()
                cargarProductos()
            } catch (e: Exception) {
                _estado.value = EstadoOperacion.Error("Error al eliminar: ${e.message}")
            }
        }
    }

    fun resetEstado() { _estado.value = EstadoOperacion.Idle }
}