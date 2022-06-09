package com.lawmobile.body_cameras.extensions

import java.math.BigInteger
import java.security.MessageDigest

fun ByteArray.toMD5(): String {
    val md = MessageDigest.getInstance("MD5")
    return BigInteger(1, md.digest(this)).toString(16).padStart(32, '0')
}
