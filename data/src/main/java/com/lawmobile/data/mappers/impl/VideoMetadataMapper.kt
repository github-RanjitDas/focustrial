package com.lawmobile.data.mappers.impl

import com.lawmobile.data.mappers.CameraMapper
import com.lawmobile.data.mappers.DomainMapper
import com.lawmobile.data.mappers.impl.CatalogMapper.toCamera
import com.lawmobile.data.mappers.impl.CatalogMapper.toDomain
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.entities.DomainAssociatedFile
import com.lawmobile.domain.entities.DomainHashVideo
import com.lawmobile.domain.entities.DomainMetadata
import com.lawmobile.domain.entities.DomainVideoMetadata
import com.lawmobile.domain.enums.CameraType
import com.safefleet.mobile.external_hardware.cameras.entities.AssociatedFile
import com.safefleet.mobile.external_hardware.cameras.entities.HashMetadataFile
import com.safefleet.mobile.external_hardware.cameras.entities.VideoInformation
import com.safefleet.mobile.external_hardware.cameras.entities.VideoMetadata

object VideoMetadataMapper :
    DomainMapper<VideoInformation, DomainVideoMetadata>,
    CameraMapper<DomainVideoMetadata, VideoInformation> {

    override fun VideoInformation.toDomain(): DomainVideoMetadata {
        val serialNumber = x1sn ?: x2sn
        return DomainVideoMetadata(
            fileName = fileName,
            metadata = metadata?.toDomain(),
            nameFolder = nameFolder,
            officerId = officerId,
            path = path,
            associatedFiles = associatedFiles?.toDomainList(),
            serialNumber = serialNumber,
            endTime = endTime,
            gmtOffset = gmtOffset,
            hash = hash?.toDomain(),
            preEvent = preEvent,
            startTime = startTime,
            videoSpecs = videoSpecs,
            trigger = trigger,
            x2sn = x2sn,
            x1sn = x1sn
        )
    }

    override fun DomainVideoMetadata.toCamera(): VideoInformation {
        val videoInformation = VideoInformation(
            fileName = fileName,
            officerId = officerId,
            path = path,
            nameFolder = nameFolder,
            x1sn = x1sn,
            metadata = metadata?.toCamera(),
            associatedFiles = associatedFiles?.toCameraList(),
            endTime = endTime,
            gmtOffset = gmtOffset,
            hash = hash?.toCamera(),
            preEvent = preEvent,
            startTime = startTime,
            videoSpecs = videoSpecs,
            x2sn = x2sn,
            trigger = trigger
        )

        when (CameraInfo.cameraType) {
            CameraType.X1 -> {
                if (videoInformation.x1sn.isNullOrEmpty()) videoInformation.x1sn = serialNumber
            }
            CameraType.X2 -> {
                if (videoInformation.x2sn.isNullOrEmpty()) videoInformation.x2sn = serialNumber
            }
        }

        return videoInformation
    }

    private fun List<AssociatedFile>.toDomainList() = map { DomainAssociatedFile(it.date, it.name) }

    private fun VideoMetadata.toDomain() =
        DomainMetadata(
            caseNumber,
            caseNumber2,
            dispatchNumber,
            dispatchNumber2,
            driverLicense,
            event?.toDomain(),
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

    private fun List<DomainAssociatedFile>.toCameraList() = map { AssociatedFile(it.name, it.date) }

    private fun DomainMetadata.toCamera() =
        VideoMetadata(
            caseNumber,
            caseNumber2,
            dispatchNumber,
            dispatchNumber2,
            driverLicense,
            licensePlate,
            event?.toCamera(),
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

    private fun DomainHashVideo.toCamera() = HashMetadataFile(function, sums)
    private fun HashMetadataFile.toDomain() = DomainHashVideo(function, sums)
}
