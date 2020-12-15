package com.lawmobile.data.mappers

import com.lawmobile.domain.entities.DomainCatalog
import com.safefleet.mobile.external_hardware.cameras.entities.CameraCatalog

object CatalogMapper {
    fun cameraToDomainList(cameraCatalogList: List<CameraCatalog>) =
        cameraCatalogList.map {
            DomainCatalog(it.id, it.name, it.type)
        }

    fun cameraToDomain(cameraCatalog: CameraCatalog?) =
        cameraCatalog?.run {
            DomainCatalog(id, name, type)
        }

    fun domainToCamera(domainCatalog: DomainCatalog?) =
        domainCatalog?.run {
            CameraCatalog(id, name, type)
        }
}