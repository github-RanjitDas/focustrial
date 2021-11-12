package com.lawmobile.data.dto.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    @SerialName("id")
    val uuid: String,
    @SerialName("email")
    val email: String,
    @SerialName("devicePassword")
    val devicePassword: String?
)
