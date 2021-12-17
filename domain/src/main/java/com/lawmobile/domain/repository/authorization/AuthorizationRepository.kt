package com.lawmobile.domain.repository.authorization

import com.lawmobile.domain.entities.AuthorizationEndpoints
import com.lawmobile.domain.repository.BaseRepository
import com.safefleet.mobile.kotlin_commons.helpers.Result

interface AuthorizationRepository : BaseRepository {
    suspend fun getAuthorizationEndpoints(): Result<AuthorizationEndpoints>
}
