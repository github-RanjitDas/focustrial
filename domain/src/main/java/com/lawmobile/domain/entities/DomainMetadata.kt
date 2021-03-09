package com.lawmobile.domain.entities

data class DomainMetadata (
    var caseNumber: String? = null,
    var caseNumber2: String? = null,
    var dispatchNumber: String? = null,
    var dispatchNumber2: String? = null,
    var driverLicense: String? = null,
    val event: DomainCatalog? = null,
    var firstName: String? = null,
    val gender: String? = null,
    var lastName: String? = null,
    var licensePlate: String? = null,
    var location: String? = null,
    var partnerID: String? = null,
    val race: String? = null,
    var remarks: String? = null,
    var ticketNumber: String? = null,
    var ticketNumber2: String? = null
)