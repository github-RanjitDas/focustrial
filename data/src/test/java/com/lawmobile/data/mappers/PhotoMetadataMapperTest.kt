package com.lawmobile.data.mappers

import com.lawmobile.body_cameras.entities.PhotoInformation
import com.lawmobile.data.mappers.impl.PhotoMetadataMapper.toDomain
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class PhotoMetadataMapperTest {

    @Test
    fun cameraToDomain() {
        val photoInformation: PhotoInformation = mockk(relaxed = true)
        val domainPhotoMetadata = photoInformation.toDomain()
        with(photoInformation) {
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
