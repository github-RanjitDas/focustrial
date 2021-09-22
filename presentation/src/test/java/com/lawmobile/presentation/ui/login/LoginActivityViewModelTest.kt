package com.lawmobile.presentation.ui.login

import com.lawmobile.domain.entities.DomainUser
import com.lawmobile.domain.usecase.validateOfficerPassword.ValidateOfficerPasswordUseCase
import com.lawmobile.presentation.InstantExecutorExtension
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(InstantExecutorExtension::class)
internal class LoginActivityViewModelTest {

    private val useCase: ValidateOfficerPasswordUseCase = mockk()
    private val loginActivityViewModel = LoginActivityViewModel(useCase)

    private val dispatcher = TestCoroutineDispatcher()

    @BeforeEach
    fun setUp() {
        clearMocks()
        Dispatchers.setMain(dispatcher)
    }

    @Test
    fun testGetInformationUserFlow() {
        coEvery { useCase.getUserInformation() } returns Result.Success(
            DomainUser("1", "", "")
        )
        loginActivityViewModel.getUserInformation()
        coVerify { useCase.getUserInformation() }
    }

    @Test
    fun testGetInformationUserSuccess() {
        val result = Result.Success(DomainUser("1", "", ""))
        coEvery { useCase.getUserInformation() } returns result
        loginActivityViewModel.getUserInformation()
        Assert.assertEquals(loginActivityViewModel.userInformationResult.value, result)
    }

    @Test
    fun testGetInformationUserError() {
        coEvery { useCase.getUserInformation() } returns Result.Error(
            Exception("Error")
        )
        loginActivityViewModel.getUserInformation()
        Assert.assertTrue(loginActivityViewModel.userInformationResult.value is Result.Error)
    }
}
