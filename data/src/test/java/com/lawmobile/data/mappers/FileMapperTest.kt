package com.lawmobile.data.mappers

import com.lawmobile.body_cameras.entities.CameraFile
import com.lawmobile.data.mappers.impl.FileMapper.toCamera
import com.lawmobile.data.mappers.impl.FileMapper.toCameraList
import com.lawmobile.data.mappers.impl.FileMapper.toDomain
import com.lawmobile.domain.entities.DomainCameraFile
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class FileMapperTest {

    @Test
    fun domainToCameraList() {
        val domainCameraList = listOf(mockk<DomainCameraFile>(relaxed = true))
        val cameraConnectList = domainCameraList.toCameraList()
        domainCameraList.forEachIndexed { index, it ->
            with(cameraConnectList[index]) {
                assertTrue(it.name == name)
                assertTrue(it.date == date)
                assertTrue(it.nameFolder == nameFolder)
                assertTrue(it.path == path)
            }
        }
    }

    @Test
    fun cameraToDomain() {
        val cameraConnectFile: CameraFile = mockk(relaxed = true)
        val domainCameraFile = cameraConnectFile.toDomain()
        with(cameraConnectFile) {
            domainCameraFile.let {
                assertTrue(it.name == name)
                assertTrue(it.date == date)
                assertTrue(it.nameFolder == nameFolder)
                assertTrue(it.path == path)
            }
        }
    }

    @Test
    fun domainToCamera() {
        val domainCameraFile: DomainCameraFile = mockk(relaxed = true)
        val cameraConnectFile = domainCameraFile.toCamera()
        with(domainCameraFile) {
            cameraConnectFile.let {
                assertTrue(it.name == name)
                assertTrue(it.date == date)
                assertTrue(it.nameFolder == nameFolder)
                assertTrue(it.path == path)
            }
        }
    }
}
