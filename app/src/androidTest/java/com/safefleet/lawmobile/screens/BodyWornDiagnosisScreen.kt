package com.safefleet.lawmobile.screens

import com.safefleet.lawmobile.R
import com.safefleet.lawmobile.helpers.CustomAssertionActions.waitUntil
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed

class BodyWornDiagnosisScreen : BaseScreen() {

    fun isStartButtonDisplayed() {
        waitUntil { assertDisplayed(R.id.buttonStartDiagnosis, R.string.body_worn_diagnosis_button_text) }
    }
}
