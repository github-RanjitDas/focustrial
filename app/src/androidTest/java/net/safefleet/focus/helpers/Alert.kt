package net.safefleet.focus.helpers

import androidx.test.espresso.Espresso
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertNotContains
import net.safefleet.focus.R

object Alert {

    fun isDisconnectionDueInactivityAlertDisplayed() {
        assertDisplayed(R.string.connection_finished)
        assertDisplayed(R.string.connection_finished_description)
        assertDisplayed(R.string.OK)

        Espresso.pressBack()
        assertDisplayed(R.string.connection_finished_description)
    }

    fun isDisconnectionAlertDisplayed() {
        assertDisplayed(R.string.the_camera_was_disconnected)
        assertDisplayed(R.string.the_camera_was_disconnected_description)

        // Verify alert cannot be dismissed
        assertNotContains(R.string.cancel)
        Espresso.pressBack()
        assertDisplayed(R.string.the_camera_was_disconnected)
    }

    fun isMetadataChangesDisplayed() {
        assertDisplayed(R.string.unsaved_changes)
        assertDisplayed(R.string.unsaved_changes_message)
        assertDisplayed(R.string.cancel)
        assertDisplayed(R.string.accept)
    }

    fun isWifiOffAlertDisplayed() {
        assertDisplayed(R.string.wifi_is_off)
        assertDisplayed(R.string.please_turn_wifi_on)
        assertDisplayed(R.string.OK)
    }

    fun isExitAppDialogDisplayed() {
        assertDisplayed(R.string.logout)
        assertDisplayed(R.string.logout_message)
    }
}
