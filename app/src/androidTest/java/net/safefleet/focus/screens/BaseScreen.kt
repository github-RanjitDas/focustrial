package net.safefleet.focus.screens

import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn
import net.safefleet.focus.R
import net.safefleet.focus.helpers.Alert
import net.safefleet.focus.helpers.CustomAssertionActions.waitUntil

open class BaseScreen {

    fun clickOnCancel() = clickOn(R.string.cancel)

    fun clickOnAccept() = clickOn(R.string.accept)

    fun clickOnBack() = waitUntil { clickOn(R.id.imageButtonBackArrow) }

    fun isDisconnectionAlertDisplayed() {
        waitUntil { Alert.isDisconnectionAlertDisplayed() }
    }

    fun isDisconnectionDueInactivityAlertDisplayed() {
        waitUntil { Alert.isDisconnectionDueInactivityAlertDisplayed() }
    }

    fun isAcceptOptionDisplayed() = assertDisplayed(R.string.accept)

    fun isCancelOptionDisplayed() = assertDisplayed(R.string.cancel)
}
