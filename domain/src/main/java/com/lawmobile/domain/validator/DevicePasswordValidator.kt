package com.lawmobile.domain.validator

object DevicePasswordValidator {
    operator fun invoke(password: String?): String {
        if (password.isNullOrEmpty())
            throw Exception("The user does not have a device password registered")
        else return password
    }
}
