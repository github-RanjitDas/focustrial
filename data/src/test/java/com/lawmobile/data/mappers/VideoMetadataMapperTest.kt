package com.lawmobile.data.mappers

import com.lawmobile.domain.entities.DomainPhotoAssociated
import com.lawmobile.domain.entities.DomainVideoMetadata
import com.safefleet.mobile.avml.cameras.entities.CameraConnectVideoMetadata
import com.safefleet.mobile.avml.cameras.entities.PhotoAssociated
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class VideoMetadataMapperTest {

    @Test
    fun cameraToDomain() {
        val cameraConnectVideoMetadata: CameraConnectVideoMetadata = mockk(relaxed = true){
            every { photos } returns listOf(
                PhotoAssociated("3", ""),
                PhotoAssociated("1", ""),
                PhotoAssociated("5", ""),
            )
        }
        val domainVideoMetadata = VideoMetadataMapper.cameraToDomain(cameraConnectVideoMetadata)
        with(cameraConnectVideoMetadata){
            domainVideoMetadata.let {
                assertTrue(it.fileName == fileName)
                assertTrue(it.metadata?.partnerID == metadata?.partnerID)
                assertTrue(it.nameFolder == nameFolder)
                assertTrue(it.officerId == officerId)
                assertTrue(it.path == path)
                assertTrue(it.serialNumber == x1sn)
                it.associatedPhotos?.forEachIndexed { index, photo ->
                    assertTrue(photo.name == photos?.get(index)?.name)
                    assertTrue(photo.date == photos?.get(index)?.date)
                }
            }
        }
    }

    @Test
    fun domainToCamera() {
        val domainVideoMetadata: DomainVideoMetadata = mockk(relaxed = true){
            every { associatedPhotos } returns listOf(
                DomainPhotoAssociated("", "3"),
                DomainPhotoAssociated("", "1"),
                DomainPhotoAssociated("", "5"),
            )
        }
        val cameraConnectVideoMetadata = VideoMetadataMapper.domainToCamera(domainVideoMetadata)
        with(cameraConnectVideoMetadata){
            domainVideoMetadata.let {
                assertTrue(it.fileName == fileName)
                assertTrue(it.metadata?.partnerID == metadata?.partnerID)
                assertTrue(it.nameFolder == nameFolder)
                assertTrue(it.officerId == officerId)
                assertTrue(it.path == path)
                assertTrue(it.serialNumber == x1sn)
                it.associatedPhotos?.forEachIndexed { index, photo ->
                    assertTrue(photo.name == photos?.get(index)?.name)
                    assertTrue(photo.date == photos?.get(index)?.date)
                }
            }
        }
    }
}