package com.lawmobile.data.repository.validatePasswordOfficer

import com.lawmobile.data.datasource.remote.validateOfficerPassword.ValidateOfficerPasswordRemoteDataSource
import com.lawmobile.data.repository.validateOfficerPassword.ValidateOfficerPasswordRepositoryImpl
import com.lawmobile.domain.entities.DomainUser
import com.safefleet.mobile.external_hardware.cameras.entities.CameraUser
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@ExperimentalCoroutinesApi
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ValidateOfficerPasswordRepositoryImplTest {

    private val validatePasswordRemoteDataSource: ValidateOfficerPasswordRemoteDataSource = mockk()
    private val dispatcher = TestCoroutineDispatcher()

    private val validatePasswordRepositoryImpl by lazy {
        ValidateOfficerPasswordRepositoryImpl(validatePasswordRemoteDataSource)
    }

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @Test
    fun testGetInformationUserSuccess() = runBlockingTest {
        val cameraUser = CameraUser("", "", "")
        coEvery {
            validatePasswordRemoteDataSource.getUserInformation()
        } returns Result.Success(cameraUser)
        Assert.assertEquals(
            validatePasswordRepositoryImpl.getUserInformation(),
            Result.Success(DomainUser("", "", ""))
        )
    }

    @Test
    fun testGetInformationUserError() = runBlockingTest {
        coEvery {
            validatePasswordRemoteDataSource.getUserInformation()
        } returns Result.Error(Exception("Error"))
        val error = validatePasswordRepositoryImpl.getUserInformation() as Result.Error
        Assert.assertEquals(error.exception.message, "Error")
    }

    @Test
    fun testGetInformationUserFlow() = runBlockingTest {
        coEvery {
            validatePasswordRemoteDataSource.getUserInformation()
        } returns Result.Error(Exception())
        validatePasswordRepositoryImpl.getUserInformation()
        coVerify { validatePasswordRemoteDataSource.getUserInformation() }
    }
}
