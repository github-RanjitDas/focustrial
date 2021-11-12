package com.lawmobile.data.datasource.local.authorization

import com.lawmobile.data.dto.entities.DiscoveryEndpointsDto
import com.lawmobile.domain.entities.AuthorizationEndpoints
import com.lawmobile.domain.utils.PreferencesManager

class AuthorizationLocalDataSourceImpl(
    private val preferencesManager: PreferencesManager
) : AuthorizationLocalDataSource {
    override suspend fun getDiscoveryEndpointUrl(): String =
        preferencesManager.getDiscoveryEndpointUrl()

    override suspend fun getAuthorizationEndpoints(): AuthorizationEndpoints {
        return AuthorizationEndpoints(
            preferencesManager.getAuthorizationEndpointUrl(),
            preferencesManager.getTokenEndpointUrl()
        )
    }

    override suspend fun saveDiscoveryEndpoints(endpoints: DiscoveryEndpointsDto) {
        preferencesManager.saveSafeFleetIdConfigUrl(endpoints.sfIdConfiguration)
        preferencesManager.saveUsersEndpointUrl(endpoints.usersEndpoint)
    }

    override suspend fun saveAuthorizationEndpoints(endpoints: AuthorizationEndpoints) {
        preferencesManager.saveAuthorizationEndpointUrl(endpoints.authorizationEndpoint)
        preferencesManager.saveTokenEndpointUrl(endpoints.tokenEndpoint)
    }
}
