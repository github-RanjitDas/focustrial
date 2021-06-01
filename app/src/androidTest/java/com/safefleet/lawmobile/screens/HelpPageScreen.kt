package com.safefleet.lawmobile.screens

import androidx.test.espresso.Espresso.pressBack
import com.safefleet.lawmobile.R
import com.safefleet.lawmobile.helpers.CustomAssertionActions.waitUntil
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed

class HelpPageScreen : BaseScreen() {

    fun goBack() {
        waitUntil { pressBack() }
    }

    fun isUserGuideDisplayed() {
        waitUntil { assertDisplayed(R.string.user_guide) }
        waitUntil { assertDisplayed(R.id.pdfView) }
    }
}
