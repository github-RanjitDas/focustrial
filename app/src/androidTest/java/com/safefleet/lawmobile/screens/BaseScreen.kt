package com.safefleet.lawmobile.screens

import com.safefleet.lawmobile.helpers.Alert
import com.safefleet.lawmobile.helpers.ToastMessage


open class BaseScreen {

    private val alert = Alert()
    protected val toastMessage = ToastMessage()

    fun isDisconnectionAlertDisplayed() {
        alert.isDisconnectionAlertDisplayed()
    }

}
