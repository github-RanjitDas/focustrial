package com.lawmobile.data.datasource.remote.authorization

import com.lawmobile.data.dto.api.authorization.AuthorizationApi
import com.lawmobile.data.dto.entities.discoveryUrl.AuthorizationEndpointsResponseDto
import com.safefleet.mobile.kotlin_commons.helpers.Result

class AuthorizationRemoteDataSourceImpl(private val authorizationApi: AuthorizationApi) :
    AuthorizationRemoteDataSource {
    override suspend fun getAuthorizationEndpoints(tenantID: String): Result<AuthorizationEndpointsResponseDto> =
        authorizationApi.getAuthorizationEndpoints(tenantID)
}
