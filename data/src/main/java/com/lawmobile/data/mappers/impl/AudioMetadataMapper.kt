package com.lawmobile.data.mappers.impl

import com.lawmobile.body_cameras.entities.AudioInformation
import com.lawmobile.data.mappers.DomainMapper
import com.lawmobile.domain.entities.DomainAudioMetadata
import com.lawmobile.domain.entities.DomainMetadata

object AudioMetadataMapper : DomainMapper<AudioInformation, DomainAudioMetadata> {
    override fun AudioInformation.toDomain(): DomainAudioMetadata =
        DomainAudioMetadata(
            fileName,
            DomainMetadata(partnerID = metadata?.partnerID),
            nameFolder,
            officerId,
            path,
            x1sn
        )
}
