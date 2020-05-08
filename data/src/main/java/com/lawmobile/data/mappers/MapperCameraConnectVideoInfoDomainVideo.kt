package com.lawmobile.data.mappers

import com.lawmobile.domain.entity.DomainInformationVideo
import com.safefleet.mobile.avml.cameras.entities.CameraConnectVideoInfo

object MapperCameraConnectVideoInfoDomainVideo {
    fun cameraConnectFileToDomainInformationVideo(cameraConnectVideoInfo: CameraConnectVideoInfo): DomainInformationVideo {
        return DomainInformationVideo(
            cameraConnectVideoInfo.size,
            cameraConnectVideoInfo.date,
            cameraConnectVideoInfo.duration,
            cameraConnectVideoInfo.urlVideo
        )
    }
}