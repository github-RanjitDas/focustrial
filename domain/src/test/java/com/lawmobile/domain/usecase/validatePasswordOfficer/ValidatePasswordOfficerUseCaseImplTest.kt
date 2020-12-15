package com.lawmobile.domain.usecase.validatePasswordOfficer

import com.lawmobile.domain.repository.validatePasswordOfficer.ValidatePasswordOfficerRepository
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ValidatePasswordOfficerUseCaseImplTest {

    private val validatePasswordOfficerRepository: ValidatePasswordOfficerRepository = mockk {
        coEvery { getUserInformation() } returns Result.Error(Exception(""))
    }
    private val validatePasswordOfficerUseCaseImpl by lazy {
        ValidatePasswordOfficerUseCaseImpl(validatePasswordOfficerRepository)
    }

    @Test
    fun testGetInformationUser() {
        runBlocking {
            validatePasswordOfficerUseCaseImpl.getUserInformation()
        }
        coVerify { validatePasswordOfficerRepository.getUserInformation() }
    }

}