package com.safefleet.lawmobile.screens

import com.safefleet.lawmobile.R
import com.safefleet.lawmobile.helpers.Alert
import com.safefleet.lawmobile.helpers.ToastMessage
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn


open class BaseScreen {

    private val alert = Alert()
    protected val toastMessage = ToastMessage()

    fun isDisconnectionAlertDisplayed() {
        alert.isDisconnectionAlertDisplayed()
    }

    fun goBack() = clickOn(R.id.textViewFileListBack)

}
