package com.safefleet.lawmobile.screens

import com.lawmobile.presentation.utils.EspressoIdlingResource
import com.safefleet.lawmobile.R
import com.schibsted.spain.barista.assertion.BaristaImageViewAssertions
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn

class AssociateSnapshotsScreen : BaseScreen() {

    fun clickOnSimpleListButton() {
        clickOn(R.id.buttonSimpleListAssociate)
    }

    fun clickOnAssociateSnapshots() {
        clickOn(R.id.buttonAssociateFiles)
    }

    fun isAssociateScreenButtonsDisplayed() {
        assertDisplayed(R.id.buttonThumbnailListAssociate)
        assertDisplayed(R.id.buttonSimpleListAssociate)
        assertDisplayed(R.id.buttonFilterFiles)
        isFilterNotActive()
    }

    fun clickOnClose() {
        clickOn(R.id.buttonCloseAssociateFiles)
    }

    fun clickOnFilter() {
        clickOn(R.id.buttonFilterFiles)
    }

    fun isSnapshotsAddedSuccessDisplayed() {
        assertDisplayed(R.string.snapshots_added_success)
        EspressoIdlingResource.increment()
    }

    fun isNoSnapshotsSelectedDisplayed() {
        assertDisplayed(R.string.no_new_photo_selected_message)
        EspressoIdlingResource.increment()
    }

    fun isFilterActive() {
        assertDisplayed(R.id.scrollFilterAssociateTags)
        BaristaImageViewAssertions.assertHasDrawable(
            R.id.buttonFilterFiles,
            R.drawable.ic_filter
        )
    }

    private fun isFilterNotActive() {
        BaristaVisibilityAssertions.assertNotDisplayed(R.id.scrollFilterAssociateTags)
        BaristaImageViewAssertions.assertHasDrawable(
            R.id.buttonFilterFiles,
            R.drawable.ic_filter_white
        )
    }
}
