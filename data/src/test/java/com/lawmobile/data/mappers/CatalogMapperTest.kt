package com.lawmobile.data.mappers

import com.lawmobile.domain.entities.DomainCatalog
import com.safefleet.mobile.avml.cameras.entities.CameraConnectCatalog
import io.mockk.mockk
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class CatalogMapperTest {

    @Test
    fun cameraToDomainList() {
        val cameraCatalogList =
            listOf<CameraConnectCatalog>(mockk(relaxed = true), mockk(relaxed = true))
        val domainCatalogList = CatalogMapper.cameraToDomainList(cameraCatalogList)
        with(cameraCatalogList){
            domainCatalogList.forEachIndexed { index, it ->
                assertTrue(it.id == get(index).id)
                assertTrue(it.name == get(index).name)
                assertTrue(it.type == get(index).type)
            }
        }
    }

    @Test
    fun cameraToDomain() {
        val cameraConnectCatalog: CameraConnectCatalog = mockk(relaxed = true)
        val domainCatalog = CatalogMapper.cameraToDomain(cameraConnectCatalog)
        with(cameraConnectCatalog){
            domainCatalog.let {
                assertTrue(it?.id == id)
                assertTrue(it?.name == name)
                assertTrue(it?.type == type)
            }
        }
    }

    @Test
    fun domainToCamera() {
        val domainCatalog: DomainCatalog = mockk(relaxed = true)
        val cameraConnectCatalog = CatalogMapper.domainToCamera(domainCatalog)
        with(domainCatalog){
            cameraConnectCatalog.let {
                assertTrue(it?.id == id)
                assertTrue(it?.name == name)
                assertTrue(it?.type == type)
            }
        }
    }
}