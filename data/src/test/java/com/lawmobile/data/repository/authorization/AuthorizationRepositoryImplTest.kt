package com.lawmobile.data.repository.authorization

import com.lawmobile.data.datasource.remote.authorization.AuthorizationRemoteDataSource
import com.lawmobile.data.dto.entities.discoveryUrl.AuthorizationEndpointsResponseDto
import com.lawmobile.data.mappers.impl.AuthorizationEndpointsMapper.toDomain
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
internal class AuthorizationRepositoryImplTest {

    private val dataSource: AuthorizationRemoteDataSource = mockk()
    private val authorizationRepositoryImpl = AuthorizationRepositoryImpl(dataSource)

    @Test
    fun getAuthorizationEndpointsSuccess() = runBlockingTest {
        val auth = AuthorizationEndpointsResponseDto("", "")
        coEvery { dataSource.getAuthorizationEndpoints(any()) } returns Result.Success(auth)
        val result = authorizationRepositoryImpl.getAuthorizationEndpoints("")
        coVerify { dataSource.getAuthorizationEndpoints(any()) }
        Assert.assertTrue(result is Result.Success)
        val resultSuccess = result as Result.Success
        Assert.assertEquals(auth.toDomain(), resultSuccess.data)
    }

    @Test
    fun getAuthorizationEndpointsError() = runBlockingTest {
        val e = Exception()
        coEvery { dataSource.getAuthorizationEndpoints(any()) } returns Result.Error(e)
        val result = authorizationRepositoryImpl.getAuthorizationEndpoints("")
        coVerify { dataSource.getAuthorizationEndpoints(any()) }
        Assert.assertTrue(result is Result.Error)
        val resultError = result as Result.Error
        Assert.assertEquals(e, resultError.exception)
    }
}
