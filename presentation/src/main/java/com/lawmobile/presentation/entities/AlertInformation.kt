package com.lawmobile.presentation.entities

import android.content.DialogInterface

class AlertInformation(
    val title: Int,
    val message: Int? = null,
    val onClickPositiveButton: ((DialogInterface) -> Unit)? = null,
    val onClickNegativeButton: ((DialogInterface) -> Unit)? = null,
    val customMessage: String? = null
)
