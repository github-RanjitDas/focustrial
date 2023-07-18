package net.safefleet.focus.tests.x1

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.lawmobile.domain.enums.CameraType
import com.lawmobile.presentation.ui.login.x2.LoginX2Activity
import com.schibsted.spain.barista.rule.flaky.AllowFlaky
import net.safefleet.focus.tests.EspressoStartActivityBaseTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class LoginTestWifiOff :
    EspressoStartActivityBaseTest<LoginX2Activity>(LoginX2Activity::class.java) {

    @Before
    fun setUp() {
        mockUtils.setCameraType(CameraType.X2)
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
