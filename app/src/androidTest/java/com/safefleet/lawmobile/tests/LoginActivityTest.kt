package com.safefleet.lawmobile.tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.lawmobile.presentation.ui.login.LoginActivity
import com.safefleet.lawmobile.TestData
import com.safefleet.lawmobile.screens.LiveViewScreen
import com.safefleet.lawmobile.screens.LoginScreen
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class LoginActivityTest : EspressoBaseTest<LoginActivity>(LoginActivity::class.java) {
    // This class tests FMA-248 User story
    companion object {
        val SERIAL_NUMBER = TestData.SERIAL_NUMBER.value
        val OFFICER_NAME = TestData.OFFICER_NAME.value
        val OFFICER_PASSWORD = TestData.OFFICER_PASSWORD.value
        val INVALID_SERIAL_NUMBER = TestData.INVALID_SERIAL_NUMBER.value

        const val INVALID_OFFICER_PASSWORD = "950887928"

        val loginScreen = LoginScreen()
        val liveViewScreen = LiveViewScreen()
    }

    @Test
    fun verify_app_login_FMA_261() {
        loginScreen
            .isLogoDisplayed()
            .isWaitingForCameraTextDisplayed()
            .isInstructionsTextDisplayed()
            .isConnectionStatusDisplayed()
            .isExitDisplayed()

        loginScreen.typeSerialNumber(SERIAL_NUMBER).go()

        loginScreen
            .isWelcomeTextDisplayed()
            .isOfficerNameDisplayed(OFFICER_NAME)

        loginScreen.typePassword(OFFICER_PASSWORD).go()
        liveViewScreen.isLiveViewDisplayed()
    }

    @Test
    fun verify_invalid_password_login_FMA_288() {
        loginScreen.typeSerialNumber(SERIAL_NUMBER).go()

        loginScreen.isWelcomeTextDisplayed()

        loginScreen.typePassword(INVALID_OFFICER_PASSWORD).go()

        loginScreen.isIncorrectPasswordToastDisplayed()
        liveViewScreen.isLiveViewNotDisplayed()
    }

    @Test
    fun verify_empty_password_login_FMA_288() {
        loginScreen.typeSerialNumber(SERIAL_NUMBER).go()

        loginScreen.isWelcomeTextDisplayed().go()

        loginScreen.isIncorrectPasswordToastDisplayed()
        liveViewScreen.isLiveViewNotDisplayed()
    }

    @Test
    fun verify_incorrect_serial_number_FMA_287() {
        loginScreen.typeSerialNumber(INVALID_SERIAL_NUMBER).go()

        loginScreen.isConnectingToCameraTextDisplayed()
        loginScreen.isIncorrectSerialNumberToastDisplayed()
        loginScreen.isWaitingForCameraTextDisplayed()
    }
}
