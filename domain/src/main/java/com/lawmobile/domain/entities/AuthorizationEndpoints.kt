package com.lawmobile.domain.entities

data class AuthorizationEndpoints(
    val authorizationEndpoint: String,
    val tokenEndpoint: String,
) {
    fun isEmpty() = authorizationEndpoint.isEmpty() || tokenEndpoint.isEmpty()
}
