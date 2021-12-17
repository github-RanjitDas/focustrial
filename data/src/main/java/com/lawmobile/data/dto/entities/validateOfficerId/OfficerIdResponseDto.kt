package com.lawmobile.data.dto.entities.validateOfficerId

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OfficerIdResponseDto(
    @SerialName("isValidUser")
    val isValidUser: Boolean
)
