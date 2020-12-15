package com.lawmobile.data.mappers

import com.lawmobile.domain.entities.DomainCameraFile
import com.safefleet.mobile.external_hardware.cameras.entities.CameraFile
import io.mockk.mockk
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class FileMapperTest {

    @Test
    fun domainToCameraList() {
        val domainCameraList = listOf(mockk<DomainCameraFile>(relaxed = true))
        val cameraConnectList = FileMapper.domainToCameraList(domainCameraList)
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
        val domainCameraFile = FileMapper.cameraToDomain(cameraConnectFile)
        with(cameraConnectFile){
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
        val cameraConnectFile = FileMapper.domainToCamera(domainCameraFile)
        with(domainCameraFile){
            cameraConnectFile.let {
                assertTrue(it.name == name)
                assertTrue(it.date == date)
                assertTrue(it.nameFolder == nameFolder)
                assertTrue(it.path == path)
            }
        }
    }
}