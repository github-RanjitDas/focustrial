package com.lawmobile.data.mappers.impl

import com.lawmobile.data.mappers.DomainMapper
import com.lawmobile.domain.entities.DomainInformationVideo
import com.safefleet.mobile.external_hardware.cameras.entities.VideoFileInfo

object VideoInformationMapper : DomainMapper<VideoFileInfo, DomainInformationVideo> {
    override fun VideoFileInfo.toDomain(): DomainInformationVideo =
        DomainInformationVideo(
            size,
            date,
            duration,
            urlVideo
        )
}
