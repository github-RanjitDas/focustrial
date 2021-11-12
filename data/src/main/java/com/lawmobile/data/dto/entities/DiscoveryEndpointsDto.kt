package com.lawmobile.data.dto.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DiscoveryEndpointsDto(
    @SerialName("sf_id_configuration")
    val sfIdConfiguration: String,
    @SerialName("users_endpoint")
    val usersEndpoint: String
)
