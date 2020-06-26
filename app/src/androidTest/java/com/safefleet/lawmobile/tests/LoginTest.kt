package com.safefleet.lawmobile.tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.lawmobile.presentation.ui.login.LoginActivity
import com.safefleet.lawmobile.helpers.DeviceUtils
import com.safefleet.lawmobile.screens.LiveViewScreen
import com.safefleet.lawmobile.screens.LoginScreen
import com.safefleet.lawmobile.testData.TestLoginData
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class LoginTest : EspressoBaseTest<LoginActivity>(LoginActivity::class.java) {
    // This class tests FMA-248 User story
    companion object {
        val SERIAL_NUMBER = TestLoginData.SERIAL_NUMBER.value
        val OFFICER_NAME = TestLoginData.OFFICER_NAME.value
        val OFFICER_PASSWORD = TestLoginData.OFFICER_PASSWORD.value
        val INVALID_SERIAL_NUMBER = TestLoginData.INVALID_SERIAL_NUMBER.value

        const val INVALID_OFFICER_PASSWORD = "950887928"

        val loginScreen = LoginScreen()
        val liveViewScreen = LiveViewScreen()
        val deviceUtils = DeviceUtils()
    }

    @Test
    fun verifyAppLogin_FMA_261_289() {
        loginScreen
            .isLogoDisplayed()
            .isWaitingForCameraTextDisplayed()
            .isInstructionsTextDisplayed()
            .isExitDisplayed()

        loginScreen.typeSerialNumber(SERIAL_NUMBER).go()

        loginScreen
            .isWelcomeTextDisplayed()
            .isOfficerNameDisplayed(OFFICER_NAME)

        loginScreen.typePassword(OFFICER_PASSWORD).go()
        liveViewScreen.isLiveViewDisplayed()
    }

    @Test
    fun verifyInvalidPasswordLogin_FMA_288() {
        loginScreen.typeSerialNumber(SERIAL_NUMBER).go()

        loginScreen.isWelcomeTextDisplayed()

        loginScreen.typePassword(INVALID_OFFICER_PASSWORD).go()

        loginScreen.isIncorrectPasswordToastDisplayed()
        liveViewScreen.isLiveViewNotDisplayed()
    }

    @Test
    fun verifyEmptyPasswordLogin_FMA_288() {
        loginScreen.typeSerialNumber(SERIAL_NUMBER).go()

        loginScreen.isWelcomeTextDisplayed().go()

        loginScreen.isIncorrectPasswordToastDisplayed()
        liveViewScreen.isLiveViewNotDisplayed()
    }

    @Test
    fun verifyIncorrectSerialNumber_FMA_287() {
        loginScreen.typeSerialNumber(INVALID_SERIAL_NUMBER).go()

        loginScreen.isConnectingToCameraTextDisplayed()
        loginScreen.isIncorrectSerialNumberToastDisplayed()
        loginScreen.isWaitingForCameraTextDisplayed()
    }

    @Test
    fun verifyPairingForTheSecondTime_FMA_286() {
        loginScreen.typeSerialNumber(SERIAL_NUMBER).go()
        loginScreen.typePassword(OFFICER_PASSWORD).go()
        liveViewScreen.isLiveViewDisplayed()

        deviceUtils.restartApp()

        loginScreen
            .isWelcomeTextDisplayed()
            .isOfficerNameDisplayed(OFFICER_NAME)

        loginScreen.typePassword(OFFICER_PASSWORD).go()
        liveViewScreen.isLiveViewDisplayed()
    }

    @Test
    fun verifyLoginDisconnectionScenario_FMA_292() {
        mockUtils.disconnectCamera()

        loginScreen.isWaitingForCameraTextDisplayed()
        loginScreen.typeSerialNumber(SERIAL_NUMBER).go()
        loginScreen.typePassword(OFFICER_PASSWORD).go()

        loginScreen.isDisconnectionAlertDisplayed()

        mockUtils.restoreCameraConnection()
    }
}
