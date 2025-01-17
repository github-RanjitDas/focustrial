package com.lawmobile.domain.extensions

import com.lawmobile.domain.entities.DomainVideoMetadata

fun DomainVideoMetadata.getDateDependingOnNameLength(): String {
    return if (fileName.length >= 15) getCreationDateWhenSerial()
    else getCreationDateWhenSimple()
}

fun DomainVideoMetadata.getCreationDateWhenSerial(): String {
    return try {
        val year = "20" + nameFolder?.substring(0, 2)
        val month = nameFolder?.substring(2, 4)
        val day = nameFolder?.substring(4, 6)

        val date = "$year-$month-$day"
        val time = fileName.split("-").lastOrNull()
        val hour = time?.substring(0, 2)
        val min = time?.substring(2, 4)
        val sec = time?.substring(4, 6)

        String.format("%s %s:%s:%s", date, hour, min, sec)
    } catch (e: Exception) {
        e.printStackTrace()
        fileName
    }
}

fun DomainVideoMetadata.getCreationDateWhenSimple(): String {
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
        fileName
    }
}
