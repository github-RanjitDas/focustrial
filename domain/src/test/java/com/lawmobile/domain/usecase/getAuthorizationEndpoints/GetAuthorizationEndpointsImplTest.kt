package com.lawmobile.domain.usecase.getAuthorizationEndpoints

import com.lawmobile.domain.entities.AuthorizationEndpoints
import com.lawmobile.domain.repository.authorization.AuthorizationRepository
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
internal class GetAuthorizationEndpointsImplTest {

    private val repository: AuthorizationRepository = mockk()
    private val getAuthEndpoints = GetAuthorizationEndpointsImpl(repository)

    @Test
    fun authorizationSuccess() = runBlockingTest {
        val auth = AuthorizationEndpoints("", "")
        coEvery { repository.getAuthorizationEndpoints() } returns Result.Success(auth)
        val result = getAuthEndpoints()
        coVerify { repository.getAuthorizationEndpoints() }
        Assert.assertTrue(result is Result.Success)
        val resultSuccess = result as Result.Success
        Assert.assertEquals(auth, resultSuccess.data)
    }

    @Test
    fun authorizationError() = runBlockingTest {
        val e = Exception()
        coEvery { repository.getAuthorizationEndpoints() } returns Result.Error(e)
        val result = getAuthEndpoints()
        coVerify { repository.getAuthorizationEndpoints() }
        Assert.assertTrue(result is Result.Error)
        val resultError = result as Result.Error
        Assert.assertEquals(e, resultError.exception)
    }
}
