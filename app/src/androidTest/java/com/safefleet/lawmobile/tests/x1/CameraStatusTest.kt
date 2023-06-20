package com.safefleet.lawmobile.tests.x1

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.lawmobile.domain.enums.CameraType
import com.lawmobile.presentation.ui.login.x2.LoginX2Activity
import com.safefleet.lawmobile.screens.LiveViewScreen
import com.safefleet.lawmobile.screens.LoginScreen
import com.safefleet.lawmobile.tests.EspressoStartActivityBaseTest
import com.schibsted.spain.barista.rule.flaky.AllowFlaky
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@LargeTest
@RunWith(AndroidJUnit4::class)
class CameraStatusTest :
    EspressoStartActivityBaseTest<LoginX2Activity>(LoginX2Activity::class.java) {

    private val liveViewScreen = LiveViewScreen()

    @Before
    fun setUp() {
        mockUtils.setCameraType(CameraType.X2)
        LoginScreen().login()
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-1780
     */
    @Test
    @AllowFlaky(attempts = 1)
    // TODO: It's failing for a reported bug -> https://safefleet.atlassian.net/browse/FMA-3130
    fun verifyBatteryIndicator() {
        mockUtils.setBatteryProgressCamera(100)
        liveViewScreen.isBatteryIndicatorTextDisplayedX1("100")
        liveViewScreen.isBatteryStatusDisplayed()

        mockUtils.setBatteryProgressCamera(34)
        liveViewScreen.isBatteryIndicatorTextDisplayedX1("34")
        liveViewScreen.isBatteryStatusDisplayed()

        mockUtils.setBatteryProgressCamera(5)
        liveViewScreen.clickOkButton()
        liveViewScreen.isBatteryIndicatorTextDisplayedX1("5")
        liveViewScreen.isBatteryStatusDisplayed()

        mockUtils.setBatteryProgressCamera(0)
        liveViewScreen.isBatteryIndicatorNotAvailableDisplayedX1()
        liveViewScreen.isBatteryStatusDisplayed()
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-1984
     */
    // TODO: It's failing for a reported bug -> https://safefleet.atlassian.net/browse/FMA-3130
    @Test
    @AllowFlaky(attempts = 1)
    fun verifyStorageIndicator() {
        mockUtils.setStorageProgressCamera(60_000_000, 60_000_000)
        liveViewScreen.isMemoryStorageIndicatorTextDisplayedX1("0 MB")
        liveViewScreen.isMemoryStorageStatusDisplayed()

        mockUtils.setStorageProgressCamera(60_000_000, 10_000_000)
        liveViewScreen.isMemoryStorageIndicatorTextDisplayedX1("47.7 GB")
        liveViewScreen.isMemoryStorageStatusDisplayed()

        mockUtils.setStorageProgressCamera(60_000_000, 0)
        liveViewScreen.clickOkButton()
        liveViewScreen.isMemoryStorageIndicatorTextDisplayedX1("57.2 GB")
        liveViewScreen.isMemoryStorageStatusDisplayed()
    }
}
