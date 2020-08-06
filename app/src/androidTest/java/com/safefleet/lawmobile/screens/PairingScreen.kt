package com.safefleet.lawmobile.screens

import com.safefleet.lawmobile.R
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn

class PairingScreen : BaseScreen() {

    fun isUserGuideDisplayed() {
        assertDisplayed(R.id.pdfView)
    }

    fun openHelpPage() = clickOn(R.id.buttonInstructionsToLinkCamera)
}