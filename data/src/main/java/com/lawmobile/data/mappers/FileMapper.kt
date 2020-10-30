package com.lawmobile.data.mappers

import com.lawmobile.domain.entities.DomainCameraFile
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile

object FileMapper {
    fun domainToCameraList(domainFileList: List<DomainCameraFile>) =
        domainFileList.map {
            CameraConnectFile(
                it.name,
                it.date,
                it.path,
                it.nameFolder
            )
        }

    fun cameraToDomain(cameraConnectFile: CameraConnectFile) =
        cameraConnectFile.run {
            DomainCameraFile(date, name, nameFolder, path)
        }

    fun domainToCamera(domainCameraFile: DomainCameraFile) =
        domainCameraFile.run {
            CameraConnectFile(name, date, path, nameFolder)
        }
}