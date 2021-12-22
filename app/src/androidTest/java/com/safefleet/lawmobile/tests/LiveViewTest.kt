package com.safefleet.lawmobile.tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.lawmobile.presentation.ui.login.x1.LoginX1Activity
import com.safefleet.lawmobile.helpers.DeviceUtils
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
class LiveViewTest : EspressoStartActivityBaseTest<LoginX1Activity>(LoginX1Activity::class.java) {

    private val liveViewScreen = LiveViewScreen()
    private val device = DeviceUtils()
    private val mainMenuScreen = MainMenuScreen()

    @Before
    fun login() = LoginScreen().login()

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-389
     */
    @Test
    fun verifyLiveViewIsDisplayed() {
        liveViewScreen.isLiveViewDisplayed()
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-391
     */
    @Test
    fun verifyVideoInFullScreen() {
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

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-423
     */
    @Test
    fun verifyLiveViewToggleOnDisconnection() {
        with(liveViewScreen) {
            mockUtils.disconnectCamera()

            switchLiveViewToggle()
            isDisconnectionAlertDisplayed()
        }
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-1767
     */
    @Test
    fun verifyMainMenuIsNotDisplayedOnX1() {
        with(mainMenuScreen) {
            isMenuButtonNotContained()
        }
    }
}
