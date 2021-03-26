package com.lawmobile.database.mappers

import com.lawmobile.data.dao.entities.LocalCameraEvent
import com.lawmobile.database.DbCameraEvent

object DbEventsMapper {

    fun dbToLocalList(events: List<DbCameraEvent>) =
        events.map {
            dbToLocal(it)
        }

    fun dbToLocal(event: DbCameraEvent) =
        event.run {
            LocalCameraEvent(id, name, eventType, eventTag, value, date, isRead)
        }
}
