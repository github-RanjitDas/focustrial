package com.lawmobile.presentation.ui.login

import com.lawmobile.domain.entities.DomainUser
import com.lawmobile.domain.usecase.validatePasswordOfficer.ValidatePasswordOfficerUseCase
import com.lawmobile.presentation.InstantExecutorExtension
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(InstantExecutorExtension::class)
internal class LoginBaseViewModelTest {

    private val useCase: ValidatePasswordOfficerUseCase = mockk()
    private val dispatcher = TestCoroutineDispatcher()
    private val baseViewModel = LoginBaseViewModel(useCase, dispatcher)

    @Test
    fun setInstructionsOpenTrue() {
        baseViewModel.setInstructionsOpen(true)
        Assert.assertTrue(baseViewModel.isInstructionsOpen.value!!)
    }

    @Test
    fun setInstructionsOpenFalse() {
        baseViewModel.setInstructionsOpen(false)
        Assert.assertFalse(baseViewModel.isInstructionsOpen.value!!)
    }

    @Test
    fun getUserFromCameraSuccess() {
        val result: Result<DomainUser> = Result.Success(mockk())
        coEvery { useCase.getUserInformation() } returns result
        baseViewModel.getUserFromCamera()
        Assert.assertEquals(result, baseViewModel.userFromCameraResult.value)
        coVerify { useCase.getUserInformation() }
    }

    @Test
    fun getUserFromCameraError() {
        val result: Result<DomainUser> = Result.Error(mockk())
        coEvery { useCase.getUserInformation() } returns result
        baseViewModel.getUserFromCamera()
        Assert.assertEquals(result, baseViewModel.userFromCameraResult.value)
        coVerify { useCase.getUserInformation() }
    }
}
