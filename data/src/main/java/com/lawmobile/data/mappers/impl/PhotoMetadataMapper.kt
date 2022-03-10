package com.lawmobile.data.mappers.impl

import com.lawmobile.body_cameras.entities.PhotoInformation
import com.lawmobile.data.mappers.DomainMapper
import com.lawmobile.domain.entities.DomainMetadata
import com.lawmobile.domain.entities.DomainPhotoMetadata

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
