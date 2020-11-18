package com.safefleet.lawmobile.screens

import com.safefleet.lawmobile.R
import com.safefleet.lawmobile.testData.TestLoginData
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertContains
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn
import com.schibsted.spain.barista.interaction.BaristaEditTextInteractions.writeTo

class LoginScreen : BaseScreen() {

    fun isLogoDisplayed(): LoginScreen {
        assertDisplayed(R.id.imageViewLogo)
        return this
    }

    fun isInstructionsTextDisplayed(): LoginScreen {
        assertDisplayed(R.id.textInstructionsToLinkCamera, R.string.instructions_to_link_camera)
        return this
    }

    fun isWaitingForCameraTextDisplayed(): LoginScreen {
        assertDisplayed(R.id.textWaitingForCamera, R.string.waiting_for_camera)
        return this
    }

    fun isExitDisplayed(): LoginScreen {
        assertDisplayed(R.id.textViewLoginExit, R.string.exit)
        return this
    }

    fun isConnectingToCameraTextDisplayed() =
        assertDisplayed(R.id.textConnectingToCamera, R.string.connecting_to_camera)

    fun isWelcomeTextDisplayed(): LoginScreen {
        assertContains(R.id.textViewTitleOfficer, R.string.welcome_officer)
        return this
    }

    fun isOfficerNameDisplayed(officerName: String) =
        assertContains(R.id.textViewOfficerName, officerName)

    fun typePassword(officerPassword: String = TestLoginData.OFFICER_PASSWORD.value): LoginScreen {
        writeTo(R.id.textInputEditTextValidatePasswordOfficer, officerPassword)
        return this
    }

    fun go() = clickOn(R.id.imageButtonGo)

    fun isIncorrectPasswordToastDisplayed() {
        toastMessage.isToastDisplayed(R.string.incorrect_password)
        toastMessage.waitUntilToastDisappears(R.string.incorrect_password)
    }

    fun isIncorrectSerialNumberToastDisplayed() {
        toastMessage.isToastDisplayed(R.string.the_application_did_not_find_camera)
        toastMessage.waitUntilToastDisappears(R.string.the_application_did_not_find_camera)
    }

    fun login() {
        try {
            this.go()
        } catch (e: Exception) {

        }
        this.typePassword().go()
    }

}
