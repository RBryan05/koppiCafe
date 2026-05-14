package com.grupo5.cafeteriaapp.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

fun guardarImagenInterna(context: Context, uri: Uri): String {
    val inputStream = context.contentResolver.openInputStream(uri) ?: return ""
    val archivo = File(context.filesDir, "img_${UUID.randomUUID()}.jpg")
    val outputStream = FileOutputStream(archivo)
    inputStream.copyTo(outputStream)
    inputStream.close()
    outputStream.close()
    return archivo.absolutePath
}