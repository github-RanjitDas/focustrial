package com.lawmobile.data.mappers

import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.entities.DomainHashVideo
import com.lawmobile.domain.entities.DomainMetadata
import com.lawmobile.domain.entities.DomainPhotoAssociated
import com.lawmobile.domain.entities.DomainVideoMetadata
import com.lawmobile.domain.enums.CameraType
import com.safefleet.mobile.external_hardware.cameras.entities.HashVideo
import com.safefleet.mobile.external_hardware.cameras.entities.PhotoAssociated
import com.safefleet.mobile.external_hardware.cameras.entities.VideoInformation
import com.safefleet.mobile.external_hardware.cameras.entities.VideoMetadata

object VideoMetadataMapper {
    fun cameraToDomain(videoInformation: VideoInformation): DomainVideoMetadata {
        val serialNumber = videoInformation.x1sn ?: videoInformation.x2sn
        return videoInformation.run {
            DomainVideoMetadata(
                fileName = fileName,
                metadata = cameraVideoMetadataToDomain(metadata),
                nameFolder = nameFolder,
                officerId = officerId,
                path = path,
                associatedPhotos = cameraPhotosAssociatedToDomain(photos),
                serialNumber = serialNumber,
                endTime = endTime,
                gmtOffset = gmtOffset,
                hash = hashRemoteToDomain(hash),
                preEvent = preEvent,
                startTime = startTime,
                videoSpecs = videoSpecs
            )
        }
    }

    fun domainToCamera(domainVideoMetadata: DomainVideoMetadata): VideoInformation {
        val videoInformation = domainVideoMetadata.run {
            VideoInformation(
                fileName = fileName,
                officerId = officerId,
                path = path,
                nameFolder = nameFolder,
                x1sn = null,
                metadata = domainVideoMetadataToRemote(metadata),
                photos = domainToCameraAssociatedPhotos(associatedPhotos),
                endTime = endTime,
                gmtOffset = gmtOffset,
                hash = domainHashToRemote(hash),
                preEvent = preEvent,
                startTime = startTime,
                videoSpecs = videoSpecs,
                x2sn = null
            )
        }
        when (CameraInfo.cameraType) {
            CameraType.X1 -> videoInformation.x1sn = domainVideoMetadata.serialNumber
            CameraType.X2 -> videoInformation.x2sn = domainVideoMetadata.serialNumber
        }
        return videoInformation
    }

    private fun cameraPhotosAssociatedToDomain(photos: List<PhotoAssociated>?) =
        photos?.map { DomainPhotoAssociated(it.date, it.name) }

    private fun cameraVideoMetadataToDomain(metadata: VideoMetadata?) =
        metadata?.run {
            DomainMetadata(
                caseNumber,
                caseNumber2,
                dispatchNumber,
                dispatchNumber2,
                driverLicense,
                CatalogMapper.cameraToDomain(event),
                firstName,
                gender,
                lastName,
                licensePlate,
                location,
                partnerID,
                race,
                remarks,
                ticketNumber,
                ticketNumber2
            )
        }

    private fun domainToCameraAssociatedPhotos(associatedPhotos: List<DomainPhotoAssociated>?) =
        associatedPhotos?.map { PhotoAssociated(it.name, it.date) }

    private fun domainVideoMetadataToRemote(metadata: DomainMetadata?) =
        metadata?.run {
            VideoMetadata(
                caseNumber,
                caseNumber2,
                dispatchNumber,
                dispatchNumber2,
                driverLicense,
                licensePlate,
                CatalogMapper.domainToCamera(event),
                firstName,
                gender,
                lastName,
                location,
                partnerID,
                race,
                remarks,
                ticketNumber,
                ticketNumber2
            )
        }

    private fun domainHashToRemote(hashDomain: DomainHashVideo?): HashVideo? {
        hashDomain?.let {
            return HashVideo(hashDomain.function, hashDomain.sums)
        } ?: return null
    }

    private fun hashRemoteToDomain(hash: HashVideo?): DomainHashVideo? {
        hash?.let {
            return DomainHashVideo(hash.function, hash.sums)
        } ?: return null
    }
}
