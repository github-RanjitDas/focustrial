package com.safefleet.lawmobile.tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.lawmobile.presentation.ui.login.LoginActivity
import com.safefleet.lawmobile.helpers.DeviceUtils
import com.safefleet.lawmobile.screens.LiveViewScreen
import com.safefleet.lawmobile.screens.LoginScreen
import com.safefleet.lawmobile.testData.TestLoginData
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@LargeTest
@RunWith(AndroidJUnit4::class)
class LoginTest : EspressoBaseTest<LoginActivity>(LoginActivity::class.java) {
    // This class tests FMA-248 User story
    companion object {
        val OFFICER_NAME = TestLoginData.OFFICER_NAME.value
        val OFFICER_PASSWORD = TestLoginData.OFFICER_PASSWORD.value

        const val INVALID_OFFICER_PASSWORD = "950887928"

        val loginScreen = LoginScreen()
        val liveViewScreen = LiveViewScreen()
        val deviceUtils = DeviceUtils()
    }

    @Test
    fun b_verifyAppLogin_FMA_261_289() {
        with(loginScreen) {
            isLogoDisplayed()
            isWaitingForCameraTextDisplayed()
            isInstructionsTextDisplayed()

            go()

            isWelcomeTextDisplayed()

            typePassword(OFFICER_PASSWORD)
            go()

            liveViewScreen.isLiveViewDisplayed()
        }
    }

    @Test
    fun d_verifyInvalidPasswordLogin_FMA_288() {
        with(loginScreen) {
            go()

            isWelcomeTextDisplayed()

            typePassword(INVALID_OFFICER_PASSWORD)
            go()

            isIncorrectPasswordToastDisplayed()
            liveViewScreen.isLiveViewNotDisplayed()
        }
    }

    @Test
    fun c_verifyEmptyPasswordLogin_FMA_288() {
        with(loginScreen) {
            go()

            isWelcomeTextDisplayed()
            go()

            isIncorrectPasswordToastDisplayed()
            liveViewScreen.isLiveViewNotDisplayed()
        }
    }

    @Test
    fun a_verifyPairingForTheSecondTime_FMA_286() {
        with(loginScreen) {
            go()
            typePassword(OFFICER_PASSWORD)
            go()
            liveViewScreen.isLiveViewDisplayed()

            deviceUtils.restartApp()

            go()
            isWelcomeTextDisplayed()

            typePassword(OFFICER_PASSWORD)
            go()

            liveViewScreen.isLiveViewDisplayed()
        }
    }

    @Test
    fun e_verifyLoginDisconnectionScenario_FMA_292() {
        with(loginScreen) {
            mockUtils.disconnectCamera()

            isWaitingForCameraTextDisplayed()
            go()
            typePassword(OFFICER_PASSWORD)
            go()

            isDisconnectionAlertDisplayed()
        }
    }
}
