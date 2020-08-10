package com.lawmobile.presentation.extensions

import android.content.Context
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.lawmobile.presentation.R
import com.lawmobile.presentation.ui.base.BaseActivity
import com.lawmobile.presentation.utils.CameraHelper
import com.lawmobile.presentation.widgets.CustomRecordButton
import com.lawmobile.presentation.widgets.CustomSnapshotButton
import com.safefleet.mobile.commons.widgets.SafeFleetSwitch
import com.safefleet.mobile.commons.widgets.snackbar.SafeFleetSnackBar
import com.safefleet.mobile.commons.widgets.snackbar.SafeFleetSnackBarSettings

fun View.setOnClickListenerCheckConnection(callback: (View) -> Unit) {
    this.setOnClickListener {
        checkSession(context, callback, it)
    }
}

fun SafeFleetSwitch.setSwitchListenerCheckConnection(callback: (View) -> Unit) {
    onClicked = {
        checkSession(context, callback, it)
    }
}

fun CustomRecordButton.setCustomListenerCheckConnection(callback: (View) -> Unit) {
    onClicked = {
        checkSession(context, callback, it)
    }
}

fun CustomSnapshotButton.setCustomListenerCheckConnection(callback: (View) -> Unit) {
    onClicked = {
        checkSession(context, callback, it)
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

private fun checkSession(context: Context, callback: (View) -> Unit, view: View) {
    val activity = context as BaseActivity
    val isSessionExpired = activity.checkIfSessionIsExpired()
    if (isSessionExpired) {
        context.createAlertSessionExpired()
    } else if (!CameraHelper.getInstance().checkWithAlertIfTheCameraIsConnected()) {
        context.createAlertErrorConnection()
    } else {
        callback.invoke(view)
    }
}