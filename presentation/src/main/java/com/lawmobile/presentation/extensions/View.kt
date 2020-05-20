package com.lawmobile.presentation.extensions

import android.view.View
import android.widget.CompoundButton
import android.widget.Switch
import com.lawmobile.presentation.ui.base.BaseActivity
import com.lawmobile.presentation.utils.CameraHelper

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