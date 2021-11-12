package com.lawmobile.data.datasource.remote.authorization

import com.lawmobile.data.dto.api.authorization.AuthorizationApi
import com.lawmobile.data.dto.entities.AuthorizationEndpointsDto
import com.lawmobile.data.dto.entities.DiscoveryEndpointsDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
internal class AuthorizationRemoteDataSourceImplTest {

    private val authorizationApi: AuthorizationApi = mockk(relaxed = true)
    private val authorizationRemoteDataSourceImpl =
        AuthorizationRemoteDataSourceImpl(authorizationApi)

    @Test
    fun getAuthorizationEndpoints() = runBlockingTest {
        val response = AuthorizationEndpointsDto("", "")
        coEvery { authorizationApi.getAuthorizationEndpoints(any()) } returns response
        val result = authorizationRemoteDataSourceImpl.getAuthorizationEndpoints("")
        coVerify { authorizationApi.getAuthorizationEndpoints(any()) }
        Assert.assertEquals(response, result)
    }

    @Test
    fun getDiscoveryEndpoints() = runBlockingTest {
        val response = DiscoveryEndpointsDto("", "")
        coEvery { authorizationApi.getDiscoveryEndpoints(any()) } returns response
        val result = authorizationRemoteDataSourceImpl.getDiscoveryEndpoints("")
        coVerify { authorizationApi.getDiscoveryEndpoints(any()) }
        Assert.assertEquals(response, result)
    }
}
