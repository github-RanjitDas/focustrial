package com.lawmobile.presentation.ui.login.x1.fragment.officerPassword

import com.lawmobile.domain.entities.DomainUser
import com.lawmobile.domain.usecase.validatePasswordOfficer.ValidatePasswordOfficerUseCase
import com.lawmobile.presentation.InstantExecutorExtension
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(InstantExecutorExtension::class)
class OfficerPasswordViewModelTest {

    private val passwordUseCase: ValidatePasswordOfficerUseCase = mockk()

    private val viewModel = OfficerPasswordViewModel(passwordUseCase)

    @ExperimentalCoroutinesApi
    @BeforeEach
    fun setUp() {
        clearMocks()
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @Test
    fun testGetInformationUserFlow() {
        coEvery { passwordUseCase.getUserInformation() } returns Result.Success(mockk())
        viewModel.getUserInformation()
        coVerify { passwordUseCase.getUserInformation() }
    }

    @Test
    fun testGetInformationUserSuccess() {
        val result = Result.Success(DomainUser("1", "", ""))
        coEvery { passwordUseCase.getUserInformation() } returns result
        viewModel.getUserInformation()
        Assert.assertEquals(viewModel.domainUserLiveData.value, result)
    }

    @Test
    fun testGetInformationUserError() {
        coEvery { passwordUseCase.getUserInformation() } returns Result.Error(
            Exception("Error")
        )
        viewModel.getUserInformation()
        Assert.assertTrue(viewModel.domainUserLiveData.value is Result.Error)
    }

    @Test
    fun testGetOfficerPasswordWhenNull() {
        Assert.assertEquals("", viewModel.getOfficerPassword())
    }

    @Test
    fun testSetOfficerPassword() {
        val password = "123"
        viewModel.setOfficerPassword(password)
        Assert.assertEquals(password, viewModel.getOfficerPassword())
    }
}
