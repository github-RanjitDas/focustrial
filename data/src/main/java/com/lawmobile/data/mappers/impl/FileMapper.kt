package com.lawmobile.data.mappers.impl

import com.lawmobile.body_cameras.entities.CameraFile
import com.lawmobile.data.mappers.CameraMapper
import com.lawmobile.data.mappers.DomainMapper
import com.lawmobile.domain.entities.DomainCameraFile

object FileMapper :
    DomainMapper<CameraFile, DomainCameraFile>,
    CameraMapper<DomainCameraFile, CameraFile> {
    override fun CameraFile.toDomain() = DomainCameraFile(date, name, nameFolder, path)
    override fun DomainCameraFile.toCamera() = CameraFile(name, date, path, nameFolder)
    fun List<DomainCameraFile>.toCameraList(): List<CameraFile> = map { it.toCamera() }
    fun List<CameraFile>.toDomainList(): List<DomainCameraFile> = map { it.toDomain() }
}
