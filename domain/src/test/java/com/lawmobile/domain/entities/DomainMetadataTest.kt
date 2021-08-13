package com.lawmobile.domain.entities

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class DomainMetadataTest {

    @Test
    fun hasAnyInformationFalse() {
        val metadata = DomainMetadata()
        assertFalse(metadata.hasAnyInformation())
    }

    @Test
    fun hasAnyInformationTrue() {
        val metadata = DomainMetadata(firstName = "officer name")
        assertTrue(metadata.hasAnyInformation())
    }

    @Test
    fun isDifferentFromTrue() {
        val metadata1 = DomainMetadata()
        val metadata2 = DomainMetadata(firstName = "officer name")
        assertTrue(metadata1.isDifferentFrom(metadata2))
    }

    @Test
    fun isDifferentFromFalse() {
        val metadata1 = DomainMetadata()
        val metadata2 = DomainMetadata()
        assertFalse(metadata1.isDifferentFrom(metadata2))
    }

    @Test
    fun convertNullParamsToEmpty() {
        val metadata = DomainMetadata()
        metadata.convertNullParamsToEmpty()
        assertTrue(metadata.partnerID?.isEmpty() == true)
        assertTrue(metadata.ticketNumber?.isEmpty() == true)
        assertTrue(metadata.ticketNumber2?.isEmpty() == true)
        assertTrue(metadata.caseNumber?.isEmpty() == true)
        assertTrue(metadata.caseNumber2?.isEmpty() == true)
        assertTrue(metadata.dispatchNumber?.isEmpty() == true)
        assertTrue(metadata.dispatchNumber2?.isEmpty() == true)
        assertTrue(metadata.location?.isEmpty() == true)
        assertTrue(metadata.remarks?.isEmpty() == true)
        assertTrue(metadata.firstName?.isEmpty() == true)
        assertTrue(metadata.lastName?.isEmpty() == true)
        assertTrue(metadata.driverLicense?.isEmpty() == true)
        assertTrue(metadata.licensePlate?.isEmpty() == true)
    }
}
