package com.lawmobile.data.datasource.remote.authorization

import com.lawmobile.data.dto.api.authorization.AuthorizationApi
import com.lawmobile.data.dto.entities.AuthorizationEndpointsDto
import com.lawmobile.data.dto.entities.DiscoveryEndpointsDto

class AuthorizationRemoteDataSourceImpl(private val authorizationApi: AuthorizationApi) :
    AuthorizationRemoteDataSource {
    override suspend fun getDiscoveryEndpoints(
        discoveryUrl: String
    ): DiscoveryEndpointsDto = authorizationApi.getDiscoveryEndpoints(discoveryUrl)

    override suspend fun getAuthorizationEndpoints(
        safeFleetIdUrl: String
    ): AuthorizationEndpointsDto = authorizationApi.getAuthorizationEndpoints(safeFleetIdUrl)
}
