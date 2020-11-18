package com.safefleet.lawmobile.screens

import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.lawmobile.presentation.extensions.getCreationDate
import com.safefleet.lawmobile.R
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.schibsted.spain.barista.assertion.BaristaListAssertions.assertCustomAssertionAtPosition
import com.schibsted.spain.barista.assertion.BaristaListAssertions.assertDisplayedAtPosition
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn
import com.schibsted.spain.barista.interaction.BaristaListInteractions.clickListItemChild
import com.schibsted.spain.barista.interaction.BaristaListInteractions.scrollListToPosition

class FileListScreen : BaseScreen() {

    private fun isFileDisplayedAtPosition(fileDate: String, position: Int) {
        assertDisplayedAtPosition(R.id.snapshotListRecycler, position, fileDate)
    }

    fun areFilesSortedByDate(filesList: List<CameraConnectFile>) {
        val sortedFilesList = filesList.sortedByDescending { it.getCreationDate() }

        for ((itemCount, file) in sortedFilesList.withIndex()) {
            this.isFileDisplayedAtPosition(file.date, itemCount)
        }
    }

    fun selectCheckboxOnPosition(position: Int): FileListScreen {
        clickListItemChild(R.id.snapshotListRecycler, position, R.id.checkboxFileListItem)
        return this
    }

    fun isCheckboxSelected(position: Int): FileListScreen {
        assertCustomAssertionAtPosition(
            R.id.snapshotListRecycler,
            position,
            viewAssertion = matches(withChild(isChecked()))
        )
        return this
    }

    fun isCheckboxUnselected(position: Int): FileListScreen {
        assertCustomAssertionAtPosition(
            R.id.snapshotListRecycler,
            position,
            viewAssertion = matches(withChild(isNotChecked()))
        )
        return this
    }

    fun areCheckboxesUnselected(startPosition: Int, endPosition: Int) {
        for (position in (startPosition..endPosition)) {
            isCheckboxUnselected(position)
        }
    }

    fun scrollListToPosition(position: Int) {
        scrollListToPosition(R.id.snapshotListRecycler, position)
    }

    fun switchToVideosList() = clickOn(R.id.buttonVideoListSwitch)

    fun switchToSnapshotsList() = clickOn(R.id.buttonSnapshotListSwitch)

    fun areNoSnapshotsFound() =
        BaristaVisibilityAssertions.assertDisplayed(R.id.noFilesTextView, R.string.no_images_found)


}
