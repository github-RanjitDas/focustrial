package com.lawmobile.data.mappers

import com.lawmobile.domain.entities.DomainMetadata
import com.lawmobile.domain.entities.DomainPhotoAssociated
import com.lawmobile.domain.entities.DomainVideoMetadata
import com.safefleet.mobile.external_hardware.cameras.entities.PhotoAssociated
import com.safefleet.mobile.external_hardware.cameras.entities.VideoInformation
import com.safefleet.mobile.external_hardware.cameras.entities.VideoMetadata

object VideoMetadataMapper {
    fun cameraToDomain(videoInformation: VideoInformation) =
        videoInformation.run {
            DomainVideoMetadata(
                fileName,
                cameraVideoMetadataToDomain(metadata),
                nameFolder,
                officerId,
                path,
                cameraPhotosAssociatedToDomain(photos),
                x1sn
            )
        }

    fun domainToCamera(domainVideoMetadata: DomainVideoMetadata) =
        domainVideoMetadata.run {
            VideoInformation(
                fileName,
                officerId,
                path,
                nameFolder,
                serialNumber,
                domainVideoMetadataToRemote(metadata),
                domainToCameraAssociatedPhotos(associatedPhotos)
            )
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
}
