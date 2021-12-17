package com.lawmobile.data.mappers.impl

import com.lawmobile.data.mappers.DomainMapper
import com.lawmobile.domain.entities.DomainAudioMetadata
import com.lawmobile.domain.entities.DomainMetadata
import com.safefleet.mobile.external_hardware.cameras.entities.AudioInformation

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
