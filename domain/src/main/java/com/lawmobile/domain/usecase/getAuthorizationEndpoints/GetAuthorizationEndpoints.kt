package com.lawmobile.domain.usecase.getAuthorizationEndpoints

import com.lawmobile.domain.entities.AuthorizationEndpoints
import com.lawmobile.domain.usecase.BaseUseCase
import com.safefleet.mobile.kotlin_commons.helpers.Result

interface GetAuthorizationEndpoints : BaseUseCase {
    suspend operator fun invoke(): Result<AuthorizationEndpoints>
}
