package com.lawmobile.data.mappers

import com.lawmobile.domain.entities.DomainCatalog
import com.safefleet.mobile.avml.cameras.entities.CameraConnectCatalog

object CatalogMapper {
    fun cameraToDomainList(cameraCatalogList: List<CameraConnectCatalog>) =
        cameraCatalogList.map {
            DomainCatalog(it.id, it.name, it.type)
        }

    fun cameraToDomain(cameraConnectCatalog: CameraConnectCatalog?) =
        cameraConnectCatalog?.run {
            DomainCatalog(id, name, type)
        }

    fun domainToCamera(domainCatalog: DomainCatalog?) =
        domainCatalog?.run {
            CameraConnectCatalog(id, name, type)
        }
}