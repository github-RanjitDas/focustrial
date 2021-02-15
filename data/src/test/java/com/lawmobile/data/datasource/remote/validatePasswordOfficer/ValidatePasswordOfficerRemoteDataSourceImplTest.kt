package com.lawmobile.data.datasource.remote.validatePasswordOfficer

import com.lawmobile.data.InstantExecutorExtension
import com.lawmobile.data.datasource.remote.validatePasswordOfficer.ValidatePasswordOfficerRemoteDataSourceImpl.Companion.ERROR_IN_INFORMATION_USER
import com.safefleet.mobile.external_hardware.cameras.CameraService
import com.safefleet.mobile.external_hardware.cameras.entities.CameraUser
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import java.lang.Exception

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(InstantExecutorExtension::class)
class ValidatePasswordOfficerRemoteDataSourceImplTest {

    private val connectCameraUserResult = Result.Success(CameraUser("", "", ""))
    private val cameraService: CameraService = mockk()

    private val validatePasswordOfficerRemoteDataSourceImpl by lazy {
        ValidatePasswordOfficerRemoteDataSourceImpl(cameraService)
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

            Assert.assertEquals(responseError.exception.message, ERROR_IN_INFORMATION_USER)
        }
    }
}
