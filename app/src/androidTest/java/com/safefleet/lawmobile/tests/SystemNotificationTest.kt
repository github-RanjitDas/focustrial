package com.safefleet.lawmobile.tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.lawmobile.domain.enums.CameraType
import com.lawmobile.presentation.ui.login.LoginActivity
import com.safefleet.lawmobile.di.mocksServiceCameras.CameraConnectServiceMock
import com.safefleet.lawmobile.helpers.CustomAssertionActions.waitUntil
import com.safefleet.lawmobile.helpers.MockUtils
import com.safefleet.lawmobile.screens.LoginScreen
import com.safefleet.lawmobile.screens.NotificationViewScreen
import com.safefleet.lawmobile.testData.CameraEventsData
import com.safefleet.mobile.external_hardware.cameras.entities.NotificationResponse
import com.schibsted.spain.barista.rule.BaristaRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class SystemNotificationTest : EspressoBaseTest() {

    private val notificationViewScreen = NotificationViewScreen()

    @get:Rule
    var baristaRule = BaristaRule.create(LoginActivity::class.java)

    @Before
    fun setUp() {
        mockUtils.setCameraType(CameraType.X2)
        baristaRule.launchActivity()
        LoginScreen().login()
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-1793
     * Test case: https://safefleet.atlassian.net/browse/FMA-1794
     */
    @Test
    fun verifyPushNotificationIsShown() {
        var notification = NotificationResponse(
            "7",
            "warn:low_battery_warning",
            "Low battery in your body-worn camera, please charge your body-worn camera"
        )
        mockUtils.setBatteryProgressCamera(8)
        MockUtils.cameraConnectServiceX1Mock.sendPushNotification(notification)

        with(notificationViewScreen) {
            isWarningIconDisplayed()
            isLowBatteryNotificationDisplayed()

            clickOnDismissButton()
        }

        notification = NotificationResponse(
            "7",
            "err:low_storage_warning",
            "Your body-worn camera reached 95% of its memory capacity"
        )
        MockUtils.cameraConnectServiceX1Mock.sendPushNotification(notification)

        with(notificationViewScreen) {
            isErrorIconDisplayed()
            isLowStorageTitleDisplayed()
            isDateDisplayed()
            isLowStorageDescriptionDisplayed()
            isDismissButtonDisplayed()

            clickOnDismissButton()
        }

        notification = NotificationResponse(
            "7",
            "low_battery_warning",
            "Low battery in your body-worn camera, please charge your body-worn camera"
        )
        MockUtils.cameraConnectServiceX1Mock.sendPushNotification(notification)

        with(notificationViewScreen) {
            isInfoIconDisplayed()
            isLowBatteryNotificationDisplayed()
        }
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-1795
     */
    @Test
    fun verifyCorrectNotificationNumberIsShown() {
        CameraConnectServiceMock.eventList = CameraEventsData.LOG_EVENT_LIST.value
        with(notificationViewScreen) {
            isPendingNotificationDisplayed()
            val notificationCount = CameraEventsData.LOG_EVENT_LIST.value.size.toString()
            waitUntil {
                isCorrectNumberOfPendingNotificationDisplayed(notificationCount)
            }
        }
    }
}
