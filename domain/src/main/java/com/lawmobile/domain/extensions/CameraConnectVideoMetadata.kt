package com.lawmobile.domain.extensions

import com.safefleet.mobile.avml.cameras.entities.CameraConnectVideoMetadata

fun CameraConnectVideoMetadata.getCreationDate(): String {
    return try {
        val year = "20" + nameFolder?.substring(0, 2)
        val month = nameFolder?.substring(2, 4)
        val day = nameFolder?.substring(4, 6)

        val date = "$year-$month-$day"
        val time = fileName.substring(0, 6)
        val hour = time.substring(0, 2)
        val min = time.substring(2, 4)
        val sec = time.substring(4, 6)

        String.format("%s %s:%s:%s", date, hour, min, sec)
    } catch (e: Exception) {
        e.printStackTrace()
        return fileName
    }
}