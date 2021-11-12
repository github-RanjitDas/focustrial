package com.lawmobile.domain.usecase.getUserFromCamera

import com.lawmobile.domain.entities.User
import com.lawmobile.domain.repository.user.UserRepository
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.Test

internal class GetUserFromCameraImplTest {

    private val repository: UserRepository = mockk()
    private val getUserFromCameraImpl = GetUserFromCameraImpl(repository)

    @Test
    fun invokeSuccess() = runBlocking {
        val user = User("123", "kev", "123")
        coEvery { repository.getUserFromCamera() } returns Result.Success(user)
        val result = getUserFromCameraImpl()
        Assert.assertTrue(result is Result.Success)
        val resultSuccess = result as Result.Success
        Assert.assertEquals(user, resultSuccess.data)
        coVerify { repository.getUserFromCamera() }
    }

    @Test
    fun invokeError() = runBlocking {
        val exception = Exception()
        coEvery { repository.getUserFromCamera() } returns Result.Error(exception)
        val result = getUserFromCameraImpl()
        Assert.assertTrue(result is Result.Error)
        val resultError = result as Result.Error
        Assert.assertEquals(exception, resultError.exception)
        coVerify { repository.getUserFromCamera() }
    }
}
