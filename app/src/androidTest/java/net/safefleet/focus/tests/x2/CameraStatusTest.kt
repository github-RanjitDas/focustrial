package net.safefleet.focus.tests.x2

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.lawmobile.domain.enums.CameraType
import com.lawmobile.presentation.ui.login.x2.LoginX2Activity
import com.schibsted.spain.barista.rule.flaky.AllowFlaky
import net.safefleet.focus.screens.LiveViewScreen
import net.safefleet.focus.screens.LoginScreen
import net.safefleet.focus.tests.EspressoStartActivityBaseTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class CameraStatusTest :
    EspressoStartActivityBaseTest<LoginX2Activity>(LoginX2Activity::class.java) {

    private val liveViewScreen = LiveViewScreen()
    private val loginScreen = LoginScreen()

    @Before
    fun setUp() {
        mockUtils.setCameraType(CameraType.X2)
        loginScreen.loginWithoutSSO()
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-1780
     */
    @Test
    @AllowFlaky(attempts = 1)
    fun verifyBatteryIndicator() {
        mockUtils.setBatteryProgressCameraX2(100)
        liveViewScreen.isBatteryIndicatorTextDisplayed("100")
        liveViewScreen.isBatteryStatusDisplayed()

        liveViewScreen.refreshCameraStatus()
        mockUtils.setBatteryProgressCameraX2(34)
        liveViewScreen.closeHelpView()
        liveViewScreen.isBatteryIndicatorTextDisplayed("34")
        liveViewScreen.isBatteryStatusDisplayed()

        mockUtils.setBatteryProgressCameraX2(5)
        liveViewScreen.isBatteryIndicatorTextDisplayed("5")
        liveViewScreen.isBatteryStatusDisplayed()

        liveViewScreen.refreshCameraStatus()
        mockUtils.setBatteryProgressCameraX2(0)
        liveViewScreen.closeHelpView()
        liveViewScreen.isBatteryIndicatorTextDisplayed("0")
        liveViewScreen.isBatteryStatusDisplayed()
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-1984
     */
    @Test
    @AllowFlaky(attempts = 1)
    fun verifyStorageIndicator() {

        liveViewScreen.refreshCameraStatus()
        mockUtils.setStorageProgressCameraX2(100)
        liveViewScreen.closeHelpView()
        liveViewScreen.isMemoryStorageIndicatorTextDisplayed("100")
        liveViewScreen.isMemoryStorageStatusDisplayed()

        mockUtils.setStorageProgressCameraX2(84)
        liveViewScreen.isMemoryStorageIndicatorTextDisplayed("84")
        liveViewScreen.isMemoryStorageStatusDisplayed()

        liveViewScreen.refreshCameraStatus()
        mockUtils.setStorageProgressCameraX2(0)
        liveViewScreen.closeHelpView()
        liveViewScreen.isLowStorageNotificationDisplayed()
        liveViewScreen.isMemoryStorageIndicatorTextDisplayed("0")
        liveViewScreen.isMemoryStorageStatusDisplayed()
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-2933
     */
    @Test
    @AllowFlaky(attempts = 1)
    fun verifyLowSignalNotification() {
        with(liveViewScreen) {
            mockUtils.setWifiSignalLowOn()
            openSnapshotList()
            isLowSignalPopUpDisplayed()
        }
    }
}
