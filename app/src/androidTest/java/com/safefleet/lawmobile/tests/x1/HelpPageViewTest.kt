package com.safefleet.lawmobile.tests.x1

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.lawmobile.domain.enums.CameraType
import com.lawmobile.presentation.ui.login.x2.LoginX2Activity
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
    EspressoStartActivityBaseTest<LoginX2Activity>(LoginX2Activity::class.java) {

    companion object {
        private val liveViewScreen = LiveViewScreen()
        private val helpPageScreen = HelpPageScreen()
    }

    @Before
    fun setUp() {
        mockUtils.setCameraType(CameraType.X2)
        LoginScreen().login()
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-845
     */
    @Test
    fun openUserGuideFromLiveView() {
        with(liveViewScreen) {
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
            helpPageScreen.goBack()
            isRecordingInProgress()
        }
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-849
     */
    @Test
    fun userGuideDisconnectionX1() {
        mockUtils.disconnectCamera()
        with(helpPageScreen) {
            goBack()
            isDisconnectionAlertDisplayed()
        }
    }
}
