package com.lawmobile.data.datasource.remote.authorization

import com.lawmobile.data.dto.api.authorization.AuthorizationApi
import com.lawmobile.data.dto.entities.discoveryUrl.AuthorizationEndpointsResponseDto
import com.safefleet.mobile.kotlin_commons.helpers.Result
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
    private val discoveryUrlRemoteDataSourceImpl =
        AuthorizationRemoteDataSourceImpl(authorizationApi)

    @Test
    fun getAuthorizationEndpointsSuccess() = runBlockingTest {
        val auth = AuthorizationEndpointsResponseDto("", "")
        coEvery { authorizationApi.getAuthorizationEndpoints(any()) } returns Result.Success(auth)
        val result = discoveryUrlRemoteDataSourceImpl.getAuthorizationEndpoints("")
        coVerify { authorizationApi.getAuthorizationEndpoints(any()) }
        Assert.assertTrue(result is Result.Success)
        val resultSuccess = result as Result.Success
        Assert.assertEquals(auth, resultSuccess.data)
    }

    @Test
    fun getAuthorizationEndpointsError() = runBlockingTest {
        val e = Exception()
        coEvery { authorizationApi.getAuthorizationEndpoints(any()) } returns Result.Error(e)
        val result = discoveryUrlRemoteDataSourceImpl.getAuthorizationEndpoints("")
        coVerify { authorizationApi.getAuthorizationEndpoints(any()) }
        Assert.assertTrue(result is Result.Error)
        val resultError = result as Result.Error
        Assert.assertEquals(e, resultError.exception)
    }
}
