package com.lawmobile.domain.extensions

fun String.simpleDateFormat(): String {
    return replace("'T'", " ").replace(".000Z", "")
}
