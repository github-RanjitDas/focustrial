package com.lawmobile.body_cameras.entities

data class VideoMetadata(
    var caseNumber: String? = null,
    var caseNumber2: String? = null,
    var dispatchNumber: String? = null,
    var dispatchNumber2: String? = null,
    var driverLicense: String? = null,
    var licensePlate: String? = null,
    var event: CameraCatalog? = null,
    var firstName: String? = null,
    var gender: String? = null,
    var lastName: String? = null,
    var location: String? = null,
    var partnerID: String? = null,
    var race: String? = null,
    var remarks: String? = null,
    var ticketNumber: String? = null,
    var ticketNumber2: String? = null
)
