package com.safefleet.lawmobile.tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.lawmobile.domain.enums.CameraType
import com.lawmobile.presentation.ui.login.LoginActivity
import com.safefleet.lawmobile.screens.BodyWornDiagnosisScreen
import com.safefleet.lawmobile.screens.LiveViewScreen
import com.safefleet.lawmobile.screens.LoginScreen
import com.safefleet.lawmobile.screens.MainMenuScreen
import com.safefleet.mobile.kotlin_commons.helpers.Result
import com.schibsted.spain.barista.rule.BaristaRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class BodyWornDiagnosisTest : EspressoBaseTest() {

    private val mainMenuScreen = MainMenuScreen()
    private val bodyWornDiagnosisScreen = BodyWornDiagnosisScreen()
    private val liveViewScreen = LiveViewScreen()

    @get:Rule
    var baristaRule = BaristaRule.create(LoginActivity::class.java)

    @Before
    fun setUp() {
        mockUtils.setCameraType(CameraType.X2)
        baristaRule.launchActivity()
        LoginScreen().login()
        mainMenuScreen.clickOnMainMenu()
        mainMenuScreen.clickOnViewDiagnose()
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-1955
     */
    @Test
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
    fun verifyFailResponseBodyWornDiagnosis() {
        mockUtils.setBodyWornDiagnosisResult(Result.Success(false))
        with(bodyWornDiagnosisScreen) {
            isStartButtonDisplayed()
            isCloseButtonDisplayed()
            containsBodyWornDiagnosisTitle()

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
