package com.safefleet.lawmobile.tests.x2

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.lawmobile.body_cameras.entities.NotificationResponse
import com.lawmobile.domain.enums.CameraType
import com.lawmobile.presentation.ui.login.x2.LoginX2Activity
import com.safefleet.lawmobile.di.mocksServiceCameras.CameraConnectServiceMock
import com.safefleet.lawmobile.helpers.CustomAssertionActions.waitUntil
import com.safefleet.lawmobile.helpers.MockUtils.Companion.bodyCameraServiceMock
import com.safefleet.lawmobile.screens.LiveViewScreen
import com.safefleet.lawmobile.screens.LoginScreen
import com.safefleet.lawmobile.screens.NotificationViewScreen
import com.safefleet.lawmobile.testData.CameraEventsData
import com.safefleet.lawmobile.tests.EspressoStartActivityBaseTest
import com.schibsted.spain.barista.rule.flaky.AllowFlaky
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class SystemNotificationTest :
    EspressoStartActivityBaseTest<LoginX2Activity>(LoginX2Activity::class.java) {

    private val notificationViewScreen = NotificationViewScreen()
    private val liveViewScreen = LiveViewScreen()

    @Before
    fun setUp() {
        mockUtils.setCameraType(CameraType.X2)
        LoginScreen().loginWithoutSSO()
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-1793
     * Test case: https://safefleet.atlassian.net/browse/FMA-1794
     */
    @Test
    @AllowFlaky(attempts = 1)
    fun verifyPushNotificationIsShown() {
        var notification = NotificationResponse(
            "7",
            "warn:low_battery_warning",
            "7"
        )
        mockUtils.setBatteryProgressCamera(8)
        bodyCameraServiceMock.sendPushNotification(notification)

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
        bodyCameraServiceMock.sendPushNotification(notification)

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
        bodyCameraServiceMock.sendPushNotification(notification)

        with(notificationViewScreen) {
            isInfoIconDisplayed()
            isLowBatteryNotificationDisplayed("6")
        }
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-2143
     */
    @Test
    @AllowFlaky(attempts = 1)
    fun verifyChangeBatteryLevelInText() {
        var notification = NotificationResponse(
            "7",
            "battery_level",
            "8"
        )
        bodyCameraServiceMock.sendPushNotification(notification)

        liveViewScreen.isTextBatteryIndicatorContained("8 %")

        notification = NotificationResponse(
            "7",
            "battery_level",
            "7"
        )
        bodyCameraServiceMock.sendPushNotification(notification)

        liveViewScreen.isTextBatteryIndicatorContained("7 %")

        notification = NotificationResponse(
            "7",
            "low_battery_warning",
            "7"
        )

        bodyCameraServiceMock.sendPushNotification(notification)
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
        bodyCameraServiceMock.sendPushNotification(notification)
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

        bodyCameraServiceMock.sendPushNotification(notification)
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
        bodyCameraServiceMock.sendPushNotification(notification)
        liveViewScreen.isTextBatteryIndicatorContained("4 Mins")

        notification = NotificationResponse(
            "7",
            "low_battery_warning",
            "1"
        )

        bodyCameraServiceMock.sendPushNotification(notification)
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
    @AllowFlaky(attempts = 1)
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
