package com.lawmobile.data.dto.entities.discoveryUrl

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthorizationEndpointsResponseDto(

    @SerialName("authorization_endpoint")
    val authorizationEndpoint: String,

    @SerialName("token_endpoint")
    val tokenEndpoint: String,

)
