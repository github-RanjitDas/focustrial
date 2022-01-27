package com.safefleet.lawmobile.tests.x2

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.lawmobile.domain.enums.CameraType
import com.lawmobile.presentation.ui.login.x1.LoginX1Activity
import com.safefleet.lawmobile.screens.LiveViewScreen
import com.safefleet.lawmobile.screens.LoginScreen
import com.safefleet.lawmobile.tests.EspressoBaseTest
import com.schibsted.spain.barista.rule.BaristaRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class CameraStatusTest : EspressoBaseTest() {
    private val liveViewScreen = LiveViewScreen()
    private val loginScreen = LoginScreen()

    @get:Rule
    var baristaRule = BaristaRule.create(LoginX1Activity::class.java)

    @Before
    fun setUp() {
        mockUtils.setCameraType(CameraType.X2)
        baristaRule.launchActivity()
        loginScreen.login()
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-1780
     */
    @Test
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
    fun verifyStorageIndicator() {
        mockUtils.setStorageProgressCameraX2(100)
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
    fun verifyLowSignalNotification() {
        with(liveViewScreen) {
            mockUtils.setWifiSignalLowOn()
            openSnapshotList()
            isLowSignalPopUpDisplayed()
        }
    }
}
