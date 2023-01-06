package com.lawmobile.data.mappers.impl

import com.lawmobile.body_cameras.entities.CameraCatalog
import com.lawmobile.data.mappers.CameraMapper
import com.lawmobile.data.mappers.DomainMapper
import com.lawmobile.domain.entities.MetadataEvent

object CatalogMapper :
    DomainMapper<CameraCatalog, MetadataEvent>,
    CameraMapper<MetadataEvent, CameraCatalog> {
    override fun CameraCatalog.toDomain(): MetadataEvent = MetadataEvent(id, name, type, order)
    override fun MetadataEvent.toCamera(): CameraCatalog = CameraCatalog(id, name, type, order)
    fun List<CameraCatalog>.toDomainList() = map { it.toDomain() }
}
