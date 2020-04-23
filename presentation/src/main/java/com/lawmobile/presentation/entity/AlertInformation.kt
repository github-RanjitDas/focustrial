package com.lawmobile.presentation.entity

import android.content.DialogInterface

class AlertInformation(
    val title: Int,
    val message: Int,
    val onClickPositiveButton: (Unit) -> Unit,
    val isNegativeButtonEnable: Boolean = true,
    val onClickNegativeButton: ((DialogInterface) -> Unit)? = null
)