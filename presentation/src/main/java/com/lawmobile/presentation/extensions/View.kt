package com.lawmobile.presentation.extensions

import android.view.View
import android.widget.CompoundButton
import android.widget.Switch
import com.lawmobile.presentation.R
import com.lawmobile.presentation.entity.AlertInformation
import com.lawmobile.presentation.utils.CameraHelper

fun View.setOnClickListenerCheckConnection(callback: (View) -> Unit) {
    this.setOnClickListener {
        if (!CameraHelper.getInstance().checkWithAlertIfTheCameraIsConnected()) {
            createAlertErrorConnection()
        } else {
            callback.invoke(it)
        }
    }
}

fun View.createAlertErrorConnection() {
    val title = R.string.the_camera_was_disconnected
    val message = R.string.the_camera_was_disconnected_description
    val alertInformation = AlertInformation(title, message, null, null)

    this.context.createAlertInformation(alertInformation)
}

fun Switch.setOnCheckedChangeListenerCheckConnection(callback: (buttonView: CompoundButton, isChecked: Boolean) -> Unit) {
    this.setOnCheckedChangeListener { buttonView, isChecked ->
        if (!CameraHelper.getInstance().checkWithAlertIfTheCameraIsConnected()) {
            createAlertErrorConnection()
        } else {
            callback.invoke(buttonView, isChecked)
        }
    }
}