package net.safefleet.focus.tests.x1

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.lawmobile.domain.enums.CameraType
import com.lawmobile.presentation.ui.login.x2.LoginX2Activity
import com.lawmobile.presentation.utils.checkIfSessionIsExpired
import com.schibsted.spain.barista.rule.flaky.AllowFlaky
import io.mockk.every
import io.mockk.mockkStatic
import net.safefleet.focus.helpers.DeviceUtils
import net.safefleet.focus.helpers.SmokeTest
import net.safefleet.focus.screens.LiveViewScreen
import net.safefleet.focus.screens.LoginScreen
import net.safefleet.focus.screens.MainMenuScreen
import net.safefleet.focus.tests.EspressoStartActivityBaseTest
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@LargeTest
@RunWith(AndroidJUnit4::class)
class LiveViewTest :
    EspressoStartActivityBaseTest<LoginX2Activity>(LoginX2Activity::class.java) {

    private val liveViewScreen = LiveViewScreen()
    private val device = DeviceUtils()
    private val mainMenuScreen = MainMenuScreen()

    @Before
    fun setUp() {
        mockUtils.setCameraType(CameraType.X2)
        LoginScreen().login()
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-389
     */
    @SmokeTest
    @Test
    fun verifyLiveViewIsDisplayed() {
        liveViewScreen.isLiveViewDisplayed()
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-391
     */
    @SmokeTest
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
    @SmokeTest
    @Test
    fun verifyLiveViewToggleOnDisconnection() {
        with(liveViewScreen) {
            mockUtils.disconnectCamera()

            switchLiveViewToggle()
            isDisconnectionAlertDisplayed()
        }
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-652
     */
    @SmokeTest
    @Test
    @AllowFlaky(attempts = 2)
    fun verifyDisconnectionDueInactivity() {
        with(liveViewScreen) {
            mockkStatic("com.lawmobile.presentation.utils.SessionExpired")
            every { checkIfSessionIsExpired() } returns true

            switchLiveViewToggle()
            isDisconnectionDueInactivityAlertDisplayed()
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
