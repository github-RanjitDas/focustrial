package com.safefleet.lawmobile.tests.x1

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.lawmobile.presentation.ui.login.LoginActivity
import com.safefleet.lawmobile.tests.EspressoBaseTest
import com.schibsted.spain.barista.rule.BaristaRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class LoginTestWifiOff : EspressoBaseTest() {

    @get:Rule
    var baristaRule = BaristaRule.create(LoginActivity::class.java)

    @Before
    fun setupTest() {
        mockUtils.turnWifiOff()
        baristaRule.launchActivity()
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-1040
     */
    @Test
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
