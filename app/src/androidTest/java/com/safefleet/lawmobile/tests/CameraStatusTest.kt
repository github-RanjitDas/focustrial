package com.safefleet.lawmobile.tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.lawmobile.domain.enums.CameraType
import com.lawmobile.presentation.ui.login.x1.LoginX1Activity
import com.safefleet.lawmobile.screens.LiveViewScreen
import com.safefleet.lawmobile.screens.LoginScreen
import com.schibsted.spain.barista.rule.BaristaRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class CameraStatusTest : EspressoBaseTest() {
    private val liveViewScreen = LiveViewScreen()

    @get:Rule
    var baristaRule = BaristaRule.create(LoginX1Activity::class.java)

    @Before
    fun setUp() {
        mockUtils.setCameraType(CameraType.X1)
        baristaRule.launchActivity()
        LoginScreen().login()
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-1780
     */
    @Test
    fun verifyBatteryIndicator() {
        mockUtils.setBatteryProgressCamera(100)
        liveViewScreen.refreshCameraStatusX1()
        liveViewScreen.isBatteryIndicatorTextDisplayedX1("100")
        liveViewScreen.isBatteryStatusDisplayed()

        mockUtils.setBatteryProgressCamera(34)
        liveViewScreen.refreshCameraStatusX1()
        liveViewScreen.isBatteryIndicatorTextDisplayedX1("34")
        liveViewScreen.isBatteryStatusDisplayed()

        mockUtils.setBatteryProgressCamera(5)
        liveViewScreen.refreshCameraStatusX1()
        liveViewScreen.clickOkButton()
        liveViewScreen.isBatteryIndicatorTextDisplayedX1("5")
        liveViewScreen.isBatteryStatusDisplayed()

        mockUtils.setBatteryProgressCamera(0)
        liveViewScreen.refreshCameraStatusX1()
        liveViewScreen.isBatteryIndicatorNotAvailableDisplayedX1()
        liveViewScreen.isBatteryStatusDisplayed()
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-1984
     */
    @Test
    fun verifyStorageIndicator() {
        mockUtils.setStorageProgressCamera(60_000_000, 60_000_000)
        liveViewScreen.refreshCameraStatusX1()
        liveViewScreen.isMemoryStorageIndicatorTextDisplayedX1("0 MB")
        liveViewScreen.isMemoryStorageStatusDisplayed()

        mockUtils.setStorageProgressCamera(60_000_000, 10_000_000)
        liveViewScreen.refreshCameraStatusX1()
        liveViewScreen.isMemoryStorageIndicatorTextDisplayedX1("47.7 GB")
        liveViewScreen.isMemoryStorageStatusDisplayed()

        mockUtils.setStorageProgressCamera(60_000_000, 0)
        liveViewScreen.refreshCameraStatusX1()
        liveViewScreen.clickOkButton()
        liveViewScreen.isMemoryStorageIndicatorTextDisplayedX1("57.2 GB")
        liveViewScreen.isMemoryStorageStatusDisplayed()
    }
}
