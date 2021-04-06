package com.safefleet.lawmobile.screens

import com.safefleet.lawmobile.R
import com.safefleet.lawmobile.helpers.Alert
import com.safefleet.lawmobile.helpers.CustomAssertionActions.waitUntil
import com.safefleet.lawmobile.testData.TestLoginData
import com.schibsted.spain.barista.assertion.BaristaImageViewAssertions.assertHasDrawable
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertContains
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertNotDisplayed
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn
import com.schibsted.spain.barista.interaction.BaristaEditTextInteractions.writeTo

class LoginScreen : BaseScreen() {

    fun isLogoDisplayed() = assertDisplayed(R.id.imageViewFMALogoNoAnimation)

    fun isConnectToCameraTextDisplayed() =
        assertDisplayed(R.id.textViewConnectToCamera, R.string.waiting_for_camera)

    fun isPasswordTextDisplayed() =
        assertContains(R.id.textViewPassword, R.string.welcome_officer)

    fun typePassword(officerPassword: String = TestLoginData.OFFICER_PASSWORD.value) =
        writeTo(R.id.editTextOfficerPassword, officerPassword)

    fun clickOnGo() = clickOn(R.id.buttonGo)

    fun clickOnLogin() = clickOn(R.id.buttonLogin)

    fun clickOnCameraInstructions() = clickOn(R.id.buttonInstructionsToLinkCamera)

    fun isInstructionsButtonDisplayed() =
        assertDisplayed(R.id.buttonInstructionsToLinkCamera, R.string.instructions_to_link_camera)

    fun isInstructionPopUpDisplayed() {
        assertContains(R.id.textViewInstructionsTitle, R.string.instructions_to_link_camera)
        assertDisplayed(R.id.buttonInstructionsToLinkCamera, R.string.instructions_to_link_camera)
        assertHasDrawable(R.id.imageViewWifiInstructions, R.drawable.ic_wifi_camera)
        assertContains(R.id.buttonDismissInstructions, R.string.got_it)
    }

    fun clickOnGotIt() = clickOn(R.id.buttonDismissInstructions)

    fun clickOnCloseInstructions() = clickOn(R.id.buttonCloseInstructions)

    fun isPairingSuccessDisplayed() {
        assertHasDrawable(R.id.imageViewResultPairing, R.drawable.ic_successful_green)
        assertContains(R.id.textViewResultPairing, R.string.success_connection_to_camera)
    }

    fun isIncorrectPasswordToastDisplayed() {
        assertDisplayed(R.string.incorrect_password)
    }

    fun isFooterLogoDisplayed() {
        assertHasDrawable(R.id.imageViewSafeFleetFooterLogo, R.drawable.ic_logo_safefleet)
    }

    fun isPairingErrorDisplayed() {
        assertHasDrawable(R.id.imageViewResultPairing, R.drawable.ic_error_big)
        assertDisplayed(R.id.textViewResultPairing, R.string.error_connection_to_camera)
    }

    fun retryPairing() {
        clickOn(R.id.buttonRetry)
    }

    fun login() {
        try {
            clickOnGo()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        isLoginScreenDisplayed()
        typePassword()
        clickOnLogin()
    }

    fun isWifiOffAlertDisplayed() = Alert.isWifiOffAlertDisplayed()

    fun isInstructionsPopUpNotDisplayed() {
        waitUntil { assertNotDisplayed(R.id.textViewInstructionsTitle) }
        assertNotDisplayed(R.string.instruction_1)
        assertNotDisplayed(R.string.instruction_2)
        assertNotDisplayed(R.string.instruction_3)
        assertNotDisplayed(R.string.instruction_4)
        assertNotDisplayed(R.string.instruction_5)
        assertNotDisplayed(R.string.instruction_6)
        assertNotDisplayed(R.id.imageViewWifiInstructions)
        assertNotDisplayed(R.id.buttonDismissInstructions)
    }

    fun isGoButtonDisplayed() = assertDisplayed(R.id.buttonGo)

    fun isPairingScreenDisplayed() {
        isLogoDisplayed()
        isConnectToCameraTextDisplayed()
        isGoButtonDisplayed()
        isInstructionsButtonDisplayed()
        isFooterLogoDisplayed()
    }

    fun isLoginScreenDisplayed() {
        waitUntil { assertDisplayed(R.id.buttonLogin) }
        isLogoDisplayed()
        isPasswordTextDisplayed()
        isFooterLogoDisplayed()
    }
}
