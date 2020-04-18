package com.lawmobile.data.repository.validatePasswordOfficer

import com.lawmobile.data.datasource.remote.validatePasswordOfficer.ValidatePasswordOfficerRemoteDataSource
import com.lawmobile.domain.entity.DomainUser
import com.safefleet.mobile.avml.cameras.entities.CameraConnectUserResponse
import com.safefleet.mobile.commons.helpers.Result
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ValidatePasswordOfficerRepositoryImplTest {

    private val validatePasswordOfficerRemoteDataSource: ValidatePasswordOfficerRemoteDataSource =
        mockk {

        }

    private val validatePasswordOfficerRepositoryImpl by lazy {
        ValidatePasswordOfficerRepositoryImpl(
            validatePasswordOfficerRemoteDataSource
        )
    }

    @Test
    fun testGetInformationUserSuccess() {
        val cameraConnectUserResponse = CameraConnectUserResponse("", "", "")
        coEvery { validatePasswordOfficerRemoteDataSource.getUserInformation() } returns Result.Success(
            cameraConnectUserResponse
        )
        runBlocking {
            Assert.assertEquals(
                validatePasswordOfficerRepositoryImpl.getUserInformation(),
                Result.Success(DomainUser("", "", ""))
            )
        }
    }

    @Test
    fun testGetInformationUserError() {
        coEvery { validatePasswordOfficerRemoteDataSource.getUserInformation() } returns Result.Error(
            Exception("Error")
        )
        runBlocking {
            val error = validatePasswordOfficerRepositoryImpl.getUserInformation() as Result.Error
            Assert.assertEquals(error.exception.message, "Error")
        }
    }

    @Test
    fun testGetInformationUserFlow() {
        coEvery { validatePasswordOfficerRemoteDataSource.getUserInformation() } returns Result.Error(
            Exception()
        )
        runBlocking { validatePasswordOfficerRepositoryImpl.getUserInformation() }
        coVerify { validatePasswordOfficerRemoteDataSource.getUserInformation() }
    }

}