package com.lawmobile.presentation.extensions

fun String.ifIsNotEmptyLet(callback: (String) -> Unit) {
    if (isNotEmpty()) callback.invoke(this)
}