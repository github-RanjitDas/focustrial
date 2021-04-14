package com.safefleet.lawmobile.screens

import androidx.annotation.StringRes
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.RootMatchers.isPlatformPopup
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.safefleet.lawmobile.R
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn
import com.schibsted.spain.barista.interaction.BaristaPickerInteractions.setDateOnPicker

class FilterDialogScreen {

    fun selectStartDate(year: Int? = null, month: Int? = null, day: Int? = null) {
        clickOn(R.id.startDateTextView)
        if (year != null && month != null && day != null)
            selectDate(year, month, day)
    }

    fun selectEndDate(year: Int? = null, month: Int? = null, day: Int? = null) {
        clickOn(R.id.endDateTextView)
        if (year != null && month != null && day != null)
            selectDate(year, month, day)
    }

    private fun selectDate(year: Int, month: Int, day: Int) =
        setDateOnPicker(year, month, day)

    fun clickOnOk() = clickOn(android.R.id.button1)

    fun applyFilter() = clickOn(R.id.buttonApplyFilter)

    fun clearStartDate() = clickOn(R.id.buttonClearStartDate)

    fun selectEvent(@StringRes event: Int) {
        clickOn(R.id.eventsSpinnerFilter)
        onView(withText(event)).inRoot(isPlatformPopup()).perform(click())
    }

    fun selectEvent(event: String) {
        clickOn(R.id.eventsSpinnerFilter)
        onView(withText(event)).inRoot(isPlatformPopup()).perform(click())
    }
}
