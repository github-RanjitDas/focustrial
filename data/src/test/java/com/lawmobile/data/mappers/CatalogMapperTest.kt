package com.lawmobile.data.mappers

import com.lawmobile.data.mappers.impl.CatalogMapper.toCamera
import com.lawmobile.data.mappers.impl.CatalogMapper.toDomain
import com.lawmobile.data.mappers.impl.CatalogMapper.toDomainList
import com.lawmobile.domain.entities.MetadataEvent
import com.safefleet.mobile.external_hardware.cameras.entities.CameraCatalog
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class CatalogMapperTest {

    @Test
    fun cameraToDomainList() {
        val cameraCatalogList =
            listOf<CameraCatalog>(mockk(relaxed = true), mockk(relaxed = true))
        val domainCatalogList = cameraCatalogList.toDomainList()
        with(cameraCatalogList) {
            domainCatalogList.forEachIndexed { index, it ->
                assertTrue(it.id == get(index).id)
                assertTrue(it.name == get(index).name)
                assertTrue(it.type == get(index).type)
            }
        }

        assertFalse(domainCatalogList.isEmpty())
    }

    @Test
    fun cameraToDomainListEmpty() {
        val cameraCatalogList = emptyList<CameraCatalog>()
        val domainCatalogList = cameraCatalogList.toDomainList()
        assertTrue(domainCatalogList.isEmpty())
    }

    @Test
    fun cameraToDomain() {
        val cameraConnectCatalog: CameraCatalog = mockk(relaxed = true)
        val domainCatalog = cameraConnectCatalog.toDomain()
        with(cameraConnectCatalog) {
            domainCatalog.let {
                assertTrue(it.id == id)
                assertTrue(it.name == name)
                assertTrue(it.type == type)
            }
        }
    }

    @Test
    fun domainToCamera() {
        val metadataEvent: MetadataEvent = mockk(relaxed = true)
        val cameraConnectCatalog = metadataEvent.toCamera()
        with(metadataEvent) {
            cameraConnectCatalog.let {
                assertTrue(it.id == id)
                assertTrue(it.name == name)
                assertTrue(it.type == type)
            }
        }
    }
}
