package com.safefleet.lawmobile.screens

import com.safefleet.lawmobile.R
import com.safefleet.lawmobile.helpers.Alert
import com.safefleet.lawmobile.helpers.CustomAssertionActions.retry
import com.safefleet.lawmobile.helpers.CustomAssertionActions.waitUntil
import com.safefleet.lawmobile.testData.TestLoginData
import com.schibsted.spain.barista.assertion.BaristaImageViewAssertions.assertHasDrawable
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertContains
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertNotDisplayed
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn
import com.schibsted.spain.barista.interaction.BaristaEditTextInteractions.writeTo

open class LoginScreen : BaseScreen() {

    fun typePassword(officerPassword: String = TestLoginData.OFFICER_PASSWORD.value) =
        writeTo(R.id.editTextOfficerPassword, officerPassword)

    fun clickOnGo() = clickOn(R.id.buttonGo)

    fun clickOnLogin() = waitUntil { clickOn(R.id.buttonLogin) }

    fun clickOnCameraInstructions() = clickOn(R.id.buttonInstructionsToLinkCamera)

    fun clickOnGotIt() = waitUntil { clickOn(R.id.buttonDismissInstructions) }

    fun clickOnCloseInstructions() = clickOn(R.id.buttonCloseInstructions)

    fun retryPairing() {
        clickOn(R.id.buttonRetry)
    }

    open fun login(officerPassword: String = TestLoginData.OFFICER_PASSWORD.value) {
        try {
            clickOnGo()
            retryLogin()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        isLoginScreenDisplayed()
        typePassword(officerPassword)
        clickOnLogin()
    }

    fun retryLogin(timeout: Long = 1000) {
        val isTryAgainDisplayed = isTryAgainDisplayed(timeout)
        if (isTryAgainDisplayed) retry { retryPairing() }
    }

    private fun isTryAgainDisplayed(timeout: Long): Boolean {
        try {
            waitUntil(timeout) { assertDisplayed(R.string.try_again_button_pairing) }
        } catch (e: java.lang.Exception) {
            return false
        }
        return true
    }

    private fun isLogoDisplayed() = assertDisplayed(R.id.imageViewFMALogoNoAnimation)

    private fun isConnectToCameraTextDisplayed() =
        assertDisplayed(R.id.textViewConnectToCamera, R.string.waiting_for_camera)

    private fun isPasswordTextDisplayed() =
        assertContains(R.id.textViewPassword, R.string.welcome_officer)

    private fun isInstructionsButtonDisplayed() =
        assertDisplayed(R.id.buttonInstructionsToLinkCamera, R.string.instructions_to_link_camera)

    fun isInstructionPopUpDisplayed() {
        assertContains(R.id.textViewInstructionsTitle, R.string.instructions_to_link_camera)
        assertDisplayed(R.id.buttonInstructionsToLinkCamera, R.string.instructions_to_link_camera)
        assertHasDrawable(R.id.imageViewWifiInstructions, R.drawable.ic_wifi_camera)
        assertContains(R.id.buttonDismissInstructions, R.string.got_it)
    }

    fun isPairingSuccessDisplayed() {
        waitUntil { assertHasDrawable(R.id.imageViewResultPairing, R.drawable.ic_successful_green) }
        waitUntil {
            assertContains(
                R.id.textViewResultPairing,
                R.string.success_connection_to_camera
            )
        }
    }

    fun isIncorrectPasswordToastDisplayed() {
        assertDisplayed(R.string.incorrect_password)
    }

    private fun isFooterLogoDisplayed() {
        assertHasDrawable(R.id.imageViewSafeFleetFooterLogo, R.drawable.ic_logo_safefleet)
    }

    fun isPairingErrorDisplayed() {
        assertHasDrawable(R.id.imageViewResultPairing, R.drawable.ic_error_big)
        assertDisplayed(R.id.textViewResultPairing, R.string.error_connection_to_camera)
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

    private fun isGoButtonDisplayed() = assertDisplayed(R.id.buttonGo)

    fun isPairingScreenDisplayed() {
        waitUntil { isLogoDisplayed() }
        isConnectToCameraTextDisplayed()
        isGoButtonDisplayed()
        isInstructionsButtonDisplayed()
        isFooterLogoDisplayed()
    }

    fun isLoginScreenDisplayed() {
        waitUntil { assertDisplayed(R.id.buttonLogin) }
        waitUntil { isLogoDisplayed() }
        waitUntil { isPasswordTextDisplayed() }
        waitUntil { isFooterLogoDisplayed() }
    }
}
