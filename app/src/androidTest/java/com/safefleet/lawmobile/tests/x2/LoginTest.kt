package com.safefleet.lawmobile.tests.x2

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.lawmobile.domain.enums.CameraType
import com.lawmobile.presentation.ui.login.x2.LoginX2Activity
import com.safefleet.lawmobile.screens.LoginScreen
import com.safefleet.lawmobile.tests.EspressoBaseTest
import com.schibsted.spain.barista.rule.BaristaRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class LoginTest : EspressoBaseTest() {

    private val loginScreen = LoginScreen()

    @get:Rule
    var baristaRule = BaristaRule.create(LoginX2Activity::class.java)

    @Before
    fun setUp() {
        mockUtils.setCameraType(CameraType.X2)
        baristaRule.launchActivity()
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-2914
     */
    @Test
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
