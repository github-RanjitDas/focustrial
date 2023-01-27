package com.lawmobile.domain.entities

data class VideoDetailMetaDataCached(
    val eventPosition: Int,
    val partnerIdValue: String,
    val ticket1Value: String,
    val ticket2Value: String,
    val case1Value: String,
    val case2Value: String,
    val dispatch1Value: String,
    val dispatch2Value: String,
    val locationValue: String,
    val firstNameValue: String,
    val lastNameValue: String,
    val genderPosition: Int,
    val racePosition: Int,
    val driverLicenseValue: String,
    val licensePlateValue: String,
    val notesValue: String,
    val playerProgress: Int = 0
)
