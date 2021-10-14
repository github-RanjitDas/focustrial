package com.lawmobile.data.mappers

import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.entities.DomainAnnotations
import com.lawmobile.domain.entities.DomainAssociatedFile
import com.lawmobile.domain.entities.DomainHashVideo
import com.lawmobile.domain.entities.DomainMetadata
import com.lawmobile.domain.entities.DomainVideoMetadata
import com.lawmobile.domain.enums.CameraType
import com.safefleet.mobile.external_hardware.cameras.entities.Annotation
import com.safefleet.mobile.external_hardware.cameras.entities.AssociatedFile
import com.safefleet.mobile.external_hardware.cameras.entities.HashMetadataFile
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
                associatedFiles = cameraPhotosAssociatedToDomain(associatedFiles),
                annotations = cameraAnnotationsToDomain(annotations),
                serialNumber = serialNumber,
                endTime = endTime,
                gmtOffset = gmtOffset,
                hash = hashRemoteToDomain(hash),
                preEvent = preEvent,
                startTime = startTime,
                videoSpecs = videoSpecs,
                trigger = trigger,
                x2sn = x2sn,
                x1sn = x1sn
            )
        }
    }

    private fun cameraAnnotationsToDomain(annotations: List<Annotation>?) =
        annotations?.map { DomainAnnotations(it.type, it.timestamp) }

    fun domainToCamera(domainVideoMetadata: DomainVideoMetadata): VideoInformation {
        val videoInformation = domainVideoMetadata.run {
            VideoInformation(
                fileName = fileName,
                officerId = officerId,
                path = path,
                nameFolder = nameFolder,
                x1sn = x1sn,
                metadata = domainVideoMetadataToRemote(metadata),
                associatedFiles = domainToCameraAssociatedPhotos(associatedFiles),
                annotations = domainToCameraAnnotations(annotations),
                endTime = endTime,
                gmtOffset = gmtOffset,
                hash = domainHashToRemote(hash),
                preEvent = preEvent,
                startTime = startTime,
                videoSpecs = videoSpecs,
                x2sn = x2sn,
                trigger = trigger
            )
        }
        when (CameraInfo.cameraType) {
            CameraType.X1 -> {
                if (videoInformation.x1sn.isNullOrEmpty()) videoInformation.x1sn = domainVideoMetadata.serialNumber
            }
            CameraType.X2 -> {
                if (videoInformation.x2sn.isNullOrEmpty()) videoInformation.x2sn = domainVideoMetadata.serialNumber
            }
        }
        return videoInformation
    }

    private fun domainToCameraAnnotations(annotations: List<DomainAnnotations>?) =
        annotations?.map { Annotation(it.type, it.timestamp) }

    private fun cameraPhotosAssociatedToDomain(photos: List<AssociatedFile>?) =
        photos?.map { DomainAssociatedFile(it.date, it.name) }

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

    private fun domainToCameraAssociatedPhotos(associatedFiles: List<DomainAssociatedFile>?) =
        associatedFiles?.map { AssociatedFile(it.name, it.date) }

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

    private fun domainHashToRemote(hashDomain: DomainHashVideo?): HashMetadataFile? =
        hashDomain?.run { HashMetadataFile(function, sums) }

    private fun hashRemoteToDomain(hash: HashMetadataFile?): DomainHashVideo? =
        hash?.run { DomainHashVideo(function, sums) }
}
