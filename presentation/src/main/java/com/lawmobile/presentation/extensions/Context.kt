package com.lawmobile.presentation.extensions

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.lawmobile.presentation.R
import com.lawmobile.presentation.entities.AlertInformation
import com.lawmobile.presentation.entities.NeutralAlertInformation
import com.lawmobile.presentation.ui.base.BaseActivity
import com.lawmobile.presentation.ui.login.LoginActivity
import com.lawmobile.presentation.utils.CameraHelper
import com.lawmobile.presentation.widgets.CustomFilterDialog
import com.safefleet.mobile.commons.widgets.SafeFleetConfirmationDialog

fun Context.createAlertInformation(alertInformation: AlertInformation) {
    val builder = AlertDialog.Builder(this)
    var message = ""
    if (alertInformation.message != null) {
        message = getString(alertInformation.message)
    } else {
        if (alertInformation.customMessage != null) {
            message = alertInformation.customMessage
        }
    }

    builder.apply {
        setCancelable(false)
        setTitle(getString(alertInformation.title))
        setMessage(message)
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
    SafeFleetConfirmationDialog(
        this,
        true,
        getString(R.string.logout),
        getString(R.string.logout_message)
    ).apply {
        onResponseClicked = {
            if (it) {
                startActivity(Intent(context, LoginActivity::class.java))
                dismiss()
                activity.logout()
            }
        }
        show()
    }
}

fun Context.createAlertDialogMetadataExit() {
    val activity = this as BaseActivity
    SafeFleetConfirmationDialog(
        this,
        true,
        getString(R.string.unsaved_changes),
        getString(R.string.unsaved_changes_message)
    ).apply {
        onResponseClicked = {
            if (it) {
                activity.finish()
            }
        }
        show()
    }
}

fun Context.createAlertMobileDataActive(neutralAlertInformation: NeutralAlertInformation): AlertDialog {
    val builder = AlertDialog.Builder(this)
    builder.apply {
        neutralAlertInformation.run {
            setTitle(title)
            setMessage(message)
            buttonText?.let {
                setNeutralButton(getString(it)) { dialog, _ ->
                    onClickNeutralButton?.invoke(dialog)
                }
            }
        }
        setCancelable(false)
    }
    return builder.create()
}

fun Context.showToast(message: String, duration: Int) {
    Toast.makeText(this, message, duration).show()
}

fun Context.checkSession(callback: (View) -> Unit, view: View) {
    val isSessionExpired = checkIfSessionIsExpired()
    if (isSessionExpired) {
        this.createAlertSessionExpired()
    } else if (!CameraHelper.getInstance().checkWithAlertIfTheCameraIsConnected()) {
        this.createAlertErrorConnection()
    } else {
        callback.invoke(view)
    }
}

fun Context.isAnimationsEnabled() =
    Settings.System.getFloat(
        contentResolver,
        Settings.Global.TRANSITION_ANIMATION_SCALE,
        0F
    ) != 0F
            && Settings.System.getFloat(
        contentResolver,
        Settings.Global.WINDOW_ANIMATION_SCALE,
        0F
    ) != 0F
            && Settings.System.getFloat(
        contentResolver,
        Settings.Global.ANIMATOR_DURATION_SCALE,
        0F
    ) != 0F

fun Context.showDatePickerDialog(textView: TextView) {
    val current = System.currentTimeMillis().convertMilliSecondsToDate().split("-")
    val pickerDialog = DatePickerDialog(
        this,
        { _, year, month, dayOfMonth ->
            val date = fixDate(year, month + 1, dayOfMonth)
            textView.text = date
            showTimePickerDialog(textView)
        },
        current[0].toInt(), current[1].toInt() - 1, current[2].toInt()
    )
    pickerDialog.show()
}

fun Context.showTimePickerDialog(textView: TextView) {
    val current = System.currentTimeMillis().convertMilliSecondsToHour().split(":")
    val pickerDialog = TimePickerDialog(
        this,
        { _, hour, minute ->
            val time = textView.text.toString() + " " + fixTime(hour, minute)
            textView.text = time
        },
        current[0].toInt(), current[1].toInt(), true
    )
    pickerDialog.show()
}

fun Context.createFilterDialog(): CustomFilterDialog {
    return CustomFilterDialog(this, true).apply { show() }
}

private fun fixDate(year: Int, _month: Int, _day: Int): String {
    var month = _month.toString()
    var day = _day.toString()
    if (_month < 10) {
        month = "0$month"
    }
    if (_day < 10) {
        day = "0$day"
    }
    return "$year-$month-$day"
}

private fun fixTime(_hour: Int, _minute: Int): String {
    var minute = _minute.toString()
    var hour = _hour.toString()
    if (_minute < 10) {
        minute = "0$minute"
    }
    if (_hour < 10) {
        hour = "0$hour"
    }
    return "$hour:$minute"
}