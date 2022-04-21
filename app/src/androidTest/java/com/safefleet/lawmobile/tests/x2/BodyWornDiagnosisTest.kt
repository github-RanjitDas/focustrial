package com.safefleet.lawmobile.tests.x2

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.lawmobile.domain.enums.CameraType
import com.lawmobile.presentation.ui.login.x2.LoginX2Activity
import com.safefleet.lawmobile.screens.BodyWornDiagnosisScreen
import com.safefleet.lawmobile.screens.LiveViewScreen
import com.safefleet.lawmobile.screens.LoginScreen
import com.safefleet.lawmobile.screens.MainMenuScreen
import com.safefleet.lawmobile.tests.EspressoStartActivityBaseTest
import com.safefleet.mobile.kotlin_commons.helpers.Result
import com.schibsted.spain.barista.rule.flaky.AllowFlaky
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class BodyWornDiagnosisTest :
    EspressoStartActivityBaseTest<LoginX2Activity>(LoginX2Activity::class.java) {

    private val mainMenuScreen = MainMenuScreen()
    private val bodyWornDiagnosisScreen = BodyWornDiagnosisScreen()
    private val liveViewScreen = LiveViewScreen()

    @Before
    fun setUp() {
        mockUtils.setCameraType(CameraType.X2)
        LoginScreen().loginWithoutSSO()
        mainMenuScreen.clickOnMainMenu()
        mainMenuScreen.clickOnViewDiagnose()
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-1955
     */
    @Test
    @AllowFlaky(attempts = 1)
    fun verifySuccessfulResponseBodyWornDiagnosis() {
        with(bodyWornDiagnosisScreen) {
            isStartButtonDisplayed()
            isCloseButtonDisplayed()
            containsBodyWornDiagnosisTitle()

            clickOnStartButton()

            isSuccessTitleDisplayed()
            isSuccessMessageBodyDisplayed()
            isOkButtonDisplayed()
        }
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-1956
     */
    @Test
    @AllowFlaky(attempts = 2)
    fun verifyFailResponseBodyWornDiagnosis() {
        mockUtils.setBodyWornDiagnosisResult(Result.Success(false))
        with(bodyWornDiagnosisScreen) {
            clickOnStartButton()

            isFailTitleDisplayed()
            isFailMessageBodyDisplayed()
            isOkButtonDisplayed()
        }
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-1957
     */
    @Test
    @AllowFlaky(attempts = 2)
    fun verifyErrorMessageWhenDiagnosisProcessFails() {
        mockUtils.setBodyWornDiagnosisResult(Result.Error(Exception("")))
        with(bodyWornDiagnosisScreen) {
            isStartButtonDisplayed()
            isCloseButtonDisplayed()
            containsBodyWornDiagnosisTitle()

            clickOnStartButton()

            isErrorBodyWornDiagnosisMessageDisplayed()
        }
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-1960
     */
    @Test
    @AllowFlaky(attempts = 2)
    fun verifyCloseBodyWornDiagnosisViewFromCloseButton() {
        with(bodyWornDiagnosisScreen) {
            isStartButtonDisplayed()
            isCloseButtonDisplayed()
            containsBodyWornDiagnosisTitle()

            clickOnCloseButton()
        }
        liveViewScreen.isLiveViewDisplayed()
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-1991
     */
    @Test
    @AllowFlaky(attempts = 1)
    fun verifyCloseBodyWornDiagnosisViewFromOkButton() {
        with(bodyWornDiagnosisScreen) {
            isStartButtonDisplayed()
            isCloseButtonDisplayed()
            containsBodyWornDiagnosisTitle()

            clickOnStartButton()

            isSuccessTitleDisplayed()
            isSuccessMessageBodyDisplayed()
            isOkButtonDisplayed()

            clickOnOkButton()
        }
        liveViewScreen.isLiveViewDisplayed()
    }
}
