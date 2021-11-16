package com.lawmobile.data.datasource.local.authorization

import com.lawmobile.data.dto.entities.DiscoveryEndpointsDto
import com.lawmobile.domain.entities.AuthorizationEndpoints

interface AuthorizationLocalDataSource {
    suspend fun getDiscoveryEndpointUrl(): String
    suspend fun getAuthorizationEndpoints(): AuthorizationEndpoints
    suspend fun saveDiscoveryEndpoints(endpoints: DiscoveryEndpointsDto)
    suspend fun saveAuthorizationEndpoints(endpoints: AuthorizationEndpoints)
}
