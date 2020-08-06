package com.safefleet.lawmobile.screens

import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import com.safefleet.lawmobile.R
import com.safefleet.lawmobile.helpers.CustomCheckboxAction
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.schibsted.spain.barista.assertion.BaristaListAssertions
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn
import com.schibsted.spain.barista.interaction.BaristaListInteractions
import com.schibsted.spain.barista.interaction.BaristaSleepInteractions

class FileListScreen : BaseScreen() {

    @IdRes
    var recyclerView: Int = 0

    fun areCheckboxesUnselected(startPosition: Int, endPosition: Int) {
        for (position in (startPosition..endPosition)) {
            isCheckboxUnselected(position)
        }
    }

    fun scrollListToPosition(position: Int) {
        BaristaListInteractions.scrollListToPosition(recyclerView, position)
    }

    private fun isFileDisplayedAtPosition(fileDate: String, position: Int) {
        BaristaListAssertions.assertDisplayedAtPosition(
            recyclerView,
            position,
            fileDate
        )
    }

    fun areFilesSortedByDate(filesList: List<CameraConnectFile>) {
        val sortedFilesList = filesList.sortedByDescending { it.date }

        for ((itemCount, file) in sortedFilesList.withIndex()) {
            this.isFileDisplayedAtPosition(file.date, itemCount)
        }
    }

    fun selectCheckboxOnPosition(position: Int): FileListScreen {
        CustomCheckboxAction.selectCheckboxOnRecyclerPosition(recyclerView, position)
        return this
    }

    fun isCheckboxSelected(position: Int): FileListScreen {
        BaristaListAssertions.assertCustomAssertionAtPosition(
            recyclerView,
            position,
            viewAssertion = ViewAssertions.matches(ViewMatchers.withChild(ViewMatchers.isChecked()))
        )
        return this
    }

    fun isCheckboxUnselected(position: Int): FileListScreen {
        BaristaListAssertions.assertCustomAssertionAtPosition(
            recyclerView,
            position,
            viewAssertion = ViewAssertions.matches(ViewMatchers.withChild(ViewMatchers.isNotChecked()))
        )
        return this
    }

    fun isSnapshotListDisplayed() = assertDisplayed(R.id.snapshotListRecycler)

    fun isVideoListDisplayed() = assertDisplayed(R.id.videoListRecycler)

    fun switchToVideosList() {
        BaristaSleepInteractions.sleep(500)
        clickOn(R.id.buttonVideoListSwitch)
        BaristaSleepInteractions.sleep(2000)
    }

    fun switchToSnapshotsList() {
        BaristaSleepInteractions.sleep(500)
        clickOn(R.id.buttonSnapshotListSwitch)
        BaristaSleepInteractions.sleep(2000)
    }

    fun areNoFilesFound(@StringRes toastMessage: Int) =
        assertDisplayed(R.id.noFilesTextView, toastMessage)

    fun goBack() = clickOn(R.id.textViewFileListBack)

    fun clickOnItemInPosition(position: Int) =
        BaristaListInteractions.clickListItem(recyclerView, position)
}
