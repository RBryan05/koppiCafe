package com.grupo5.cafeteriaapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.grupo5.cafeteriaapp.data.model.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await // Permite usar operaciones de Firestore como corrutinas

// Sealed class que representa los posibles estados de una operación CRUD
sealed class EstadoOperacion {
    object Idle : EstadoOperacion()       // Sin operación en curso
    object Loading : EstadoOperacion()    // Operación en progreso
    object Success : EstadoOperacion()    // Operación completada exitosamente
    data class Error(val mensaje: String) : EstadoOperacion() // Operación fallida con mensaje
}

class ProductoViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val coleccion = db.collection("productos") // Referencia a la colección en Firestore

    // Lista de productos expuesta como StateFlow inmutable para la UI
    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos

    // Estado actual de la última operación CRUD
    private val _estado = MutableStateFlow<EstadoOperacion>(EstadoOperacion.Idle)
    val estado: StateFlow<EstadoOperacion> = _estado

    // Obtiene todos los productos de Firestore y los mapea al modelo Producto
    // Usa copy(id = doc.id) porque Firestore no incluye el ID dentro del documento
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

    // Agrega un nuevo documento a Firestore; Firestore genera el ID automáticamente
    fun agregarProducto(producto: Producto) {
        viewModelScope.launch {
            _estado.value = EstadoOperacion.Loading
            try {
                coleccion.add(producto).await()
                _estado.value = EstadoOperacion.Success
                cargarProductos() // Recarga la lista para reflejar el nuevo producto
            } catch (e: Exception) {
                _estado.value = EstadoOperacion.Error("Error al agregar: ${e.message}")
            }
        }
    }

    // Sobreescribe el documento completo con set(); usa el ID existente del producto
    fun editarProducto(id: String, producto: Producto) {
        viewModelScope.launch {
            _estado.value = EstadoOperacion.Loading
            try {
                coleccion.document(id).set(producto).await()
                _estado.value = EstadoOperacion.Success
                cargarProductos() // Recarga para sincronizar cambios
            } catch (e: Exception) {
                _estado.value = EstadoOperacion.Error("Error al editar: ${e.message}")
            }
        }
    }

    // Elimina el documento por ID; no cambia el estado a Loading ya que es instantáneo
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

    // Resetea el estado a Idle; llamado tras navegar después de un Success o para limpiar errores
    fun resetEstado() { _estado.value = EstadoOperacion.Idle }
}