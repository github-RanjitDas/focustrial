package com.lawmobile.data.mappers

import com.lawmobile.domain.entities.DomainMetadata
import com.lawmobile.domain.entities.DomainPhotoMetadata
import com.safefleet.mobile.avml.cameras.entities.CameraConnectPhotoMetadata

object PhotoMetadataMapper {
    fun cameraToDomain(cameraConnectPhotoMetadata: CameraConnectPhotoMetadata) =
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