package com.lawmobile.domain.usecase.authorization

import com.lawmobile.domain.entities.AuthorizationEndpoints
import com.lawmobile.domain.usecase.BaseUseCase
import com.safefleet.mobile.kotlin_commons.helpers.Result

interface AuthorizationUseCase : BaseUseCase {
    suspend fun getAuthorizationEndpoints(tenantID: String): Result<AuthorizationEndpoints>
}
