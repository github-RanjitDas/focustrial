package com.lawmobile.data.mappers

import com.lawmobile.domain.entities.DomainInformationVideo
import com.safefleet.mobile.avml.cameras.entities.CameraConnectVideoInfo

object VideoInformationMapper {
    fun cameraToDomain(cameraConnectVideoInfo: CameraConnectVideoInfo) =
        cameraConnectVideoInfo.run {
            DomainInformationVideo(
                size,
                date,
                duration,
                urlVideo
            )
        }

}