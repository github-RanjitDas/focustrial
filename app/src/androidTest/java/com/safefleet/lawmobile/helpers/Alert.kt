package com.safefleet.lawmobile.helpers

import androidx.test.espresso.Espresso
import com.safefleet.lawmobile.R
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertNotContains

class Alert {

    fun isDisconnectionAlertDisplayed() {
        assertDisplayed(R.string.the_camera_was_disconnected)
        assertDisplayed(R.string.the_camera_was_disconnected_description)

        // Verify alert cannot be dismissed
        assertNotContains(R.string.cancel)
        Espresso.pressBack()
        assertDisplayed(R.string.the_camera_was_disconnected)
    }

}
