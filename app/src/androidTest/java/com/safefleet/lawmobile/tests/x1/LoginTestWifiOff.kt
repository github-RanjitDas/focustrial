package com.safefleet.lawmobile.tests.x1

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.lawmobile.domain.enums.CameraType
import com.lawmobile.presentation.ui.login.x1.LoginX1Activity
import com.safefleet.lawmobile.tests.EspressoStartActivityBaseTest
import com.schibsted.spain.barista.rule.flaky.AllowFlaky
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class LoginTestWifiOff :
    EspressoStartActivityBaseTest<LoginX1Activity>(LoginX1Activity::class.java) {

    @Before
    fun setUp() {
        mockUtils.setCameraType(CameraType.X1)
        mockUtils.turnWifiOff()
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-1040
     */
    @Test
    @AllowFlaky(attempts = 2)
    fun verifyPairingWifiOff() {
        with(LoginTest.loginScreen) {
            mockUtils.turnWifiOff()

            clickOnGo()

            isWifiOffAlertDisplayed()
            clickOnCancel()

            mockUtils.turnWifiOn()
            login()

            LoginTest.liveViewScreen.isLiveViewDisplayed()
        }
    }
}
