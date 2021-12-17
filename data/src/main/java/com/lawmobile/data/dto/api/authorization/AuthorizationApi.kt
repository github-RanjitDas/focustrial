package com.lawmobile.data.dto.api.authorization

import com.lawmobile.data.dto.entities.AuthorizationEndpointsDto
import com.lawmobile.data.dto.entities.DiscoveryEndpointsDto

interface AuthorizationApi {
    suspend fun getDiscoveryEndpoints(discoveryUrl: String): DiscoveryEndpointsDto
    suspend fun getAuthorizationEndpoints(safeFleetIdUrl: String): AuthorizationEndpointsDto
}
