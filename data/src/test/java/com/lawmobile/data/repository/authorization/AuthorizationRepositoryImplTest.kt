package com.lawmobile.data.repository.authorization

import com.lawmobile.data.datasource.local.authorization.AuthorizationLocalDataSource
import com.lawmobile.data.datasource.remote.authorization.AuthorizationRemoteDataSource
import com.lawmobile.data.dto.entities.AuthorizationEndpointsDto
import com.lawmobile.data.dto.entities.DiscoveryEndpointsDto
import com.lawmobile.domain.entities.AuthorizationEndpoints
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

    private val remoteDataSource: AuthorizationRemoteDataSource = mockk()
    private val localDataSource: AuthorizationLocalDataSource = mockk()
    private val authorizationRepositoryImpl =
        AuthorizationRepositoryImpl(remoteDataSource, localDataSource)

    @Test
    fun getAuthorizationEndpointsPreviouslySavedSuccess() = runBlockingTest {
        val authorizationEndpoints = AuthorizationEndpoints("url", "url")
        coEvery { localDataSource.getAuthorizationEndpoints() } returns authorizationEndpoints
        val result = authorizationRepositoryImpl.getAuthorizationEndpoints()
        Assert.assertTrue(result is Result.Success)
        val resultSuccess = result as Result.Success
        Assert.assertEquals(authorizationEndpoints, resultSuccess.data)
        coVerify { localDataSource.getAuthorizationEndpoints() }
    }

    @Test
    fun getAuthorizationEndpointsPreviouslySavedError() = runBlockingTest {
        val exception = Exception()
        coEvery { localDataSource.getAuthorizationEndpoints() } throws exception
        val result = authorizationRepositoryImpl.getAuthorizationEndpoints()
        Assert.assertTrue(result is Result.Error)
        val resultError = result as Result.Error
        Assert.assertEquals(exception, resultError.exception)
        coVerify { localDataSource.getAuthorizationEndpoints() }
    }

    @Test
    fun getAuthorizationEndpointsFromNetworkSuccess() = runBlockingTest {
        val authorizationEndpoints = AuthorizationEndpoints("", "")
        val authorizationEndpointsDto = AuthorizationEndpointsDto("", "")
        val discoveryEndpointsDto = DiscoveryEndpointsDto("", "")

        coEvery { localDataSource.getAuthorizationEndpoints() } returns authorizationEndpoints
        coEvery { localDataSource.getDiscoveryEndpointUrl() } returns "url"
        coEvery { remoteDataSource.getDiscoveryEndpoints(any()) } returns discoveryEndpointsDto
        coEvery { localDataSource.saveDiscoveryEndpoints(any()) } returns Unit
        coEvery { remoteDataSource.getAuthorizationEndpoints(any()) } returns authorizationEndpointsDto
        coEvery { localDataSource.saveAuthorizationEndpoints(any()) } returns Unit

        val result = authorizationRepositoryImpl.getAuthorizationEndpoints()
        Assert.assertTrue(result is Result.Success)
        val resultSuccess = result as Result.Success
        Assert.assertEquals(authorizationEndpoints, resultSuccess.data)

        coVerify { localDataSource.getAuthorizationEndpoints() }
        coVerify { localDataSource.getDiscoveryEndpointUrl() }
        coVerify { remoteDataSource.getDiscoveryEndpoints(any()) }
        coVerify { localDataSource.saveDiscoveryEndpoints(any()) }
        coVerify { remoteDataSource.getAuthorizationEndpoints(any()) }
        coVerify { localDataSource.saveAuthorizationEndpoints(any()) }
    }

    @Test
    fun getAuthorizationEndpointsFromNetworkError() = runBlockingTest {
        val exception = Exception()
        val authorizationEndpoints = AuthorizationEndpoints("", "")
        val discoveryEndpointsDto = DiscoveryEndpointsDto("", "")

        coEvery { localDataSource.getAuthorizationEndpoints() } returns authorizationEndpoints
        coEvery { localDataSource.getDiscoveryEndpointUrl() } returns "url"
        coEvery { remoteDataSource.getDiscoveryEndpoints(any()) } returns discoveryEndpointsDto
        coEvery { localDataSource.saveDiscoveryEndpoints(any()) } returns Unit
        coEvery { remoteDataSource.getAuthorizationEndpoints(any()) } throws exception

        val result = authorizationRepositoryImpl.getAuthorizationEndpoints()
        Assert.assertTrue(result is Result.Error)
        val resultError = result as Result.Error
        Assert.assertEquals(exception, resultError.exception)

        coVerify { localDataSource.getAuthorizationEndpoints() }
        coVerify { localDataSource.getDiscoveryEndpointUrl() }
        coVerify { remoteDataSource.getDiscoveryEndpoints(any()) }
        coVerify { localDataSource.saveDiscoveryEndpoints(any()) }
        coVerify { remoteDataSource.getAuthorizationEndpoints(any()) }
    }

    @Test
    fun getDiscoveryEndpointsFromNetworkError() = runBlockingTest {
        val exception = Exception()
        val authorizationEndpoints = AuthorizationEndpoints("", "")

        coEvery { localDataSource.getAuthorizationEndpoints() } returns authorizationEndpoints
        coEvery { localDataSource.getDiscoveryEndpointUrl() } returns "url"
        coEvery { remoteDataSource.getDiscoveryEndpoints(any()) } throws exception

        val result = authorizationRepositoryImpl.getAuthorizationEndpoints()
        Assert.assertTrue(result is Result.Error)
        val resultError = result as Result.Error
        Assert.assertEquals(exception, resultError.exception)

        coVerify { localDataSource.getAuthorizationEndpoints() }
        coVerify { localDataSource.getDiscoveryEndpointUrl() }
        coVerify { remoteDataSource.getDiscoveryEndpoints(any()) }
    }
}
