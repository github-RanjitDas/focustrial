package com.lawmobile.data.datasource.remote.user

import com.lawmobile.data.dto.api.user.UserApi
import com.lawmobile.data.dto.entities.UserDto
import com.lawmobile.data.utils.CameraServiceFactory
import com.safefleet.mobile.external_hardware.cameras.CameraService
import com.safefleet.mobile.external_hardware.cameras.entities.CameraUser
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.Test

internal class UserRemoteDataSourceImplTest {

    private val cameraService: CameraService = mockk()
    private val cameraServiceFactory: CameraServiceFactory = mockk {
        every { create() } returns cameraService
    }
    private val userApi: UserApi = mockk()
    private val userRemoteDataSourceImpl = UserRemoteDataSourceImpl(cameraServiceFactory, userApi)

    @Test
    fun getUserFromNetwork() = runBlocking {
        val userDto: UserDto = mockk(relaxed = true)
        coEvery { userApi.getUser(any()) } returns userDto
        val result = userRemoteDataSourceImpl.getUserFromNetwork("")
        Assert.assertEquals(userDto, result)
        coVerify { userApi.getUser(any()) }
    }

    @Test
    fun getUserFromCameraSuccess() = runBlocking {
        val cameraUser: CameraUser = mockk()
        coEvery { cameraService.getUserResponse() } returns Result.Success(cameraUser)
        val result = userRemoteDataSourceImpl.getUserFromCamera()
        Assert.assertEquals(cameraUser, result)
        coVerify { cameraService.getUserResponse() }
    }

    @Test
    fun getUserFromCameraException() = runBlocking {
        val exception = Exception()
        coEvery { cameraService.getUserResponse() } returns Result.Error(exception)
        val result = try {
            userRemoteDataSourceImpl.getUserFromCamera()
        } catch (e: Exception) {
            e
        }
        Assert.assertEquals(exception, result)
        coVerify { cameraService.getUserResponse() }
    }
}
