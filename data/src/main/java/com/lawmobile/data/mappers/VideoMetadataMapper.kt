package com.lawmobile.data.mappers

import com.lawmobile.domain.entities.DomainMetadata
import com.lawmobile.domain.entities.DomainPhotoAssociated
import com.lawmobile.domain.entities.DomainVideoMetadata
import com.safefleet.mobile.avml.cameras.entities.CameraConnectVideoMetadata
import com.safefleet.mobile.avml.cameras.entities.PhotoAssociated
import com.safefleet.mobile.avml.cameras.entities.VideoMetadata

object VideoMetadataMapper {
    fun cameraToDomain(cameraConnectVideoMetadata: CameraConnectVideoMetadata) =
        cameraConnectVideoMetadata.run {
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
            CameraConnectVideoMetadata(
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