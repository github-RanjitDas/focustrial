package net.safefleet.focus.screens

import androidx.test.espresso.Espresso.pressBack
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import net.safefleet.focus.R
import net.safefleet.focus.helpers.CustomAssertionActions.waitUntil

class HelpPageScreen : BaseScreen() {

    fun goBack() {
        waitUntil { pressBack() }
    }

    fun isUserGuideDisplayed() {
        waitUntil { assertDisplayed(R.string.user_guide) }
        waitUntil { assertDisplayed(R.id.pdfView) }
    }
}
