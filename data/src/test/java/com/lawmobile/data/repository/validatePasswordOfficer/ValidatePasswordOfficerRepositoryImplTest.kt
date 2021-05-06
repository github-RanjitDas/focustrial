package com.lawmobile.data.repository.validatePasswordOfficer

import com.lawmobile.data.datasource.remote.validatePasswordOfficer.ValidatePasswordOfficerRemoteDataSource
import com.lawmobile.domain.entities.DomainUser
import com.safefleet.mobile.external_hardware.cameras.entities.CameraUser
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
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
        val cameraUser = CameraUser("", "", "")
        coEvery { validatePasswordOfficerRemoteDataSource.getUserInformation() } returns Result.Success(
            cameraUser
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
