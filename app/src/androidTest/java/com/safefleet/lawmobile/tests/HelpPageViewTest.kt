package com.safefleet.lawmobile.tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.lawmobile.presentation.ui.login.LoginActivity
import com.safefleet.lawmobile.screens.HelpPageScreen
import com.safefleet.lawmobile.screens.LiveViewScreen
import com.safefleet.lawmobile.screens.LoginScreen
import com.safefleet.lawmobile.screens.PairingScreen
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class HelpPageViewTest : EspressoBaseTest<LoginActivity>(LoginActivity::class.java) {

    companion object {
        private val liveViewScreen = LiveViewScreen()
        private val pairingScreen = PairingScreen()
        private val helpPageScreen = HelpPageScreen()
    }

    @Test
    fun openUserGuideFromLiveView_FMA_845() {
        LoginScreen().login()
        with(liveViewScreen) {
            openHelpPage()
            isUserGuideDisplayed()
        }
    }

    @Test
    fun openUserGuideFromPairingScreen_FMA_846() {
        with(pairingScreen) {
            openHelpPage()
            isUserGuideDisplayed()
        }
    }

    @Test
    fun userGuideWhileRecording_FMA_847() {
        LoginScreen().login()
        with(liveViewScreen) {
            startRecording()
            openHelpPage()
            helpPageScreen.goBack()
            isRecordingInProgress()
        }
    }

    @Test
    fun userGuideDisconnectionX1_FMA_849() {
        LoginScreen().login()
        liveViewScreen.openHelpPage()

        mockUtils.disconnectCamera()

        helpPageScreen.goBack()
        helpPageScreen.isDisconnectionAlertDisplayed()
    }
}