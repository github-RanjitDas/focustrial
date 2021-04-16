package com.safefleet.lawmobile.screens

import com.safefleet.lawmobile.R
import com.safefleet.lawmobile.helpers.CustomAssertionActions.waitUntil
import com.schibsted.spain.barista.assertion.BaristaImageViewAssertions.assertHasDrawable
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertContains
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn

class BodyWornDiagnosisScreen : BaseScreen() {

    fun clickOnStartButton() = clickOn(R.id.buttonStartDiagnosis)

    fun clickOnCloseButton() = clickOn(R.id.imageButtonBackArrow)

    fun clickOnOkButton() = clickOn(R.id.buttonOkFinishedDiagnosis)

    fun isStartButtonDisplayed() = waitUntil {
        assertDisplayed(
            R.id.buttonStartDiagnosis,
            R.string.body_worn_diagnosis_button_text
        )
    }

    fun containsBodyWornDiagnosisTitle() = assertContains(R.string.live_view_menu_item_diagnose)

    fun isSuccessTitleDisplayed() {
        assertDisplayed(R.id.titleBackgroundSolid)
        assertHasDrawable(R.id.imageIconResult, R.drawable.ic_success_icon)
        assertContains(R.string.body_worn_diagnosis_success_text)
    }

    fun isSuccessMessageBodyDisplayed() =
        assertDisplayed(
            R.id.textDescriptionDiagnosis,
            R.string.body_worn_result_success_description
        )

    fun isOkButtonDisplayed() = assertDisplayed(R.id.buttonOkFinishedDiagnosis, R.string.OK)

    fun isFailTitleDisplayed() {
        assertDisplayed(R.id.titleBackgroundSolid)
        assertHasDrawable(R.id.imageIconResult, R.drawable.ic_error_diagnosis_icon)
        assertContains(R.string.body_worn_diagnosis_error_text)
    }

    fun isFailMessageBodyDisplayed() =
        assertDisplayed(R.id.textDescriptionDiagnosis, R.string.body_worn_result_failed_description)

    fun isErrorBodyWornDiagnosisMessageDisplayed() =
        assertContains(R.string.body_worn_icon_error_is_not_possible_get_diagnosis)

    fun isCloseButtonDisplayed() = assertDisplayed(R.id.imageButtonBackArrow)
}
