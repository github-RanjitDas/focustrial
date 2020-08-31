package com.safefleet.lawmobile.screens

import com.safefleet.lawmobile.R
import com.safefleet.lawmobile.testData.TestLoginData
import com.schibsted.spain.barista.assertion.BaristaImageViewAssertions.assertHasDrawable
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertContains
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn
import com.schibsted.spain.barista.interaction.BaristaEditTextInteractions.writeTo
import com.schibsted.spain.barista.interaction.BaristaSleepInteractions

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

    fun isInstructionsTitleDisplayed() =
        assertContains(R.id.textViewInstructionsTitle, R.string.instructions_to_link_camera)

    fun isInstructionsTextDisplayed() =
        assertDisplayed(R.id.buttonInstructionsToLinkCamera, R.string.instructions_to_link_camera)

    fun isInstructionsImageDisplayed() =
        assertHasDrawable(R.id.imageViewWifiInstructions, R.drawable.ic_wifi_camera)

    fun isInstructionsGotItButtonDisplayed() =
        assertContains(R.id.buttonDismissInstructions, R.string.got_it)

    fun clickOnGotIt() = clickOn(R.id.buttonDismissInstructions)

    fun isResultPairingSuccessImageDisplayed() =
        assertHasDrawable(R.id.imageViewResultPairing, R.drawable.ic_successful_green)

    fun isResultPairingSuccessTextDisplayed() =
        assertContains(R.id.textViewResultPairing, R.string.success_connection_to_camera)

    fun isIncorrectPasswordToastDisplayed() {
        assertDisplayed(R.string.incorrect_password)
    }

    fun login() {
        try {
            clickOnGo()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        BaristaSleepInteractions.sleep(1000)
        typePassword()
        clickOnLogin()
    }

}
