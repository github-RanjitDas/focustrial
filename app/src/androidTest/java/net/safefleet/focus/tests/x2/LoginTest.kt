package net.safefleet.focus.tests.x2

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.lawmobile.domain.enums.CameraType
import com.lawmobile.presentation.ui.login.x2.LoginX2Activity
import com.schibsted.spain.barista.rule.flaky.AllowFlaky
import net.safefleet.focus.screens.LoginScreen
import net.safefleet.focus.tests.EspressoStartActivityBaseTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class LoginTest :
    EspressoStartActivityBaseTest<LoginX2Activity>(LoginX2Activity::class.java) {

    private val loginScreen = LoginScreen()

    @Before
    fun setUp() {
        mockUtils.setCameraType(CameraType.X2)
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-2914
     */
    @Test
    @AllowFlaky(attempts = 1)
    fun verifyLoginSuccessWithoutSS0() {
        with(loginScreen) {
            areLoginLogosDisplayed()
            isOfficerIdLabelDisplayed()
            isChangeBodyCameraButtonDisplayed()
            isOnBoardingCardsTextDisplayed()

            typeOfficerId()
            clickOnContinue()

            isEditButtonDisplayed()
            isDevicePasswordDisplayed()
            isInstructionsToLinkCameraButtonDisplayed()

            typeDevicePassword()
            clickOnConnect()
            isBodyCameraConnectedSuccessfully()
        }
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-2914
     */
    @Test
    @AllowFlaky(attempts = 1)
    fun verifyLoginFailWithoutSS0() {
        with(loginScreen) {
            areLoginLogosDisplayed()
            isOfficerIdLabelDisplayed()
            isChangeBodyCameraButtonDisplayed()
            isOnBoardingCardsTextDisplayed()

            typeOfficerId()
            clickOnContinue()

            isEditButtonDisplayed()
            isDevicePasswordDisplayed()
            isInstructionsToLinkCameraButtonDisplayed()

            typeDevicePassword()
            mockUtils.setIncorrectNetwork()
            clickOnConnect()
            isCredentialsErrorDisplayed()
            clickOnOkButton()
            isDevicePasswordDisplayed()
        }
    }
}
