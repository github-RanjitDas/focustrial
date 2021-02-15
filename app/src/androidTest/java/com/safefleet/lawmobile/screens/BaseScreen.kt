package com.safefleet.lawmobile.screens

import com.safefleet.lawmobile.R
import com.safefleet.lawmobile.helpers.Alert
import com.schibsted.spain.barista.interaction.BaristaClickInteractions

open class BaseScreen {

    fun isDisconnectionAlertDisplayed() {
        Alert.isDisconnectionAlertDisplayed()
    }

    fun clickOnCancel() = BaristaClickInteractions.clickOn(R.string.cancel)
}
