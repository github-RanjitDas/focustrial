package com.safefleet.lawmobile.tests.x2

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.lawmobile.domain.enums.CameraType
import com.lawmobile.presentation.ui.login.x2.LoginX2Activity
import com.safefleet.lawmobile.di.mocksServiceCameras.CameraConnectServiceMock
import com.safefleet.lawmobile.helpers.CustomAssertionActions.waitUntil
import com.safefleet.lawmobile.helpers.MockUtils.Companion.cameraConnectServiceX1Mock
import com.safefleet.lawmobile.screens.LiveViewScreen
import com.safefleet.lawmobile.screens.LoginScreen
import com.safefleet.lawmobile.screens.NotificationViewScreen
import com.safefleet.lawmobile.testData.CameraEventsData
import com.safefleet.lawmobile.tests.EspressoBaseTest
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
    private val liveViewScreen = LiveViewScreen()

    @get:Rule
    var baristaRule = BaristaRule.create(LoginX2Activity::class.java)

    @Before
    fun setUp() {
        mockUtils.setCameraType(CameraType.X2)
        baristaRule.launchActivity()
        LoginScreen().loginWithoutSSO()
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
            "7"
        )
        mockUtils.setBatteryProgressCamera(8)
        cameraConnectServiceX1Mock.sendPushNotification(notification)

        with(notificationViewScreen) {
            isWarningIconDisplayed()
            isLowBatteryNotificationDisplayed("7")

            clickOnDismissButton()
        }

        notification = NotificationResponse(
            "7",
            "err:low_storage_warning",
            "95"
        )
        cameraConnectServiceX1Mock.sendPushNotification(notification)

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
            "6"
        )
        cameraConnectServiceX1Mock.sendPushNotification(notification)

        with(notificationViewScreen) {
            isInfoIconDisplayed()
            isLowBatteryNotificationDisplayed("6")
        }
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-2143
     */
    @Test
    fun verifyChangeBatteryLevelInText() {
        var notification = NotificationResponse(
            "7",
            "battery_level",
            "8"
        )
        cameraConnectServiceX1Mock.sendPushNotification(notification)

        liveViewScreen.isTextBatteryIndicatorContained("8 %")

        notification = NotificationResponse(
            "7",
            "battery_level",
            "7"
        )
        cameraConnectServiceX1Mock.sendPushNotification(notification)

        liveViewScreen.isTextBatteryIndicatorContained("7 %")

        notification = NotificationResponse(
            "7",
            "low_battery_warning",
            "7"
        )

        cameraConnectServiceX1Mock.sendPushNotification(notification)
        with(notificationViewScreen) {
            isLowBatteryNotificationDisplayed("7")
            clickOnDismissButton()
        }
        liveViewScreen.isTextBatteryIndicatorContained("7 Mins")

        notification = NotificationResponse(
            "7",
            "low_battery_warning",
            "6"
        )
        cameraConnectServiceX1Mock.sendPushNotification(notification)
        with(notificationViewScreen) {
            isLowBatteryNotificationDisplayed("6")
            clickOnDismissButton()
        }
        liveViewScreen.isTextBatteryIndicatorContained("6 Mins")

        notification = NotificationResponse(
            "7",
            "low_battery_warning",
            "5"
        )

        cameraConnectServiceX1Mock.sendPushNotification(notification)
        with(notificationViewScreen) {
            isLowBatteryNotificationDisplayed("5")
            clickOnDismissButton()
        }

        liveViewScreen.isTextBatteryIndicatorContained("5 Mins")

        notification = NotificationResponse(
            "7",
            "battery_level",
            "4"
        )
        cameraConnectServiceX1Mock.sendPushNotification(notification)
        liveViewScreen.isTextBatteryIndicatorContained("4 Mins")

        notification = NotificationResponse(
            "7",
            "low_battery_warning",
            "1"
        )

        cameraConnectServiceX1Mock.sendPushNotification(notification)
        with(notificationViewScreen) {
            isLowBatteryNotificationDisplayed("1")
            clickOnDismissButton()
        }
        liveViewScreen.isTextBatteryIndicatorContained("1 Mins")
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-1795
     */
    @Test
    fun verifyCorrectNotificationNumberIsShown() {
        CameraConnectServiceMock.eventList = CameraEventsData.LOG_EVENT_LIST.value
        with(notificationViewScreen) {
            isBellButtonDisplayed()
            val notificationCount = CameraEventsData.LOG_EVENT_LIST.value.size.toString()
            waitUntil {
                isCorrectNumberOfPendingNotificationDisplayed(notificationCount)
            }
        }
    }
}
