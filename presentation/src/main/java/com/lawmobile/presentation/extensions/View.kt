package com.lawmobile.presentation.extensions

import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.lawmobile.presentation.R
import com.lawmobile.presentation.ui.base.BaseActivity
import com.lawmobile.presentation.widgets.CustomRecordButton
import com.lawmobile.presentation.widgets.CustomSnapshotButton
import com.safefleet.mobile.commons.widgets.SafeFleetSwitch
import com.safefleet.mobile.commons.widgets.snackbar.SafeFleetSnackBar
import com.safefleet.mobile.commons.widgets.snackbar.SafeFleetSnackBarSettings
import java.sql.Timestamp

fun View.setOnClickListenerCheckConnection(callback: (View) -> Unit) {
    this.setOnClickListener {
        context.checkSession( callback, it)
    }
}

fun SafeFleetSwitch.setSwitchListenerCheckConnection(callback: (View) -> Unit) {
    onClicked = {
        context.checkSession(callback, it)
    }
}

fun CustomRecordButton.setCustomListenerCheckConnection(callback: (View) -> Unit) {
    onClicked = {
        context.checkSession( callback, it)
    }
}

fun CustomSnapshotButton.setCustomListenerCheckConnection(callback: (View) -> Unit) {
    onClicked = {
        context.checkSession( callback, it)
    }
}

fun View.showErrorSnackBar(message: String) {
    SafeFleetSnackBar.make(
        SafeFleetSnackBarSettings(
            this,
            message,
            Snackbar.LENGTH_SHORT,
            R.drawable.ic_warning,
            this.context.getColor(R.color.red)
        )
    )?.show()
}

fun View.showSuccessSnackBar(message: String) {
    SafeFleetSnackBar.make(
        SafeFleetSnackBarSettings(
            this,
            message,
            Snackbar.LENGTH_SHORT,
            R.drawable.ic_successful_white,
            this.context.getColor(R.color.greenSuccess)
        )
    )?.show()
}

fun checkIfSessionIsExpired(): Boolean {
    val timeNow = Timestamp(System.currentTimeMillis())
    return (timeNow.time - BaseActivity.lastInteraction.time) > BaseActivity.MAX_TIME_SESSION
}