package com.safefleet.lawmobile.screens

import com.safefleet.lawmobile.R
import com.safefleet.lawmobile.helpers.Alert
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn

open class BaseScreen {

    fun isDisconnectionAlertDisplayed() {
        Alert.isDisconnectionAlertDisplayed()
    }

    fun clickOnCancel() = clickOn(R.string.cancel)

    fun clickOnAccept() = clickOn(R.string.accept)

    fun isAcceptOptionDisplayed() = assertDisplayed(R.string.accept)

    fun isCancelOptionDisplayed() = assertDisplayed(R.string.cancel)
}
