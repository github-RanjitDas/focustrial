package com.lawmobile.data.dto.api.authorization

import com.lawmobile.data.dto.entities.discoveryUrl.AuthorizationEndpointsResponseDto
import com.safefleet.mobile.kotlin_commons.helpers.Result

interface AuthorizationApi {
    suspend fun getAuthorizationEndpoints(tenantID: String): Result<AuthorizationEndpointsResponseDto>
}
