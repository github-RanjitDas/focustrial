package com.safefleet.lawmobile.screens

import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.test.espresso.assertion.ViewAssertions
import com.lawmobile.data.extensions.getCreationDate
import com.safefleet.lawmobile.R
import com.safefleet.lawmobile.helpers.CustomAssertionActions.waitUntil
import com.safefleet.lawmobile.helpers.CustomCheckboxAction
import com.safefleet.lawmobile.helpers.isActivated
import com.safefleet.lawmobile.helpers.isNotActivated
import com.safefleet.mobile.external_hardware.cameras.entities.CameraFile
import com.schibsted.spain.barista.assertion.BaristaImageViewAssertions.assertHasDrawable
import com.schibsted.spain.barista.assertion.BaristaListAssertions
import com.schibsted.spain.barista.assertion.BaristaRecyclerViewAssertions
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertNotDisplayed
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn
import com.schibsted.spain.barista.interaction.BaristaEditTextInteractions.writeTo
import com.schibsted.spain.barista.interaction.BaristaListInteractions

class FileListScreen : BaseScreen() {

    @IdRes
    var recyclerView: Int = R.id.fileListRecycler

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

    fun areFilesSortedByDate(filesList: List<CameraFile>) {
        val sortedFilesList = filesList.sortedByDescending { it.getCreationDate() }

        for ((itemCount, file) in sortedFilesList.withIndex()) {
            isFileDisplayedAtPosition(file.getCreationDate(), itemCount)
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

    fun clickOnBack() = clickOn(R.id.imageButtonBackArrow)

    fun clickOnItemInPosition(position: Int) =
        BaristaListInteractions.clickListItem(recyclerView, position)

    fun clickOnSelectFilesToAssociate() = clickOn(R.id.buttonSelectSnapshotsToAssociate)

    fun clickOnAssociateWithAnOfficer() = clickOn(R.string.associate_with_an_officer)

    fun clickOnButtonAssignToOfficer() = clickOn(R.id.buttonAssignToOfficer)

    fun clickOnSimpleListButton() = clickOn(R.id.buttonSimpleList)

    fun typeOfficerIdToAssociate(officerId: String) =
        writeTo(R.id.editTextAssignToOfficer, officerId)

    fun openFilterDialog() {
        clickOn(R.id.buttonOpenFilters)
        assertDisplayed(R.id.startDateTextView)
        assertDisplayed(R.id.endDateTextView)
        assertDisplayed(R.id.buttonApplyFilter)
        assertDisplayed(R.id.buttonCancelFilter)
    }

    fun clickOnRemoveTag() {
        clickOn(R.id.buttonClearTag)
        assertNotDisplayed(R.id.scrollFilterTags)
        assertHasDrawable(R.id.buttonOpenFilters, R.drawable.ic_filter)
    }

    fun matchItemsCount(count: Int) =
        BaristaRecyclerViewAssertions.assertRecyclerViewItemCount(recyclerView, count)

    fun isFilterButtonDisplayed() = assertDisplayed(R.id.buttonOpenFilters)

    fun isFilterActive() {
        assertDisplayed(R.id.scrollFilterTags)
        assertHasDrawable(R.id.buttonOpenFilters, R.drawable.ic_filter_white)
    }

    fun isFilterNotActive() {
        assertNotDisplayed(R.id.scrollFilterTags)
        assertHasDrawable(R.id.buttonOpenFilters, R.drawable.ic_filter)
    }

    fun isNoFilesFoundDisplayed() = assertDisplayed(R.id.noFilesTextView)

    fun isDateTimeHeaderDisplayed() =
        assertDisplayed(R.id.textViewDateAndTime, R.string.date_and_time)

    fun isEventHeaderDisplayed() = assertDisplayed(R.id.textViewEvent, R.string.event)

    fun isSnapshotsTitleDisplayed() = assertDisplayed(R.string.snapshots_title)

    fun isSelectDisplayed() =
        waitUntil { assertDisplayed(R.string.select) }

    fun isVideosListScreenDisplayed() {
        isSelectDisplayed()
        isFilterButtonDisplayed()
        isDateTimeHeaderDisplayed()
        isEventHeaderDisplayed()
    }

    fun isSnapshotsListScreenDisplayed() {
        isSelectDisplayed()
        isFilterButtonDisplayed()
        isSnapshotsTitleDisplayed()
    }
}
