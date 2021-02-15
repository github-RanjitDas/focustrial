package com.lawmobile.data.mappers

import com.lawmobile.domain.entities.DomainInformationVideo
import com.safefleet.mobile.external_hardware.cameras.entities.VideoFileInfo

object VideoInformationMapper {
    fun cameraToDomain(cameraConnectVideoInfo: VideoFileInfo) =
        cameraConnectVideoInfo.run {
            DomainInformationVideo(
                size,
                date,
                duration,
                urlVideo
            )
        }
}
