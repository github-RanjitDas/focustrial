package com.safefleet.lawmobile.screens

import com.safefleet.lawmobile.R
import com.safefleet.lawmobile.helpers.Alert
import com.safefleet.lawmobile.helpers.ToastMessage
import com.safefleet.mobile.avml.cameras.entities.CameraConnectVideoMetadata
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertNotDisplayed
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn
import com.schibsted.spain.barista.interaction.BaristaEditTextInteractions.writeTo

class VideoPlaybackScreen : BaseScreen() {

    fun clickOnSave() = clickOn(R.id.saveButtonVideoPlayback)
    fun clickOnBack() = clickOn(R.id.backArrowFileListAppBar)
    fun clickOnAccept() = clickOn(R.string.accept)

    fun selectEvent(data: CameraConnectVideoMetadata) {
        clickOn(R.id.eventValue)
        data.metadata?.event?.name?.let { clickOn(it) }
    }

    private fun selectGender(data: CameraConnectVideoMetadata) {
        clickOn(R.id.genderValue)
        data.metadata?.gender?.let { clickOn(it) }
    }

    private fun selectRace(data: CameraConnectVideoMetadata) {
        clickOn(R.id.raceValue)
        data.metadata?.race?.let { clickOn(it) }
    }

    fun typePartnerId(data: CameraConnectVideoMetadata) =
        data.officerId?.let { writeTo(R.id.partnerIdValue, it) }

    fun fillAllFields(data: CameraConnectVideoMetadata) {
        selectEvent(data)
        selectGender(data)
        selectRace(data)

        data.officerId?.let { writeTo(R.id.partnerIdValue, it) }
        data.metadata?.run {
            ticketNumber?.let { writeTo(R.id.ticket1Value, it) }
            ticketNumber2?.let { writeTo(R.id.ticket2Value, it) }
            caseNumber?.let { writeTo(R.id.case1Value, it) }
            caseNumber2?.let { writeTo(R.id.case2Value, it) }
            dispatchNumber?.let { writeTo(R.id.dispatch1Value, it) }
            dispatchNumber2?.let { writeTo(R.id.dispatch2Value, it) }
            location?.let { writeTo(R.id.locationValue, it) }
            remarks?.let { writeTo(R.id.notesValue, it) }
            firstName?.let { writeTo(R.id.firstNameValue, it) }
            lastName?.let { writeTo(R.id.lastNameValue, it) }
            driverLicense?.let { writeTo(R.id.driverLicenseValue, it) }
            licensePlate?.let { writeTo(R.id.licensePlateValue, it) }
        }
    }

    fun checkIfFieldsAreFilled() {
        assertNotDisplayed(R.id.eventValue, "Select")
        assertNotDisplayed(R.id.genderValue, "Select")
        assertNotDisplayed(R.id.raceValue, "Select")
        assertNotDisplayed(R.id.partnerIdValue, "")
        assertNotDisplayed(R.id.ticket2Value, "")
        assertNotDisplayed(R.id.case1Value, "")
        assertNotDisplayed(R.id.case2Value, "")
        assertNotDisplayed(R.id.dispatch1Value, "")
        assertNotDisplayed(R.id.dispatch2Value, "")
        assertNotDisplayed(R.id.locationValue, "")
        assertNotDisplayed(R.id.notesValue, "")
        assertNotDisplayed(R.id.firstNameValue, "")
        assertNotDisplayed(R.id.lastNameValue, "")
        assertNotDisplayed(R.id.driverLicenseValue, "")
        assertNotDisplayed(R.id.licensePlateValue, "")
    }

    fun isSavedSuccessDisplayed() =
        ToastMessage.isToastDisplayed(R.string.video_metadata_saved_success)

    fun isEventMandatoryDisplayed() =
        ToastMessage.isToastDisplayed(R.string.event_mandatory)

    fun isMetadataChangesAlertDisplayed() = Alert.isMetadataChangesDisplayed()
}