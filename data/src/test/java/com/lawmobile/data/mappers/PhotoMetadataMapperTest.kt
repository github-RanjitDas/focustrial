package com.lawmobile.data.mappers

import com.safefleet.mobile.external_hardware.cameras.entities.PhotoInformation
import io.mockk.mockk
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class PhotoMetadataMapperTest {

    @Test
    fun cameraToDomain() {
        val photoInformation: PhotoInformation = mockk(relaxed = true)
        val domainPhotoMetadata = PhotoMetadataMapper.cameraToDomain(photoInformation)
        with(photoInformation){
            domainPhotoMetadata.let {
                assertTrue(it.fileName == fileName)
                assertTrue(it.metadata?.partnerID == metadata?.partnerID)
                assertTrue(it.nameFolder == nameFolder)
                assertTrue(it.officerId == officerId)
                assertTrue(it.path == path)
                assertTrue(it.serialNumber == x1sn)
            }
        }
    }
}