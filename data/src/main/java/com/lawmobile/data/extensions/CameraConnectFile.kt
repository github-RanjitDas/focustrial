package com.lawmobile.data.extensions

import com.lawmobile.body_cameras.entities.CameraFile

fun CameraFile.getDateDependingOnNameLength(): String {
    return if (name.length >= 15) getCreationDateWhenSerial()
    else getCreationDateWhenSimple()
}

fun CameraFile.getCreationDateWhenSerial(): String {
    return try {
        val year = date.substring(0, 2) + nameFolder.substring(0, 2)
        val month = nameFolder.substring(2, 4)
        val day = nameFolder.substring(4, 6)

        val date = "$year-$month-$day"
        val time = name.split("-").lastOrNull()
        val hour = time?.substring(0, 2)
        val min = time?.substring(2, 4)
        val sec = time?.substring(4, 6)

        String.format("%s / %s:%s:%s", date, hour, min, sec)
    } catch (e: Exception) {
        date
    }
}

fun CameraFile.getCreationDateWhenSimple(): String {
    return try {
        val year = date.substring(0, 2) + nameFolder.substring(0, 2)
        val month = nameFolder.substring(2, 4)
        val day = nameFolder.substring(4, 6)

        val date = "$year-$month-$day"
        val time = name.substring(0, 6)
        val hour = time.substring(0, 2)
        val min = time.substring(2, 4)
        val sec = time.substring(4, 6)

        String.format("%s / %s:%s:%s", date, hour, min, sec)
    } catch (e: Exception) {
        date
    }
}
