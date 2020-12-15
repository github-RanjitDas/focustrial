package com.lawmobile.data.mappers

import com.lawmobile.domain.entities.DomainMetadata
import com.lawmobile.domain.entities.DomainPhotoMetadata
import com.safefleet.mobile.external_hardware.cameras.entities.PhotoInformation

object PhotoMetadataMapper {
    fun cameraToDomain(cameraConnectPhotoMetadata: PhotoInformation) =
        cameraConnectPhotoMetadata.run {
            DomainPhotoMetadata(
                fileName,
                DomainMetadata(partnerID = metadata?.partnerID),
                nameFolder,
                officerId,
                path,
                x1sn
            )
        }
}