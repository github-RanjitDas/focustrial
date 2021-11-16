package com.lawmobile.domain.usecase.getDevicePassword

import com.lawmobile.domain.entities.User
import com.lawmobile.domain.repository.user.UserRepository
import com.lawmobile.domain.validator.DevicePasswordValidator
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.mockkObject
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.Test

internal class GetDevicePasswordImplTest {

    private val repository: UserRepository = mockk()
    private val getDevicePassword = GetDevicePasswordImpl(repository)

    @Test
    fun invokeSuccess() = runBlocking {
        mockkObject(DevicePasswordValidator)
        val user = User(devicePassword = "123")
        val resultUser = Result.Success(user)
        coEvery { repository.getUserFromNetwork(any()) } returns resultUser
        val result = getDevicePassword("")
        Assert.assertTrue(result is Result.Success)
        val resultSuccess = result as Result.Success
        Assert.assertEquals(user.devicePassword, resultSuccess.data)
        coVerify { repository.getUserFromNetwork(any()) }
        coVerify { DevicePasswordValidator(user.devicePassword) }
    }

    @Test
    fun invokeRepositoryError() = runBlocking {
        val exception = Exception()
        val resultUser = Result.Error(exception)
        coEvery { repository.getUserFromNetwork(any()) } returns resultUser
        val result = getDevicePassword("")
        Assert.assertTrue(result is Result.Error)
        val resultError = result as Result.Error
        Assert.assertEquals(exception, resultError.exception)
        coVerify { repository.getUserFromNetwork(any()) }
    }

    @Test
    fun invokeValidatorError() = runBlocking {
        mockkObject(DevicePasswordValidator)
        val user = User(devicePassword = "")
        val resultUser = Result.Success(user)
        coEvery { repository.getUserFromNetwork(any()) } returns resultUser
        val result = getDevicePassword("")
        Assert.assertTrue(result is Result.Error)
        coVerify { repository.getUserFromNetwork(any()) }
        coVerify { DevicePasswordValidator(user.devicePassword) }
    }
}
