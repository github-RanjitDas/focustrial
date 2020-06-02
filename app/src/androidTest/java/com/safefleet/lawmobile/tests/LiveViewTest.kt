package com.safefleet.lawmobile.tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.lawmobile.presentation.ui.login.LoginActivity
import com.safefleet.lawmobile.helpers.DeviceUtils
import com.safefleet.lawmobile.helpers.MockUtils
import com.safefleet.lawmobile.screens.LiveViewScreen
import com.safefleet.lawmobile.screens.LoginScreen
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class LiveViewTest : EspressoBaseTest<LoginActivity>(LoginActivity::class.java) {

    private val liveViewScreen = LiveViewScreen()
    private val mockUtils = MockUtils()
    private val device = DeviceUtils()

    @Before
    fun login() = LoginScreen().login()

    @Test
    fun verifyLiveViewIsDisplayed_FMA_389() {
        liveViewScreen.isLiveViewDisplayed()
    }

    @Test
    fun verifyVideoInFullScreen_FMA_391() {
        liveViewScreen.isLiveViewDisplayed()

        device.switchToLandscape()
        liveViewScreen.isLiveViewDisplayed()

        liveViewScreen.switchFullScreenMode()
        liveViewScreen.isVideoInFullScreen()

        liveViewScreen.switchFullScreenMode()
        liveViewScreen.isLiveViewDisplayed()

        device.switchToPortrait()
    }

    @Test
    fun verifyLiveViewToggleOnDisconnection_FMA_423() {
        mockUtils.disconnectCamera()

        liveViewScreen.switchLiveViewToggle()
        liveViewScreen.isDisconnectionAlertDisplayed()

        mockUtils.restoreCameraConnection()
    }
}
