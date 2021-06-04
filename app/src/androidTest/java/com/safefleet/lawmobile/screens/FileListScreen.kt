package com.safefleet.lawmobile.screens

import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import com.lawmobile.data.extensions.getCreationDate
import com.safefleet.lawmobile.R
import com.safefleet.lawmobile.helpers.CustomAssertionActions.waitUntil
import com.safefleet.lawmobile.helpers.CustomCheckboxAction
import com.safefleet.lawmobile.helpers.RecyclerViewHelper
import com.safefleet.lawmobile.helpers.isActivated
import com.safefleet.lawmobile.helpers.isNotActivated
import com.safefleet.mobile.external_hardware.cameras.entities.CameraFile
import com.schibsted.spain.barista.assertion.BaristaImageViewAssertions.assertHasDrawable
import com.schibsted.spain.barista.assertion.BaristaListAssertions.assertCustomAssertionAtPosition
import com.schibsted.spain.barista.assertion.BaristaListAssertions.assertDisplayedAtPosition
import com.schibsted.spain.barista.assertion.BaristaRecyclerViewAssertions.assertRecyclerViewItemCount
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertNotDisplayed
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn
import com.schibsted.spain.barista.interaction.BaristaEditTextInteractions.writeTo
import com.schibsted.spain.barista.interaction.BaristaListInteractions
import com.schibsted.spain.barista.interaction.BaristaListInteractions.clickListItem
import org.hamcrest.Matcher

open class FileListScreen : BaseScreen() {

    @IdRes
    var recyclerView: Int = R.id.fileListRecycler

    @IdRes
    var targetView: Int = 0

    @IdRes
    var targetCheckBox: Int = 0

    fun scrollListToPosition(position: Int) {
        BaristaListInteractions.scrollListToPosition(recyclerView, position)
    }

    fun selectCheckboxOnPosition(position: Int) {
        CustomCheckboxAction.selectCheckboxOnRecyclerPosition(
            recyclerView,
            targetCheckBox,
            position
        )
    }

    fun clickOnBack() = clickOn(R.id.imageButtonBackArrow)

    fun clickOnItemInPosition(position: Int) =
        waitUntil { clickListItem(recyclerView, position) }

    open fun clickOnSelectFilesToAssociate() =
        waitUntil { clickOn(R.id.buttonSelectToAssociate) }

    fun clickOnAssociateWithAnOfficer() = waitUntil { clickOn(R.string.associate_with_an_officer) }

    fun clickOnButtonAssignToOfficer() = waitUntil { clickOn(R.id.buttonAssignToOfficer) }

    fun clickOnSimpleListButton() = clickOn(R.id.buttonSimpleList)

    open fun typeOfficerIdToAssociate(officerId: String) =
        waitUntil { writeTo(R.id.editTextAssignToOfficer, officerId) }

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

    fun clickOnRetry() = waitUntil { clickOn(R.string.retry) }

    fun checkFileEvent(event: String?) = event?.let { assertDisplayed(it) }

    fun reviewItemsCount(matcher: Matcher<Int>) {
        waitUntil { onView(ViewMatchers.withId(recyclerView)).check(RecyclerViewHelper(matcher)) }
    }

    fun matchItemsCount(count: Int) =
        waitUntil { assertRecyclerViewItemCount(recyclerView, count) }

    fun areCheckboxesUnselected(startPosition: Int, endPosition: Int) {
        for (position in (startPosition..endPosition)) {
            isCheckboxUnselected(position)
        }
    }

    private fun isFileDisplayedAtPosition(fileDate: String, position: Int) {
        assertDisplayedAtPosition(
            recyclerView,
            position,
            targetView,
            fileDate
        )
    }

    fun areFilesSortedByDate(filesList: List<CameraFile>) {
        val sortedFilesList = filesList.sortedByDescending { it.getCreationDate() }

        for ((itemCount, file) in sortedFilesList.withIndex()) {
            waitUntil { isFileDisplayedAtPosition(file.getCreationDate(), itemCount) }
        }
    }

    fun isCheckboxSelected(position: Int) {
        assertCustomAssertionAtPosition(
            recyclerView,
            position,
            targetCheckBox,
            ViewAssertions.matches(isActivated())
        )
    }

    fun isCheckboxUnselected(position: Int) {
        assertCustomAssertionAtPosition(
            recyclerView,
            position,
            targetCheckBox,
            ViewAssertions.matches(isNotActivated())
        )
    }

    fun isFileListDisplayed() = assertDisplayed(R.id.fileListRecycler)

    fun areNoFilesFound(@StringRes message: Int) =
        assertDisplayed(R.id.noFilesTextView, message)

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

    private fun isDateTimeHeaderDisplayed() =
        assertDisplayed(R.id.textViewDateAndTime, R.string.date_and_time)

    private fun isEventHeaderDisplayed() = assertDisplayed(R.id.textViewEvent, R.string.event)

    private fun isSnapshotsTitleDisplayed() = assertDisplayed(R.string.snapshots_title)

    fun isSelectDisplayed() =
        waitUntil { assertDisplayed(R.string.select) }

    fun isSelectVideosToAssociateDisplayed() =
        waitUntil { assertDisplayed(R.string.select_videos_to_associate) }

    fun isSelectSnapshotsToAssociateDisplayed() =
        waitUntil { assertDisplayed(R.string.select_snapshots_to_associate) }

    fun isVideosListScreenDisplayed() {
        isFilterButtonDisplayed()
        isDateTimeHeaderDisplayed()
        isEventHeaderDisplayed()
    }

    fun isSnapshotsListScreenDisplayed() {
        isFilterButtonDisplayed()
        isSnapshotsTitleDisplayed()
    }

    fun isAssociatePartnerSuccessMessageDisplayed() =
        waitUntil { assertDisplayed(R.string.file_list_associate_partner_id_success) }

    fun isOfficerAssociatedDisplayed(officer: String) = waitUntil { assertDisplayed(officer) }

    fun isRetryDisplayed(): Boolean {
        try {
            waitUntil { assertDisplayed(R.string.retry) }
        } catch (e: Exception) {
            return false
        }
        return true
    }
}
