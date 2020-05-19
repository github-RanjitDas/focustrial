package com.lawmobile.presentation.extensions

import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile

fun CameraConnectFile.getVideoStartTime(): String {
    val date: String = date.split(" ").toTypedArray()[0]
    val time = name.substring(0, 6)
    val hour = time.substring(0, 2)
    val min = time.substring(2, 4)
    val sec = time.substring(4, 6)

    return String.format("%s %s:%s:%s", date, hour, min, sec)
}