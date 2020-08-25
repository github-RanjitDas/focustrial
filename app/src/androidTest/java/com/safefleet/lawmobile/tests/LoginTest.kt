package com.safefleet.lawmobile.tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.lawmobile.presentation.ui.login.LoginActivity
import com.safefleet.lawmobile.screens.LiveViewScreen
import com.safefleet.lawmobile.screens.LoginScreen
import com.safefleet.lawmobile.testData.TestLoginData
import com.schibsted.spain.barista.interaction.BaristaSleepInteractions
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
        val OFFICER_PASSWORD = TestLoginData.OFFICER_PASSWORD.value

        const val INVALID_OFFICER_PASSWORD = "950887928"

        val loginScreen = LoginScreen()
        val liveViewScreen = LiveViewScreen()
    }

    @Test
    fun b_verifyAppLogin_FMA_261_289() {
        with(loginScreen) {
            isLogoDisplayed()
            isConnectToCameraTextDisplayed()
            isInstructionsTextDisplayed()

            clickOnGo()

            isResultPairingSuccessImageDisplayed()
            isResultPairingSuccessTextDisplayed()

            BaristaSleepInteractions.sleep(1000)

            isPasswordTextDisplayed()

            typePassword(OFFICER_PASSWORD)
            clickOnLogin()

            liveViewScreen.isLiveViewDisplayed()
        }
    }

    @Test
    fun d_verifyInvalidPasswordLogin_FMA_288() {
        with(loginScreen) {
            clickOnGo()

            isResultPairingSuccessImageDisplayed()
            isResultPairingSuccessTextDisplayed()

            BaristaSleepInteractions.sleep(1000)

            isPasswordTextDisplayed()

            typePassword(INVALID_OFFICER_PASSWORD)
            clickOnLogin()

            isIncorrectPasswordToastDisplayed()
            liveViewScreen.isLiveViewNotDisplayed()
        }
    }

    @Test
    fun c_verifyEmptyPasswordLogin_FMA_288() {
        with(loginScreen) {
            clickOnGo()

            isResultPairingSuccessImageDisplayed()
            isResultPairingSuccessTextDisplayed()

            BaristaSleepInteractions.sleep(1000)

            isPasswordTextDisplayed()
            clickOnLogin()

            isIncorrectPasswordToastDisplayed()
            liveViewScreen.isLiveViewNotDisplayed()
        }
    }

    @Test
    fun a_verifyPairingForTheSecondTime_FMA_286() {
        with(loginScreen) {
            clickOnGo()

            isResultPairingSuccessImageDisplayed()
            isResultPairingSuccessTextDisplayed()

            BaristaSleepInteractions.sleep(1000)

            isPasswordTextDisplayed()
            typePassword(OFFICER_PASSWORD)
            clickOnLogin()

            liveViewScreen.isLiveViewDisplayed()

            liveViewScreen.logout()

            clickOnGo()

            isResultPairingSuccessImageDisplayed()
            isResultPairingSuccessTextDisplayed()

            BaristaSleepInteractions.sleep(1000)

            isPasswordTextDisplayed()
            typePassword(OFFICER_PASSWORD)
            clickOnLogin()

            liveViewScreen.isLiveViewDisplayed()
        }
    }

    @Test
    fun e_verifyLoginDisconnectionScenario_FMA_292() {
        with(loginScreen) {
            clickOnGo()

            isResultPairingSuccessImageDisplayed()
            isResultPairingSuccessTextDisplayed()

            BaristaSleepInteractions.sleep(1000)

            isPasswordTextDisplayed()

            mockUtils.disconnectCamera()

            typePassword(OFFICER_PASSWORD)
            clickOnLogin()

            isDisconnectionAlertDisplayed()
        }
    }

    /*@Test
    fun f_verifyPairingDisconnectionScenario_FMA_() {
        with(loginScreen) {
            mockUtils.disconnectCameraWifi()
            clickOnGo()

            //isVerifyConnectionToCameraWifiDisplayed()
        }
    }*/

    @Test
    fun g_verifyInstructionsToConnectCamera_FMA_() {
        with(loginScreen) {
            clickOnCameraInstructions()

            isInstructionsTitleDisplayed()
            isInstructionsImageDisplayed()
            isInstructionsTextDisplayed()
            isInstructionsGotItButtonDisplayed()

            clickOnGotIt()
        }
    }
}
