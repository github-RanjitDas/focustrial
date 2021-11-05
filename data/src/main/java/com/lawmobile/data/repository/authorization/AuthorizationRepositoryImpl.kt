package com.lawmobile.data.repository.authorization

import com.lawmobile.data.datasource.remote.authorization.AuthorizationRemoteDataSource
import com.lawmobile.data.mappers.impl.AuthorizationEndpointsMapper.toDomain
import com.lawmobile.domain.entities.AuthorizationEndpoints
import com.lawmobile.domain.repository.authorization.AuthorizationRepository
import com.safefleet.mobile.kotlin_commons.helpers.Result

class AuthorizationRepositoryImpl(
    private val authorizationRemoteDataSource: AuthorizationRemoteDataSource
) : AuthorizationRepository {
    override suspend fun getAuthorizationEndpoints(tenantID: String): Result<AuthorizationEndpoints> {
        return when (
            val result = authorizationRemoteDataSource.getAuthorizationEndpoints(tenantID)
        ) {
            is Result.Success -> Result.Success(result.data.toDomain())
            is Result.Error -> result
        }
    }
}
