package com.safefleet.lawmobile.screens

import com.safefleet.lawmobile.R
import com.safefleet.lawmobile.helpers.CustomAssertionActions.waitUntil
import com.schibsted.spain.barista.assertion.BaristaImageViewAssertions.assertHasDrawable
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertContains
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn

class NotificationViewScreen : BaseScreen() {

    fun clickOnDismissButton() = clickOn(R.id.buttonDismissNotification)

    private fun isTypeHeaderDisplayed() = assertDisplayed(R.id.textViewType, R.string.type)

    private fun isNotificationHeaderDisplayed() =
        assertDisplayed(R.id.textViewNotification, R.string.notification)

    private fun isDateTimeHeaderDisplayed() =
        assertDisplayed(R.id.textViewDateAndTime, R.string.date_and_time)

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

    fun isLowBatteryTitleDisplayed() =
        assertDisplayed(R.id.textViewNotificationTitle, "low_battery_warning")

    fun isLowStorageTitleDisplayed() =
        assertDisplayed(R.id.textViewNotificationTitle, "low_storage_warning")

    fun isLowBatteryDescriptionDisplayed() =
        assertDisplayed(R.id.textViewNotificationMessage, R.string.battery_alert_description)

    fun isLowStorageDescriptionDisplayed() =
        assertDisplayed(R.id.textViewNotificationMessage, R.string.storage_alert_description)

    fun isDismissButtonDisplayed() =
        assertDisplayed(R.id.buttonDismissNotification, R.string.dismiss)

    fun isLowBatteryNotificationDisplayed() {
        isLowBatteryTitleDisplayed()
        isDateDisplayed()
        isLowBatteryDescriptionDisplayed()
        isDismissButtonDisplayed()
    }

    fun isPendingNotificationDisplayed() = assertDisplayed(R.id.textPendingNotification)

    fun isCorrectNumberOfPendingNotificationDisplayed(pendingNotifications: String) =
        assertContains(pendingNotifications)
}
