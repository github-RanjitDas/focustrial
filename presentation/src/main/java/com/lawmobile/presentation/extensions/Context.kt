package com.lawmobile.presentation.extensions

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.lawmobile.presentation.R
import com.lawmobile.presentation.entities.AlertInformation
import com.lawmobile.presentation.ui.base.BaseActivity

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

fun Context.createAlertErrorConnection() {
    val title = R.string.the_camera_was_disconnected
    val message = R.string.the_camera_was_disconnected_description
    val alertInformation = AlertInformation(title, message, null, null)

    this.createAlertInformation(alertInformation)
}

fun Context.createAlertSessionExpired() {
    val activity = this as BaseActivity
    val alertInformation =
        AlertInformation(
            R.string.connection_finished, R.string.connection_finished_description,
            { activity.restartApp() }, null
        )
    this.createAlertInformation(alertInformation)
}

fun Context.createAlertConfirmAppExit() {
    val activity = this as BaseActivity
    val alertInformation =
        AlertInformation(
            R.string.confirm_app_exit_title, R.string.confirm_app_exit_description,
            { activity.killApp() }, {})
    this.createAlertInformation(alertInformation)
}

fun Context.showToast(message: String, duration: Int) {
    Toast.makeText(this, message, duration).show()
}