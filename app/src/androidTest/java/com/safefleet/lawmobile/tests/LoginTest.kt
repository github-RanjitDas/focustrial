package com.safefleet.lawmobile.tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.lawmobile.presentation.ui.login.LoginActivity
import com.safefleet.lawmobile.screens.LiveViewScreen
import com.safefleet.lawmobile.screens.LoginScreen
import com.safefleet.lawmobile.testData.TestLoginData
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class LoginTest : EspressoStartActivityBaseTest<LoginActivity>(LoginActivity::class.java) {

    companion object {
        val OFFICER_PASSWORD = TestLoginData.OFFICER_PASSWORD.value
        const val INVALID_OFFICER_PASSWORD = "950887928"

        val loginScreen = LoginScreen()
        val liveViewScreen = LiveViewScreen()
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-1036
     * Test case: https://safefleet.atlassian.net/browse/FMA-1037
     */
    @Test
    fun verifyAppLogin() {
        with(loginScreen) {
            isPairingScreenDisplayed()

            clickOnGo()

            isPairingSuccessDisplayed()

            isLoginScreenDisplayed()

            typePassword(OFFICER_PASSWORD)
            clickOnLogin()

            liveViewScreen.isLiveViewDisplayed()
        }
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-1038
     */
    @Test
    fun verifyIncorrectLogin() {
        with(loginScreen) {
            clickOnGo()

            isPairingSuccessDisplayed()

            isLoginScreenDisplayed()
            clickOnLogin()

            isIncorrectPasswordToastDisplayed()
            liveViewScreen.isLiveViewNotDisplayed()

            typePassword(INVALID_OFFICER_PASSWORD)
            clickOnLogin()

            isIncorrectPasswordToastDisplayed()
            liveViewScreen.isLiveViewNotDisplayed()
        }
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-1039
     * Test case: https://safefleet.atlassian.net/browse/FMA-1041
     */
    @Test
    fun verifyPairingDisconnectionScenario() {
        with(loginScreen) {
            mockUtils.disconnectCamera()

            clickOnGo()
            isPairingErrorDisplayed()

            mockUtils.restoreCameraConnection()

            retryPairing()
            isPairingSuccessDisplayed()

            isLoginScreenDisplayed()

            mockUtils.disconnectCamera()

            typePassword(OFFICER_PASSWORD)
            clickOnLogin()

            isDisconnectionAlertDisplayed()
        }
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-1322
     */
    @Test
    fun verifyInstructionsToConnectCamera() {
        with(loginScreen) {
            clickOnCameraInstructions()

            isInstructionPopUpDisplayed()

            clickOnCloseInstructions()

            isPairingScreenDisplayed()

            clickOnCameraInstructions()

            isInstructionPopUpDisplayed()

            clickOnGotIt()

            isPairingScreenDisplayed()
            isInstructionsPopUpNotDisplayed()
        }
    }
}
