package com.safefleet.lawmobile.tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.lawmobile.presentation.ui.login.LoginActivity
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
class LoginTest : EspressoStartActivityBaseTest<LoginActivity>(LoginActivity::class.java) {
    // This class tests FMA-248 User story
    companion object {
        val OFFICER_PASSWORD = TestLoginData.OFFICER_PASSWORD.value

        const val INVALID_OFFICER_PASSWORD = "950887928"

        val loginScreen = LoginScreen()
        val liveViewScreen = LiveViewScreen()
    }

    @Test
    fun verifyAppLogin_FMA_1036_1037() {
        with(loginScreen) {
            isLogoDisplayed()
            isConnectToCameraTextDisplayed()
            isInstructionsButtonDisplayed()
            isFooterLogoDisplayed()

            clickOnGo()

            isPairingSuccessDisplayed()

            isLogoDisplayed()
            isPasswordTextDisplayed()
            isFooterLogoDisplayed()

            typePassword(OFFICER_PASSWORD)
            clickOnLogin()

            liveViewScreen.isLiveViewDisplayed()
        }
    }

    @Test
    fun verifyIncorrectLogin_FMA_1038() {
        with(loginScreen) {
            clickOnGo()

            isPairingSuccessDisplayed()

            isPasswordTextDisplayed()
            clickOnLogin()

            isIncorrectPasswordToastDisplayed()
            liveViewScreen.isLiveViewNotDisplayed()

            typePassword(INVALID_OFFICER_PASSWORD)
            clickOnLogin()

            isIncorrectPasswordToastDisplayed()
            liveViewScreen.isLiveViewNotDisplayed()
        }
    }

    @Test
    fun verifyPairingDisconnectionScenario_FMA_1039_1041() {
        with(loginScreen) {
            mockUtils.disconnectCamera()

            clickOnGo()
            isPairingErrorDisplayed()

            mockUtils.restoreCameraConnection()

            retryPairing()
            isPairingSuccessDisplayed()

            isPasswordTextDisplayed()

            mockUtils.disconnectCamera()

            typePassword(OFFICER_PASSWORD)
            clickOnLogin()

            isDisconnectionAlertDisplayed()
        }
    }

    @Test
    fun verifyInstructionsToConnectCamera_FMA_1322() {
        with(loginScreen) {
            clickOnCameraInstructions()

            isInstructionPopUpDisplayed()

            clickOnCloseInstructions()

            isLogoDisplayed()
            isConnectToCameraTextDisplayed()
            isInstructionsButtonDisplayed()
            isFooterLogoDisplayed()

            clickOnCameraInstructions()

            isInstructionPopUpDisplayed()

            clickOnGotIt()

            isLogoDisplayed()
            isConnectToCameraTextDisplayed()
            isInstructionsButtonDisplayed()
            isFooterLogoDisplayed()
            isInstructionsPopUpNotDisplayed()
        }
    }
}
