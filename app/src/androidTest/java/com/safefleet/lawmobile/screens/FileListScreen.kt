package com.safefleet.lawmobile.screens

import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.test.espresso.assertion.ViewAssertions
import com.safefleet.lawmobile.R
import com.safefleet.lawmobile.helpers.CustomCheckboxAction
import com.safefleet.lawmobile.helpers.isActivated
import com.safefleet.lawmobile.helpers.isNotActivated
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.schibsted.spain.barista.assertion.BaristaListAssertions
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn
import com.schibsted.spain.barista.interaction.BaristaEditTextInteractions.writeTo
import com.schibsted.spain.barista.interaction.BaristaListInteractions

class FileListScreen : BaseScreen() {

    @IdRes
    var recyclerView: Int = 0

    @IdRes
    var targetView: Int = 0

    @IdRes
    var targetCheckBox: Int = 0

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
            targetView,
            fileDate
        )
    }

    fun areFilesSortedByDate(filesList: List<CameraConnectFile>) {
        val sortedFilesList = filesList.sortedByDescending { it.date }

        for ((itemCount, file) in sortedFilesList.withIndex()) {
            isFileDisplayedAtPosition(file.date, itemCount)
        }
    }

    fun selectCheckboxOnPosition(position: Int) {
        CustomCheckboxAction.selectCheckboxOnRecyclerPosition(
            recyclerView,
            targetCheckBox,
            position
        )
    }

    fun isCheckboxSelected(position: Int) {
        BaristaListAssertions.assertCustomAssertionAtPosition(
            recyclerView,
            position,
            targetCheckBox,
            ViewAssertions.matches(isActivated())
        )
    }

    fun isCheckboxUnselected(position: Int) {
        BaristaListAssertions.assertCustomAssertionAtPosition(
            recyclerView,
            position,
            targetCheckBox,
            ViewAssertions.matches(isNotActivated())
        )
    }

    fun checkFileEvent(event: String?) = event?.let { assertDisplayed(it) }

    fun isFileListDisplayed() = assertDisplayed(R.id.fileListRecycler)

    fun areNoFilesFound(@StringRes message: Int) =
        assertDisplayed(R.id.noFilesTextView, message)

    fun clickOnBack() = clickOn(R.id.backArrowFileListAppBar)

    fun clickOnItemInPosition(position: Int) =
        BaristaListInteractions.clickListItem(recyclerView, position)

    fun clickOnSelectFilesToAssociate() = clickOn(R.id.buttonSelectSnapshotsToAssociate)

    fun clickOnAssociateWithAnOfficer() = clickOn(R.string.associate_with_an_officer)

    fun clickOnButtonAssignToOfficer() = clickOn(R.id.buttonAssignToOfficer)

    fun clickOnSimpleListButton() = clickOn(R.id.buttonSimpleList)

    fun typeOfficerIdToAssociate(officerId: String) =
        writeTo(R.id.editTextAssignToOfficer, officerId)
}
