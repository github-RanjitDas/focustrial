package com.lawmobile.database.mappers

import com.lawmobile.data.dao.entities.LocalCameraEvent
import com.lawmobile.data.mappers.LocalMapper
import com.lawmobile.database.DbCameraEvent

object DbEventsMapper : LocalMapper<DbCameraEvent, LocalCameraEvent> {
    override fun DbCameraEvent.toLocal(): LocalCameraEvent =
        LocalCameraEvent(id, name, eventType, eventTag, value, date, isRead)
    fun List<DbCameraEvent>.toLocalList() = map { it.toLocal() }
}
