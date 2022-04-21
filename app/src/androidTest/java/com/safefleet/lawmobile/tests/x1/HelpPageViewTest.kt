package com.safefleet.lawmobile.tests.x1

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.lawmobile.domain.enums.CameraType
import com.lawmobile.presentation.ui.login.x1.LoginX1Activity
import com.safefleet.lawmobile.screens.HelpPageScreen
import com.safefleet.lawmobile.screens.LiveViewScreen
import com.safefleet.lawmobile.screens.LoginScreen
import com.safefleet.lawmobile.tests.EspressoStartActivityBaseTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class HelpPageViewTest :
    EspressoStartActivityBaseTest<LoginX1Activity>(LoginX1Activity::class.java) {

    companion object {
        private val liveViewScreen = LiveViewScreen()
        private val helpPageScreen = HelpPageScreen()
    }

    @Before
    fun setUp() {
        mockUtils.setCameraType(CameraType.X1)
        LoginScreen().login()
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-845
     */
    @Test
    fun openUserGuideFromLiveView() {
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
        liveViewScreen.openHelpPage()
        mockUtils.disconnectCamera()
        with(helpPageScreen) {
            goBack()
            isDisconnectionAlertDisplayed()
        }
    }
}
