package com.safefleet.lawmobile.screens

import com.safefleet.lawmobile.R
import com.safefleet.lawmobile.helpers.CustomAssertionActions.waitUntil
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed

class NotificationViewScreen : BaseScreen() {

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
}
