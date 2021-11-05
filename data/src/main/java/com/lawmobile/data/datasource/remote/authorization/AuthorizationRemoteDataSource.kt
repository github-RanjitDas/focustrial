package com.lawmobile.data.datasource.remote.authorization

import com.lawmobile.data.dto.entities.discoveryUrl.AuthorizationEndpointsResponseDto
import com.safefleet.mobile.kotlin_commons.helpers.Result

interface AuthorizationRemoteDataSource {
    suspend fun getAuthorizationEndpoints(tenantID: String): Result<AuthorizationEndpointsResponseDto>
}
