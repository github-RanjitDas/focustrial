package com.lawmobile.data.datasource.remote.validatePasswordOfficer

import com.lawmobile.data.datasource.remote.validateOfficerPassword.ValidateOfficerPasswordRemoteDataSourceImpl
import com.lawmobile.data.datasource.remote.validateOfficerPassword.ValidateOfficerPasswordRemoteDataSourceImpl.Companion.USER_INFORMATION_ERROR
import com.lawmobile.data.utils.CameraServiceFactory
import com.safefleet.mobile.external_hardware.cameras.CameraService
import com.safefleet.mobile.external_hardware.cameras.entities.CameraUser
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.lang.Exception

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ValidateOfficerPasswordRemoteDataSourceImplTest {

    private val connectCameraUserResult = Result.Success(CameraUser("", "", ""))
    private val cameraService: CameraService = mockk()
    private val cameraServiceFactory: CameraServiceFactory = mockk {
        every { create() } returns cameraService
    }

    private val validatePasswordOfficerRemoteDataSourceImpl by lazy {
        ValidateOfficerPasswordRemoteDataSourceImpl(cameraServiceFactory)
    }

    @Test
    fun testGetUserInformationFromDataSourceSuccess() {
        coEvery { cameraService.getUserResponse() } returns connectCameraUserResult
        runBlocking {
            Assert.assertEquals(
                connectCameraUserResult,
                validatePasswordOfficerRemoteDataSourceImpl.getUserInformation()
            )
        }
    }

    @Test
    fun testGetUserInformationFromDataSourceSuccessFailed() {
        coEvery { cameraService.getUserResponse() } throws Exception("")
        runBlocking {
            val responseError =
                validatePasswordOfficerRemoteDataSourceImpl.getUserInformation() as Result.Error
            Assert.assertEquals(responseError.exception.message, USER_INFORMATION_ERROR)
        }
    }
}
