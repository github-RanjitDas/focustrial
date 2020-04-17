package com.lawmobile.presentation.extensions

import com.google.android.material.textfield.TextInputLayout

fun TextInputLayout.text() = this.editText?.text.toString()