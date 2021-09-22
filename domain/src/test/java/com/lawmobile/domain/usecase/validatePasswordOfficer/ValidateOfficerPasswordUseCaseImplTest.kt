package com.lawmobile.domain.usecase.validatePasswordOfficer

import com.lawmobile.domain.repository.validateOfficerPassword.ValidateOfficerPasswordRepository
import com.lawmobile.domain.usecase.validateOfficerPassword.ValidateOfficerPasswordUseCaseImpl
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ValidateOfficerPasswordUseCaseImplTest {

    private val validateOfficerPasswordRepository: ValidateOfficerPasswordRepository = mockk {
        coEvery { getUserInformation() } returns Result.Error(Exception(""))
    }
    private val validatePasswordOfficerUseCaseImpl by lazy {
        ValidateOfficerPasswordUseCaseImpl(validateOfficerPasswordRepository)
    }

    @Test
    fun testGetInformationUser() {
        runBlocking {
            validatePasswordOfficerUseCaseImpl.getUserInformation()
        }
        coVerify { validateOfficerPasswordRepository.getUserInformation() }
    }
}
