package com.lawmobile.data.datasource.local.authorization

import com.lawmobile.data.dto.entities.DiscoveryEndpointsDto
import com.lawmobile.domain.entities.AuthorizationEndpoints
import com.lawmobile.domain.utils.PreferencesManager
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.Test

internal class AuthorizationLocalDataSourceImplTest {

    private val preferencesManager: PreferencesManager = mockk()
    private val authorizationLocalDataSourceImpl =
        AuthorizationLocalDataSourceImpl(preferencesManager)

    @Test
    fun getDiscoveryEndpointUrl() = runBlocking {
        val discoveryUrl = "discoveryUrl"
        coEvery { preferencesManager.getDiscoveryEndpointUrl() } returns discoveryUrl
        val result = authorizationLocalDataSourceImpl.getDiscoveryEndpointUrl()
        Assert.assertEquals(discoveryUrl, result)
        coVerify { preferencesManager.getDiscoveryEndpointUrl() }
    }

    @Test
    fun getAuthorizationEndpoints() = runBlocking {
        val authorizationUrl = "authorizationUrl"
        val tokenUrl = "tokenUrl"

        coEvery { preferencesManager.getAuthorizationEndpointUrl() } returns authorizationUrl
        coEvery { preferencesManager.getTokenEndpointUrl() } returns tokenUrl

        val result = authorizationLocalDataSourceImpl.getAuthorizationEndpoints()

        Assert.assertEquals(authorizationUrl, result.authorizationEndpoint)
        Assert.assertEquals(tokenUrl, result.tokenEndpoint)

        coVerify { preferencesManager.getAuthorizationEndpointUrl() }
        coVerify { preferencesManager.getTokenEndpointUrl() }
    }

    @Test
    fun saveDiscoveryEndpoints() = runBlocking {
        val discoveryEndpoints = DiscoveryEndpointsDto("", "")
        coEvery { preferencesManager.saveSafeFleetIdConfigUrl(any()) } returns Unit
        coEvery { preferencesManager.saveUsersEndpointUrl(any()) } returns Unit
        authorizationLocalDataSourceImpl.saveDiscoveryEndpoints(discoveryEndpoints)
        coVerify { preferencesManager.saveSafeFleetIdConfigUrl(any()) }
        coVerify { preferencesManager.saveUsersEndpointUrl(any()) }
    }

    @Test
    fun saveAuthorizationEndpoints() = runBlocking {
        val authorizationEndpoints = AuthorizationEndpoints("", "")
        coEvery { preferencesManager.saveAuthorizationEndpointUrl(any()) } returns Unit
        coEvery { preferencesManager.saveTokenEndpointUrl(any()) } returns Unit
        authorizationLocalDataSourceImpl.saveAuthorizationEndpoints(authorizationEndpoints)
        coVerify { preferencesManager.saveAuthorizationEndpointUrl(any()) }
        coVerify { preferencesManager.saveTokenEndpointUrl(any()) }
    }
}
