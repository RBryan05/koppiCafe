package com.grupo5.cafeteriaapp.data.model

data class Producto(
    val id: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val precio: Double = 0.0,
    val stock: Int = 0,
    val categoria: String = "",
    val disponible: Boolean = true,
    val imagenUrl: String = ""
)