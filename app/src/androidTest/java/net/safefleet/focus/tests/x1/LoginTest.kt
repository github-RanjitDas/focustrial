package net.safefleet.focus.tests.x1

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.lawmobile.domain.enums.CameraType
import com.lawmobile.presentation.ui.login.x2.LoginX2Activity
import com.schibsted.spain.barista.rule.flaky.AllowFlaky
import net.safefleet.focus.screens.LiveViewScreen
import net.safefleet.focus.screens.LoginScreen
import net.safefleet.focus.testData.TestLoginData
import net.safefleet.focus.tests.EspressoStartActivityBaseTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class LoginTest :
    EspressoStartActivityBaseTest<LoginX2Activity>(LoginX2Activity::class.java) {

    companion object {
        val OFFICER_PASSWORD = TestLoginData.OFFICER_PASSWORD.value
        const val INVALID_OFFICER_PASSWORD = "950887928"

        val loginScreen = LoginScreen()
        val liveViewScreen = LiveViewScreen()
    }

    @Before
    fun setUp() {
        mockUtils.setCameraType(CameraType.X2)
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
    @AllowFlaky(attempts = 1)
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
