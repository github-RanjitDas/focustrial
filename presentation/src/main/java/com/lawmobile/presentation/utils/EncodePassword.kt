package com.lawmobile.presentation.utils

import android.util.Base64
import java.security.MessageDigest

object EncodePassword {
    fun encodePasswordOfficer(value: String): String {
        return try {
            val md: MessageDigest = MessageDigest.getInstance("SHA-256")
            val buffer = value.toByteArray()
            md.update(buffer)
            val md5Digest = md.digest()
            Base64.encodeToString(md5Digest, Base64.DEFAULT).replace("\n", "")
        } catch (e: Exception) {
            ""
        }
    }
}
