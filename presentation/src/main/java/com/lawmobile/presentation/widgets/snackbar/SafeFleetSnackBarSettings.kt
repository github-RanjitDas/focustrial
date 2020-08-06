package com.lawmobile.presentation.widgets.snackbar

import android.view.View

data class SafeFleetSnackBarSettings(
    val view: View,
    val message: String,
    val duration: Int,
    val icon: Int,
    val bg_color: Int
)