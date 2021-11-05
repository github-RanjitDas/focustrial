package com.lawmobile.data.dto.entities.discoveryUrl

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DiscoveryResponseDto(
    @SerialName("sso_discovery")
    val ssoDiscovery: String,
)
