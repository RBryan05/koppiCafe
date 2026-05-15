package com.grupo5.cafeteriaapp.data.model

// Data class que representa un producto de la cafetería.
// Kotlin genera automáticamente equals(), hashCode(), toString() y copy().
// Todos los campos tienen valores por defecto para compatibilidad con Firestore (deserialización automática).
data class Producto(
    val id: String = "",          // ID único del producto (generado por Firestore)
    val nombre: String = "",      // Nombre visible en la UI
    val descripcion: String = "", // Descripción del producto
    val precio: Double = 0.0,     // Precio con decimales
    val stock: Int = 0,           // Cantidad disponible en inventario
    val categoria: String = "",   // Categoría para filtrar (ej: "Bebidas", "Snacks")
    val disponible: Boolean = true, // Si está visible/comprable; true por defecto
    val imagenUrl: String = ""    // URL para cargar la imagen (Glide/Coil)
)