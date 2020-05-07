package com.lawmobile.data.datasource.remote.validatePasswordOfficer

import com.lawmobile.data.InstantExecutorExtension
import com.lawmobile.data.datasource.remote.validatePasswordOfficer.ValidatePasswordOfficerRemoteDataSourceImpl.Companion.ERROR_IN_INFORMATION_USER
import com.safefleet.mobile.avml.cameras.x1.CameraDataSource
import com.safefleet.mobile.avml.cameras.entities.CameraConnectUserResponse
import com.safefleet.mobile.commons.helpers.Result
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import java.lang.Exception

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(InstantExecutorExtension::class)
class ValidatePasswordOfficerRemoteDataSourceImplTest {

    private val connectCameraUserResult = Result.Success(CameraConnectUserResponse("", "", ""))
    private val cameraDataSource: CameraDataSource = mockk()

    private val validatePasswordOfficerRemoteDataSourceImpl by lazy {
        ValidatePasswordOfficerRemoteDataSourceImpl(cameraDataSource)
    }

    @Test
    fun testGetUserInformationFromDataSourceSuccess() {
        coEvery { cameraDataSource.getUserInformation() } returns connectCameraUserResult
        runBlocking {
            Assert.assertEquals(
                connectCameraUserResult,
                validatePasswordOfficerRemoteDataSourceImpl.getUserInformation()
            )
        }
    }

    @Test
    fun testGetUserInformationFromDataSourceSuccessFailed() {
        coEvery { cameraDataSource.getUserInformation() } throws Exception("")
        runBlocking {
            val responseError =
                validatePasswordOfficerRemoteDataSourceImpl.getUserInformation() as Result.Error

            Assert.assertEquals(responseError.exception.message, ERROR_IN_INFORMATION_USER)
        }
    }
}