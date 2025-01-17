package net.safefleet.focus.screens

import androidx.test.espresso.matcher.ViewMatchers.withTagValue
import com.lawmobile.domain.enums.NotificationType
import com.schibsted.spain.barista.assertion.BaristaImageViewAssertions.assertHasAnyDrawable
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertContains
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn
import net.safefleet.focus.R
import net.safefleet.focus.helpers.CustomAssertionActions.waitUntil
import org.hamcrest.CoreMatchers.equalTo

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

    fun isWarningIconDisplayed() {
        waitUntil { assertDisplayed(R.id.imageViewNotificationIcon) }
        waitUntil { assertHasAnyDrawable(R.id.imageViewNotificationIcon) }
        waitUntil { assertDisplayed(withTagValue(equalTo(R.drawable.ic_warning_icon))) }
    }

    fun isErrorIconDisplayed() {
        waitUntil { assertDisplayed(R.id.imageViewNotificationIcon) }
        waitUntil { assertHasAnyDrawable(R.id.imageViewNotificationIcon) }
        waitUntil { assertDisplayed(withTagValue(equalTo(R.drawable.ic_error_icon))) }
    }

    fun isInfoIconDisplayed() {
        waitUntil { assertDisplayed(R.id.imageViewNotificationIcon) }
        waitUntil { assertHasAnyDrawable(R.id.imageViewNotificationIcon) }
        waitUntil { assertDisplayed(withTagValue(equalTo(R.drawable.ic_info_icon))) }
    }

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

    fun isBellButtonDisplayed() = waitUntil { assertDisplayed(R.id.buttonNotification) }

    fun isCorrectNumberOfPendingNotificationDisplayed(pendingNotifications: String) =
        assertContains(pendingNotifications)

    fun clickOnBellButton() {
        waitUntil { assertDisplayed(R.id.buttonNotification) }
        waitUntil { clickOn(R.id.buttonNotification) }
    }

    fun isNotificationTitleDisplayed() = waitUntil { assertDisplayed(R.string.notifications) }
}
