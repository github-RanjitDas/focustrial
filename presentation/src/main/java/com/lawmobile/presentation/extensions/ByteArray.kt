package com.lawmobile.presentation.extensions

import android.content.Context
import java.io.File

fun ByteArray.getPathFromTemporalFile(context: Context, name: String): String {
    val temporalDir = context.cacheDir
    val file = File(name)
    val outputFile = File.createTempFile(file.nameWithoutExtension, "." + file.extension, temporalDir)
    file.delete()
    outputFile.writeBytes(this)
    return outputFile.absolutePath
}
