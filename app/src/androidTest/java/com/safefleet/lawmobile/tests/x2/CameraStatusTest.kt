package com.safefleet.lawmobile.tests.x2

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.lawmobile.domain.enums.CameraType
import com.lawmobile.presentation.ui.login.x2.LoginX2Activity
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
    var baristaRule = BaristaRule.create(LoginX2Activity::class.java)

    @Before
    fun setUp() {
        mockUtils.setCameraType(CameraType.X2)
        baristaRule.launchActivity()
        loginScreen.loginWithoutSSO()
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-1780
     */
    @Test
    fun verifyBatteryIndicator() {
        with(liveViewScreen) {
            mockUtils.setBatteryProgressCamera(100)
            refreshCameraStatus()
            isBatteryIndicatorTextDisplayed("100")
            isBatteryStatusDisplayed()

            mockUtils.setBatteryProgressCamera(34)
            refreshCameraStatus()
            isBatteryIndicatorTextDisplayed("34")
            isBatteryStatusDisplayed()

            mockUtils.setBatteryProgressCamera(5)
            refreshCameraStatus()
            isBatteryIndicatorTextDisplayed("5")
            isBatteryStatusDisplayed()

            mockUtils.setBatteryProgressCamera(0)
            refreshCameraStatus()
            isBatteryIndicatorTextDisplayed("0")
            isBatteryStatusDisplayed()
        }
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-1984
     */
    @Test
    fun verifyStorageIndicator() {
        with(liveViewScreen) {
            mockUtils.setStorageProgressCamera(60000000, 60000000)
            refreshCameraStatus()
            isMemoryStorageIndicatorTextDisplayed("100")
            isMemoryStorageStatusDisplayed()

            mockUtils.setStorageProgressCamera(60000000, 10000000)
            refreshCameraStatus()
            isMemoryStorageIndicatorTextDisplayed("16")
            isMemoryStorageStatusDisplayed()

            mockUtils.setStorageProgressCamera(60000000, 0)
            refreshCameraStatus()
            isMemoryStorageIndicatorTextDisplayed("0")
            isMemoryStorageStatusDisplayed()
        }
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
