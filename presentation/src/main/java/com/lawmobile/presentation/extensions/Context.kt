package com.lawmobile.presentation.extensions

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.lawmobile.presentation.R
import com.lawmobile.presentation.entity.AlertInformation

fun Context.createAlertInformation(alertInformation: AlertInformation) {
    val builder = AlertDialog.Builder(this)
    builder.apply {
        setCancelable(false)
        setTitle(getString(alertInformation.title))
        setMessage(getString(alertInformation.message))
        if (alertInformation.onClickPositiveButton != null) {
            setPositiveButton(R.string.OK) { dialog, _ ->
                alertInformation.onClickPositiveButton.invoke(dialog)
            }
        }
        if (alertInformation.onClickNegativeButton != null) {
            setNegativeButton(R.string.cancel) { dialog, _ ->
                alertInformation.onClickNegativeButton.invoke(dialog)
            }
        }
        show()
    }
}

fun Context.showToast(message: String, duration: Int) {
    Toast.makeText(this, message, duration).show()
}