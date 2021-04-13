package com.safefleet.lawmobile.tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.lawmobile.presentation.ui.login.LoginActivity
import com.safefleet.lawmobile.helpers.DeviceUtils
import com.safefleet.lawmobile.helpers.SmokeTest
import com.safefleet.lawmobile.screens.LiveViewScreen
import com.safefleet.lawmobile.screens.LoginScreen
import com.safefleet.lawmobile.screens.MainMenuScreen
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@LargeTest
@RunWith(AndroidJUnit4::class)
class LiveViewTest : EspressoStartActivityBaseTest<LoginActivity>(LoginActivity::class.java) {

    private val liveViewScreen = LiveViewScreen()
    private val device = DeviceUtils()
    private val mainMenuScreen = MainMenuScreen()

    @Before
    fun login() = LoginScreen().login()

    @SmokeTest
    @Test
    fun verifyLiveViewIsDisplayed_FMA_389() {
        liveViewScreen.isLiveViewDisplayed()
    }

    @SmokeTest
    @Test
    fun verifyVideoInFullScreen_FMA_391() {
        with(liveViewScreen) {
            isLiveViewDisplayed()

            device.switchToLandscape()
            isLiveViewDisplayed()

            switchFullScreenMode()
            isVideoInFullScreen()

            switchFullScreenMode()
            isLiveViewDisplayed()

            device.switchToPortrait()
        }
    }

    @SmokeTest
    @Test
    fun verifyLiveViewToggleOnDisconnection_FMA_423() {
        with(liveViewScreen) {
            mockUtils.disconnectCamera()

            switchLiveViewToggle()
            isDisconnectionAlertDisplayed()
        }
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-1767
     */
    @SmokeTest
    @Test
    fun verifyMainMenuIsNotDisplayedOnX1() {
        with(mainMenuScreen) {
            isMenuButtonNotContained()
        }
    }
}
