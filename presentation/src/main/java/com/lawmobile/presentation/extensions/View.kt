package com.lawmobile.presentation.extensions

import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.FrameLayout
import android.widget.Switch
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.snackbar.Snackbar
import com.lawmobile.presentation.R
import com.lawmobile.presentation.ui.base.BaseActivity
import com.lawmobile.presentation.utils.CameraHelper
import com.lawmobile.presentation.widgets.snackbar.SafeFleetSnackBar
import com.lawmobile.presentation.widgets.snackbar.SafeFleetSnackBarSettings

fun View.setOnClickListenerCheckConnection(callback: (View) -> Unit) {
    this.setOnClickListener {
        val activity = context as BaseActivity
        val isSessionExpired = activity.checkIfSessionIsExpired()
        if (isSessionExpired) {
            context.createAlertSessionExpired()
        } else if (!CameraHelper.getInstance().checkWithAlertIfTheCameraIsConnected()) {
            context.createAlertErrorConnection()
        } else {
            callback.invoke(it)
        }
    }
}

fun Switch.setOnCheckedChangeListenerCheckConnection(callback: (buttonView: CompoundButton, isChecked: Boolean) -> Unit) {
    this.setOnCheckedChangeListener { buttonView, isChecked ->
        val activity = context as BaseActivity
        val isSessionExpired = activity.checkIfSessionIsExpired()
        if (isSessionExpired) {
            context.createAlertSessionExpired()
        } else if (!CameraHelper.getInstance().checkWithAlertIfTheCameraIsConnected()) {
            context.createAlertErrorConnection()
        } else {
            callback.invoke(buttonView, isChecked)
        }
    }
}

fun View?.findSuitableParent(): ViewGroup? {
    var view = this
    var fallback: ViewGroup? = null
    do {
        when (view) {
            is CoordinatorLayout -> return view
            is FrameLayout -> {
                if (view.id == android.R.id.content) {
                    return view
                } else {
                    fallback = view
                }
            }
        }

        if (view != null) {
            val parent = view.parent
            view = if (parent is View) parent else null
        }
    } while (view != null)

    return fallback
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