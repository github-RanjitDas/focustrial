package com.lawmobile.presentation.extensions

import android.content.Context
import android.content.Intent
import android.os.Process
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.lawmobile.domain.entities.CameraEvent
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.entities.customEvents.IncorrectPasswordErrorEvent
import com.lawmobile.domain.entities.customEvents.LimitOfLoginAttemptsErrorEvent
import com.lawmobile.domain.entities.customEvents.WrongCredentialsEvent
import com.lawmobile.domain.enums.CameraType
import com.lawmobile.presentation.R
import com.lawmobile.presentation.entities.AlertInformation
import com.lawmobile.presentation.ui.base.BaseActivity
import com.lawmobile.presentation.ui.login.LoginBaseActivity
import com.lawmobile.presentation.utils.CameraHelper
import com.lawmobile.presentation.utils.checkIfSessionIsExpired
import com.lawmobile.presentation.widgets.CustomNotificationDialog
import com.safefleet.mobile.safefleet_ui.widgets.SafeFleetConfirmationDialog
import kotlin.system.exitProcess

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

fun Context.createAlertInformation(alertInformation: AlertInformation) {
    val builder = AlertDialog.Builder(this)

    builder.apply {
        setCancelable(false)
        setTitle(getString(alertInformation.title))
        setMessage(alertInformation.message)
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

fun Context.createNotCancellableAlert(@StringRes title: Int, @StringRes message: Int): AlertDialog {
    val builder = AlertDialog.Builder(this)
    return builder.run {
        setTitle(getString(title))
        setMessage(getString(message))
        setCancelable(false)
        create()
    }
}

fun Context.createLowWifiSignalAlert(): AlertDialog =
    createNotCancellableAlert(
        title = R.string.low_signal_title,
        message = R.string.low_signal_message
    )

fun Context.createAlertErrorConnection() {
    val title = R.string.the_camera_was_disconnected
    val message = R.string.the_camera_was_disconnected_description
    val alertInformation = AlertInformation(
        title,
        message,
        { restartApp() },
        null
    )

    createAlertInformation(alertInformation)
}

fun Context.createAlertSessionExpired() {
    val alertInformation =
        AlertInformation(
            R.string.connection_finished, R.string.connection_finished_description,
            { restartApp() }, null
        )
    createAlertInformation(alertInformation)
    CameraHelper.getInstance().disconnectCamera()
}

fun Context.restartApp() {
    val intent: Intent? = this.packageManager
        .getLaunchIntentForPackage(this.packageName)
    intent?.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    startActivity(intent)
    Process.killProcess(Process.myPid())
    exitProcess(0)
}

fun Context.createAlertConfirmAppExit(callback: () -> Unit) {
    SafeFleetConfirmationDialog(
        this,
        true,
        getString(R.string.logout),
        getString(R.string.logout_message)
    ).apply {
        onResponseClicked = {
            if (it) {
                callback.invoke()
                dismiss()
            }
        }
        show()
    }
}

fun Context.createAlertDialogUnsavedChanges() {
    val activity = this as BaseActivity
    SafeFleetConfirmationDialog(
        this,
        true,
        getString(R.string.unsaved_changes),
        getString(R.string.unsaved_changes_message)
    ).apply {
        onResponseClicked = {
            if (it) {
                dismiss()
                activity.finish()
            }
        }
        show()
    }
}

fun Context.createMobileDataAlert(): AlertDialog =
    createNotCancellableAlert(
        title = R.string.mobile_data_status_title,
        message = R.string.mobile_data_status_message
    )

fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
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

fun Context.verifySessionBeforeAction(callback: () -> Unit) {
    val isSessionExpired = checkIfSessionIsExpired()
    if (isSessionExpired) {
        this.createAlertSessionExpired()
    } else if (!CameraHelper.getInstance().checkWithAlertIfTheCameraIsConnected()) {
        this.createAlertErrorConnection()
    } else {
        callback.invoke()
    }
}

fun Context.isAnimationsEnabled() =
    Settings.System.getFloat(
        contentResolver,
        Settings.Global.TRANSITION_ANIMATION_SCALE,
        0F
    ) != 0F &&
        Settings.System.getFloat(
        contentResolver,
        Settings.Global.WINDOW_ANIMATION_SCALE,
        0F
    ) != 0F &&
        Settings.System.getFloat(
        contentResolver,
        Settings.Global.ANIMATOR_DURATION_SCALE,
        0F
    ) != 0F

fun Context.createNotificationDialog(
    cameraEvent: CameraEvent,
    doNeedOkButtonListener: Boolean = false,
    builder: (CustomNotificationDialog.() -> Unit)? = null
) = CustomNotificationDialog(
    this,
    false,
    cameraEvent,
    doNeedOkButtonListener
).apply {
    show()
    builder?.invoke(this)
}

fun Context.getIntentForCameraType(
    activityForX1: Class<out BaseActivity>,
    activityForX2: Class<out BaseActivity>
): Intent {
    return when (CameraInfo.cameraType) {
        CameraType.X1 -> Intent(this, activityForX1)
        CameraType.X2 -> Intent(this, activityForX2)
    }
}

fun Context.showWrongCredentialsNotification() {
    val cameraEvent = WrongCredentialsEvent.event
    createNotificationDialog(cameraEvent)
        ?.setButtonText(resources.getString(R.string.OK))
}

fun Context.showIncorrectPasswordErrorNotification() {
    val cameraEvent = IncorrectPasswordErrorEvent.event
    createNotificationDialog(cameraEvent)
        .setButtonText(resources.getString(R.string.OK))
}

fun Context.showLimitOfLoginAttemptsErrorNotification(activity: LoginBaseActivity) {
    val cameraEvent = LimitOfLoginAttemptsErrorEvent.event
    createNotificationDialog(cameraEvent) {
        setButtonText(resources.getString(R.string.OK))
        onConfirmationClick = {
            activity.finishAffinity()
            exitProcess(0)
        }
    }
}
