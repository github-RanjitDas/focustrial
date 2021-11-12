package com.safefleet.lawmobile.screens

import com.lawmobile.domain.enums.NotificationType
import com.safefleet.lawmobile.R
import com.safefleet.lawmobile.helpers.CustomAssertionActions.waitUntil
import com.schibsted.spain.barista.assertion.BaristaImageViewAssertions.assertHasDrawable
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertContains
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn

class NotificationViewScreen : BaseScreen() {

    fun clickOnDismissButton() = clickOn(R.id.buttonDismissNotification)

    private fun isTypeHeaderDisplayed() =
        waitUntil {
            assertDisplayed(R.id.textViewType, R.string.type)
        }

    private fun isNotificationHeaderDisplayed() =
        waitUntil {
            assertDisplayed(R.id.textViewNotification, R.string.notification)
        }

    private fun isDateTimeHeaderDisplayed() =
        waitUntil {
            assertDisplayed(R.id.textViewDateAndTime, R.string.date_and_time)
        }

    fun isNotificationViewDisplayed() {
        waitUntil { isTypeHeaderDisplayed() }
        isNotificationHeaderDisplayed()
        isDateTimeHeaderDisplayed()
    }

    fun isWarningIconDisplayed() =
        waitUntil { assertHasDrawable(R.id.imageViewNotificationIcon, R.drawable.ic_warning_icon) }

    fun isErrorIconDisplayed() =
        waitUntil { assertHasDrawable(R.id.imageViewNotificationIcon, R.drawable.ic_error_icon) }

    fun isInfoIconDisplayed() =
        waitUntil { assertHasDrawable(R.id.imageViewNotificationIcon, R.drawable.ic_info_icon) }

    fun isDateDisplayed() = assertDisplayed(R.id.textViewNotificationDate)

    private fun isLowBatteryTitleDisplayed() =
        waitUntil {
            assertDisplayed(R.id.textViewNotificationTitle, NotificationType.LOW_BATTERY.title!!)
        }

    fun isLowStorageTitleDisplayed() =
        waitUntil {
            assertDisplayed(R.id.textViewNotificationTitle, NotificationType.LOW_STORAGE.title!!)
        }

    private fun isLowBatteryDescriptionDisplayed(value: String) =
        assertDisplayed(
            R.id.textViewNotificationMessage,
            NotificationType.LOW_BATTERY.getCustomMessage(value)!!
        )

    fun isLowStorageDescriptionDisplayed() =
        waitUntil {
            assertDisplayed(
                R.id.textViewNotificationMessage,
                NotificationType.LOW_STORAGE.message!!
            )
        }

    fun isDismissButtonDisplayed() =
        waitUntil {
            assertDisplayed(R.id.buttonDismissNotification, R.string.dismiss)
        }

    fun isLowBatteryNotificationDisplayed(value: String) {
        isLowBatteryTitleDisplayed()
        isDateDisplayed()
        isLowBatteryDescriptionDisplayed(value)
        isDismissButtonDisplayed()
    }

    fun isPendingNotificationDisplayed() = assertDisplayed(R.id.buttonNotification)

    fun isCorrectNumberOfPendingNotificationDisplayed(pendingNotifications: String) =
        assertContains(pendingNotifications)
}
