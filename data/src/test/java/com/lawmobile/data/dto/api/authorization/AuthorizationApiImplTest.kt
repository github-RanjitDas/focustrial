package com.lawmobile.data.dto.api.authorization

import com.lawmobile.data.dto.interceptors.FakeHttpClient
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.ktor.client.HttpClient
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.Test

internal class AuthorizationApiImplTest {

    private val httpClient: HttpClient = FakeHttpClient.create()

    @Test
    fun getAuthorizationEndpointsSuccess() = runBlocking {
        val authorizationApiImpl = AuthorizationApiImpl("tenant-settings/api/hardware/discovery", httpClient)
        val result = authorizationApiImpl.getAuthorizationEndpoints("")
        Assert.assertTrue(result is Result.Success)
    }

    @Test
    fun getAuthorizationEndpointsError() = runBlocking {
        val authorizationApiImpl = AuthorizationApiImpl("fakeUrl", httpClient)
        val result = authorizationApiImpl.getAuthorizationEndpoints("")
        Assert.assertTrue(result is Result.Error)
    }
}
