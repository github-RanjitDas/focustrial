package net.safefleet.focus.screens

import androidx.test.espresso.matcher.ViewMatchers.withTagValue
import com.schibsted.spain.barista.assertion.BaristaImageViewAssertions.assertHasAnyDrawable
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertContains
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn
import net.safefleet.focus.R
import net.safefleet.focus.helpers.CustomAssertionActions.waitUntil
import org.hamcrest.CoreMatchers.equalTo

class BodyWornDiagnosisScreen : BaseScreen() {

    fun clickOnStartButton() {
        isStartButtonDisplayed()
        clickOn(R.id.buttonStartDiagnosis)
    }

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
        waitUntil { assertDisplayed(R.id.titleBackgroundSolid) }
        waitUntil { assertDisplayed(R.id.imageIconResult) }
        waitUntil { assertHasAnyDrawable(R.id.imageIconResult) }
        waitUntil { assertDisplayed(withTagValue(equalTo(R.drawable.ic_success_icon))) }
        waitUntil { assertContains(R.string.body_worn_diagnosis_success_text) }
    }

    fun isSuccessMessageBodyDisplayed() =
        assertDisplayed(
            R.id.textDescriptionDiagnosis,
            R.string.diagnosis_success_message
        )

    fun isOkButtonDisplayed() = assertDisplayed(R.id.buttonOkFinishedDiagnosis, R.string.OK)

    fun isFailTitleDisplayed() {
        assertDisplayed(R.id.titleBackgroundSolid)
        waitUntil { assertDisplayed(R.id.imageIconResult) }
        waitUntil { assertHasAnyDrawable(R.id.imageIconResult) }
        waitUntil { assertDisplayed(withTagValue(equalTo(R.drawable.ic_error_diagnosis_icon))) }
        assertContains(R.string.body_worn_diagnosis_error_text)
    }

    fun isFailMessageBodyDisplayed() =
        assertDisplayed(R.id.textDescriptionDiagnosis, R.string.diagnosis_failed_message)

    fun isErrorBodyWornDiagnosisMessageDisplayed() =
        waitUntil { assertContains(R.string.error_trying_to_diagnose) }

    fun isCloseButtonDisplayed() = assertDisplayed(R.id.imageButtonBackArrow)
}
