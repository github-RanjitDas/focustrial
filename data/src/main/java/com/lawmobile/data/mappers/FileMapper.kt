package com.lawmobile.data.mappers

import com.lawmobile.domain.entities.DomainCameraFile
import com.safefleet.mobile.external_hardware.cameras.entities.CameraFile

object FileMapper {
    fun domainToCameraList(domainFileList: List<DomainCameraFile>) =
        domainFileList.map { domainToCamera(it) }

    fun cameraToDomain(cameraFile: CameraFile) =
        cameraFile.run {
            DomainCameraFile(date, name, nameFolder, path)
        }

    fun domainToCamera(domainCameraFile: DomainCameraFile) =
        domainCameraFile.run {
            CameraFile(name, date, path, nameFolder)
        }
}
