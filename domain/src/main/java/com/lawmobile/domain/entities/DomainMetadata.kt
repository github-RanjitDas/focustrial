package com.lawmobile.domain.entities

data class DomainMetadata(
    var caseNumber: String? = null,
    var caseNumber2: String? = null,
    var dispatchNumber: String? = null,
    var dispatchNumber2: String? = null,
    var driverLicense: String? = null,
    var event: MetadataEvent? = null,
    var firstName: String? = null,
    var gender: String? = null,
    var lastName: String? = null,
    var licensePlate: String? = null,
    var location: String? = null,
    var partnerID: String? = null,
    var race: String? = null,
    var remarks: String? = null,
    var ticketNumber: String? = null,
    var ticketNumber2: String? = null
) {
    fun hasAnyInformation(): Boolean {
        return !event?.id.isNullOrEmpty() ||
            !event?.name.isNullOrEmpty() ||
            !event?.type.isNullOrEmpty() ||
            !partnerID.isNullOrEmpty() ||
            !ticketNumber.isNullOrEmpty() ||
            !ticketNumber2.isNullOrEmpty() ||
            !caseNumber.isNullOrEmpty() ||
            !caseNumber2.isNullOrEmpty() ||
            !dispatchNumber.isNullOrEmpty() ||
            !dispatchNumber2.isNullOrEmpty() ||
            !location.isNullOrEmpty() ||
            !remarks.isNullOrEmpty() ||
            !firstName.isNullOrEmpty() ||
            !lastName.isNullOrEmpty() ||
            !driverLicense.isNullOrEmpty() ||
            !licensePlate.isNullOrEmpty() ||
            !gender.isNullOrEmpty() ||
            !race.isNullOrEmpty()
    }

    fun isDifferentFrom(metadata: DomainMetadata): Boolean {
        val eventName = CameraInfo.metadataEvents.map { it.name }.find { it == metadata.event?.name }
        val isEventOnCamera = eventName != null
        val isEventDifferent = if (isEventOnCamera) metadata.event?.name != event?.name else false

        return isEventDifferent ||
            metadata.partnerID != partnerID ||
            metadata.ticketNumber != ticketNumber ||
            metadata.ticketNumber2 != ticketNumber2 ||
            metadata.caseNumber != caseNumber ||
            metadata.caseNumber2 != caseNumber2 ||
            metadata.dispatchNumber != dispatchNumber ||
            metadata.dispatchNumber2 != dispatchNumber2 ||
            metadata.location != location ||
            metadata.remarks != remarks ||
            metadata.firstName != firstName ||
            metadata.lastName != lastName ||
            metadata.driverLicense != driverLicense ||
            metadata.licensePlate != licensePlate ||
            metadata.gender != gender ||
            metadata.race != race
    }

    fun convertNullParamsToEmpty(): DomainMetadata {
        event = handleNullEvent(event)
        event?.id = handleNullParameter(event?.id)
        event?.name = handleNullParameter(event?.name)
        event?.type = handleNullParameter(event?.type)
        partnerID = handleNullParameter(partnerID)
        ticketNumber = handleNullParameter(ticketNumber)
        ticketNumber2 = handleNullParameter(ticketNumber2)
        caseNumber = handleNullParameter(caseNumber)
        caseNumber2 = handleNullParameter(caseNumber2)
        dispatchNumber = handleNullParameter(dispatchNumber)
        dispatchNumber2 = handleNullParameter(dispatchNumber2)
        location = handleNullParameter(location)
        remarks = handleNullParameter(remarks)
        firstName = handleNullParameter(firstName)
        lastName = handleNullParameter(lastName)
        driverLicense = handleNullParameter(driverLicense)
        licensePlate = handleNullParameter(licensePlate)
        gender = handleNullParameter(gender)
        race = handleNullParameter(race)
        return this
    }

    private fun handleNullEvent(event: MetadataEvent?) = event ?: MetadataEvent("", "", "")

    private fun handleNullParameter(string: String?) = string ?: ""
}
