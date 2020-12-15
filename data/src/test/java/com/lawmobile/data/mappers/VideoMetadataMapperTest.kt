package com.lawmobile.data.mappers

import com.lawmobile.domain.entities.DomainPhotoAssociated
import com.lawmobile.domain.entities.DomainVideoMetadata
import com.safefleet.mobile.external_hardware.cameras.entities.PhotoAssociated
import com.safefleet.mobile.external_hardware.cameras.entities.VideoInformation
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class VideoMetadataMapperTest {

    @Test
    fun cameraToDomain() {
        val videoInformation: VideoInformation = mockk(relaxed = true){
            every { photos } returns listOf(
                PhotoAssociated("3", ""),
                PhotoAssociated("1", ""),
                PhotoAssociated("5", ""),
            )
        }
        val domainVideoMetadata = VideoMetadataMapper.cameraToDomain(videoInformation)
        with(videoInformation){
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