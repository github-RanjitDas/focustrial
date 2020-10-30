package com.lawmobile.data.mappers

import com.safefleet.mobile.avml.cameras.entities.CameraConnectPhotoMetadata
import io.mockk.mockk
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class PhotoMetadataMapperTest {

    @Test
    fun cameraToDomain() {
        val cameraConnectPhotoMetadata: CameraConnectPhotoMetadata = mockk(relaxed = true)
        val domainPhotoMetadata = PhotoMetadataMapper.cameraToDomain(cameraConnectPhotoMetadata)
        with(cameraConnectPhotoMetadata){
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