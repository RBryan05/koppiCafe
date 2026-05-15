package com.grupo5.cafeteriaapp.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.util.UUID // Para generar nombres de archivo únicos y evitar colisiones

// Copia una imagen seleccionada por el usuario (Uri) al almacenamiento interno de la app
// y devuelve la ruta absoluta del archivo guardado, o "" si ocurre un error al abrirla.
fun guardarImagenInterna(context: Context, uri: Uri): String {
    // Abre el stream de lectura de la imagen; retorna "" si no se puede acceder
    val inputStream = context.contentResolver.openInputStream(uri) ?: return ""

    // Crea el archivo destino en filesDir (privado a la app, no requiere permisos)
    val archivo = File(context.filesDir, "img_${UUID.randomUUID()}.jpg")

    val outputStream = FileOutputStream(archivo)
    inputStream.copyTo(outputStream) // Copia los bytes de la imagen al archivo destino
    inputStream.close()
    outputStream.close()

    return archivo.absolutePath // Ruta usada luego para cargar la imagen con rememberAsyncImagePainter
}