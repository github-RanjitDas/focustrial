package com.safefleet.lawmobile.tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.lawmobile.presentation.ui.login.LoginActivity
import com.safefleet.lawmobile.screens.HelpPageScreen
import com.safefleet.lawmobile.screens.LiveViewScreen
import com.safefleet.lawmobile.screens.LoginScreen
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class HelpPageViewTest : EspressoStartActivityBaseTest<LoginActivity>(LoginActivity::class.java) {

    companion object {
        private val liveViewScreen = LiveViewScreen()
        private val helpPageScreen = HelpPageScreen()
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-845
     */
    @Test
    fun openUserGuideFromLiveView() {
        LoginScreen().login()
        with(liveViewScreen) {
            openHelpPage()
            helpPageScreen.isUserGuideDisplayed()
        }
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-847
     */
    @Test
    fun userGuideWhileRecording() {
        LoginScreen().login()
        with(liveViewScreen) {
            startRecording()
            openHelpPage()
            helpPageScreen.goBack()
            isRecordingInProgress()
        }
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-849
     */
    @Test
    fun userGuideDisconnectionX1() {
        LoginScreen().login()
        liveViewScreen.openHelpPage()

        mockUtils.disconnectCamera()

        helpPageScreen.goBack()
        helpPageScreen.isDisconnectionAlertDisplayed()
    }
}
