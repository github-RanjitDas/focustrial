package com.lawmobile.data.mappers.impl

import com.lawmobile.data.mappers.DomainMapper
import com.lawmobile.domain.entities.DomainMetadata
import com.lawmobile.domain.entities.DomainPhotoMetadata
import com.safefleet.mobile.external_hardware.cameras.entities.PhotoInformation

object PhotoMetadataMapper : DomainMapper<PhotoInformation, DomainPhotoMetadata> {
    override fun PhotoInformation.toDomain(): DomainPhotoMetadata =
        DomainPhotoMetadata(
            fileName,
            DomainMetadata(partnerID = metadata?.partnerID),
            nameFolder,
            officerId,
            path,
            x1sn
        )
}
