package com.lawmobile.domain.extensions

import com.lawmobile.domain.entities.DomainInformationVideo

fun DomainInformationVideo.getDurationMinutesString(): String {
    return duration.toLong().times(1000).milliSecondsToString()
}

fun DomainInformationVideo.getDurationMinutesLong(): Long {
    return duration.toLong().times(1000)
}
