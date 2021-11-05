package com.lawmobile.domain.usecase.authorization

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
internal class AuthorizationUseCaseImplTest {

    private val repository: AuthorizationRepository = mockk()
    private val authorizationUseCaseImpl = AuthorizationUseCaseImpl(repository)

    @Test
    fun authorizationSuccess() = runBlockingTest {
        val auth = AuthorizationEndpoints("", "")
        coEvery { repository.getAuthorizationEndpoints(any()) } returns Result.Success(auth)
        val result = authorizationUseCaseImpl.getAuthorizationEndpoints("")
        coVerify { repository.getAuthorizationEndpoints(any()) }
        Assert.assertTrue(result is Result.Success)
        val resultSuccess = result as Result.Success
        Assert.assertEquals(auth, resultSuccess.data)
    }

    @Test
    fun authorizationError() = runBlockingTest {
        val e = Exception()
        coEvery { repository.getAuthorizationEndpoints(any()) } returns Result.Error(e)
        val result = authorizationUseCaseImpl.getAuthorizationEndpoints("")
        coVerify { repository.getAuthorizationEndpoints(any()) }
        Assert.assertTrue(result is Result.Error)
        val resultError = result as Result.Error
        Assert.assertEquals(e, resultError.exception)
    }
}
