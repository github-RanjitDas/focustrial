package com.lawmobile.data.dto.api.authorization

import com.lawmobile.data.dto.entities.AuthorizationEndpointsDto
import com.lawmobile.data.dto.entities.DiscoveryEndpointsDto
import io.ktor.client.HttpClient
import io.ktor.client.request.get

class AuthorizationApiImpl(
    private val httpClient: HttpClient
) : AuthorizationApi {

    override suspend fun getDiscoveryEndpoints(
        discoveryUrl: String
    ): DiscoveryEndpointsDto = httpClient.get(discoveryUrl)

    override suspend fun getAuthorizationEndpoints(
        safeFleetIdUrl: String
    ): AuthorizationEndpointsDto = httpClient.get(safeFleetIdUrl)
}
