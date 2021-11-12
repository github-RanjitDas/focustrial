package com.lawmobile.data.dto.api.authorization

import com.lawmobile.data.dto.entities.AuthorizationEndpointsDto
import com.lawmobile.data.dto.entities.DiscoveryEndpointsDto
import com.lawmobile.data.dto.interceptors.FakeHttpClient
import com.lawmobile.data.dto.interceptors.FakeHttpClient.AUTHORIZATION_URL
import com.lawmobile.data.dto.interceptors.FakeHttpClient.DISCOVERY_URL
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.Test

internal class AuthorizationApiImplTest {

    private val httpClient: HttpClient = FakeHttpClient.create()
    private val authorizationApiImpl = AuthorizationApiImpl(httpClient)

    @Test
    fun getAuthorizationEndpointsSuccess() = runBlocking {
        val response: AuthorizationEndpointsDto = httpClient.get(AUTHORIZATION_URL)
        val result = authorizationApiImpl.getAuthorizationEndpoints(AUTHORIZATION_URL)
        Assert.assertEquals(response, result)
    }

    @Test
    fun getAuthorizationEndpointsFail() = runBlocking {
        val response: AuthorizationEndpointsDto = httpClient.get(AUTHORIZATION_URL)
        val result = try {
            authorizationApiImpl.getAuthorizationEndpoints("")
        } catch (e: Exception) {
            e
        }
        Assert.assertNotEquals(response, result)
    }

    @Test
    fun getDiscoveryEndpointsSuccess() = runBlocking {
        val response: DiscoveryEndpointsDto = httpClient.get(DISCOVERY_URL)
        val result = authorizationApiImpl.getDiscoveryEndpoints(DISCOVERY_URL)
        Assert.assertEquals(response, result)
    }

    @Test
    fun getDiscoveryEndpointsFail() = runBlocking {
        val response: DiscoveryEndpointsDto = httpClient.get(DISCOVERY_URL)
        val result = try {
            authorizationApiImpl.getDiscoveryEndpoints("")
        } catch (e: Exception) {
            e
        }
        Assert.assertNotEquals(response, result)
    }
}
