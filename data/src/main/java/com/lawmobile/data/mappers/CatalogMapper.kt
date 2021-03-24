package com.lawmobile.data.mappers

import com.lawmobile.domain.entities.MetadataEvent
import com.safefleet.mobile.external_hardware.cameras.entities.CameraCatalog

object CatalogMapper {
    fun cameraToDomainList(cameraCatalogList: List<CameraCatalog>) =
        cameraCatalogList.map {
            MetadataEvent(it.id, it.name, it.type)
        }

    fun cameraToDomain(cameraCatalog: CameraCatalog?) =
        cameraCatalog?.run {
            MetadataEvent(id, name, type)
        }

    fun domainToCamera(metadataEvent: MetadataEvent?) =
        metadataEvent?.run {
            CameraCatalog(id, name, type)
        }
}
