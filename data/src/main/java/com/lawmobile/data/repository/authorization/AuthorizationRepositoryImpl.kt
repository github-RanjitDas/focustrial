package com.lawmobile.data.repository.authorization

import com.lawmobile.data.datasource.local.authorization.AuthorizationLocalDataSource
import com.lawmobile.data.datasource.remote.authorization.AuthorizationRemoteDataSource
import com.lawmobile.data.mappers.impl.AuthorizationEndpointsMapper.toDomain
import com.lawmobile.domain.entities.AuthorizationEndpoints
import com.lawmobile.domain.repository.authorization.AuthorizationRepository
import com.safefleet.mobile.kotlin_commons.helpers.Result

class AuthorizationRepositoryImpl(
    private val authorizationRemoteDataSource: AuthorizationRemoteDataSource,
    private val authorizationLocalDataSource: AuthorizationLocalDataSource
) : AuthorizationRepository {
    override suspend fun getAuthorizationEndpoints(): Result<AuthorizationEndpoints> {
        return try {
            var authEndpoints = authorizationLocalDataSource.getAuthorizationEndpoints()

            if (authEndpoints.isEmpty()) {
                val discoveryUrl = authorizationLocalDataSource.getDiscoveryEndpointUrl()
                val discoveryEndpoints =
                    authorizationRemoteDataSource.getDiscoveryEndpoints(discoveryUrl)
                authorizationLocalDataSource.saveDiscoveryEndpoints(discoveryEndpoints)

                authEndpoints = authorizationRemoteDataSource
                    .getAuthorizationEndpoints(discoveryEndpoints.sfIdConfiguration)
                    .toDomain()
                authorizationLocalDataSource.saveAuthorizationEndpoints(authEndpoints)
            }

            Result.Success(authEndpoints)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
