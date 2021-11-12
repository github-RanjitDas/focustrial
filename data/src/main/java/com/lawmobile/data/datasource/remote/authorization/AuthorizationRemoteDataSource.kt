package com.lawmobile.data.datasource.remote.authorization

import com.lawmobile.data.dto.entities.AuthorizationEndpointsDto
import com.lawmobile.data.dto.entities.DiscoveryEndpointsDto

interface AuthorizationRemoteDataSource {
    suspend fun getDiscoveryEndpoints(discoveryUrl: String): DiscoveryEndpointsDto
    suspend fun getAuthorizationEndpoints(safeFleetIdUrl: String): AuthorizationEndpointsDto
}
