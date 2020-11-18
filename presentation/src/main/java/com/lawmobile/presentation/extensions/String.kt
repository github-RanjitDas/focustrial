package com.lawmobile.presentation.extensions

import android.graphics.BitmapFactory

fun String.ifIsNotEmptyLet(callback: (String) -> Unit) {
    if (isNotEmpty()) callback.invoke(this)
}

fun String.imageHasCorrectFormat(): Boolean {
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    BitmapFactory.decodeFile(this, options)
    return options.outWidth != -1 && options.outHeight != -1
}