package com.lawmobile.presentation.extensions

import android.view.View
import com.lawmobile.presentation.R
import com.lawmobile.presentation.entity.AlertInformation
import com.lawmobile.presentation.utils.CameraHelper

fun View.setOnClickListenerCheckConnection(callback: (View) -> Unit) {
    this.setOnClickListener {
        if (!CameraHelper.getInstance().checkWithAlertIfTheCameraIsConnected()) {
            val title = R.string.the_camera_was_disconnected
            val message = R.string.the_camera_was_disconnected_description
            val alertInformation = AlertInformation(title, message, { dialogInterface ->
                dialogInterface.dismiss()
            }, false)
            this.context.createAlertInformation(alertInformation)
        } else {
            callback.invoke(it)
        }
    }
}