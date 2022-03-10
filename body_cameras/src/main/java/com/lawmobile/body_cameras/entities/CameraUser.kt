package com.lawmobile.body_cameras.entities

import com.safefleet.mobile.kotlin_commons.helpers.Result

class CameraUser(val officerId: String, val name: String, val password: String) {
    companion object {
        private const val OFFICER_ID_POSITION = 0
        private const val NAME_POSITION = 1
        private const val PASSWORD_POSITION = 2

        fun buildFromString(string: String): Result<CameraUser> {
            return try {
                println("User response string: $string")
                val params = string.replace("\n", "").replace("\r", "").split(",")
                Result.Success(
                    CameraUser(
                        params[OFFICER_ID_POSITION],
                        params[NAME_POSITION],
                        params[PASSWORD_POSITION]
                    )
                )
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }
}
