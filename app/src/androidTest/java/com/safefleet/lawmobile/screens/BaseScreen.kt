package com.safefleet.lawmobile.screens

import com.safefleet.lawmobile.helpers.Alert


open class BaseScreen {

    private val alert = Alert()

    fun isDisconnectionAlertDisplayed() {
        alert.isDisconnectionAlertDisplayed()
    }

}
