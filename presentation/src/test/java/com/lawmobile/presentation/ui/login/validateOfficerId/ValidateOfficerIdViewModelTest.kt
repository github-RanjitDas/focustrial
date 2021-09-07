package com.lawmobile.presentation.ui.login.validateOfficerId

import com.lawmobile.domain.usecase.validateOfficerId.ValidateOfficerIdUseCase
import com.lawmobile.presentation.InstantExecutorExtension
import com.lawmobile.presentation.utils.SimpleNetworkManager
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.lang.Exception

@ExperimentalCoroutinesApi
@ExtendWith(InstantExecutorExtension::class)
internal class ValidateOfficerIdViewModelTest {

    private val useCase: ValidateOfficerIdUseCase = mockk()
    private val simpleNetworkManager = mockk<SimpleNetworkManager>(relaxed = true)
    private val dispatcher = TestCoroutineDispatcher()

    private val viewModel = ValidateOfficerIdViewModel(useCase, simpleNetworkManager, dispatcher)

    @Test
    fun validateOfficerIdSuccessTrue() = runBlockingTest {
        coEvery { useCase.validateOfficerId(any()) } returns Result.Success(true)
        viewModel.validateOfficerId("")
        coVerify { useCase.validateOfficerId(any()) }
        Assert.assertTrue(viewModel.validateOfficerIdResult.value is Result.Success)
        val resultSuccess = viewModel.validateOfficerIdResult.value as Result.Success
        Assert.assertTrue(resultSuccess.data)
    }

    @Test
    fun validateOfficerIdSuccessFalse() = runBlockingTest {
        coEvery { useCase.validateOfficerId(any()) } returns Result.Success(false)
        viewModel.validateOfficerId("")
        coVerify { useCase.validateOfficerId(any()) }
        Assert.assertTrue(viewModel.validateOfficerIdResult.value is Result.Success)
        val resultSuccess = viewModel.validateOfficerIdResult.value as Result.Success
        Assert.assertFalse(resultSuccess.data)
    }

    @Test
    fun validateOfficerIdError() = runBlockingTest {
        coEvery { useCase.validateOfficerId(any()) } returns Result.Error(Exception())
        viewModel.validateOfficerId("")
        coVerify { useCase.validateOfficerId(any()) }
        Assert.assertTrue(viewModel.validateOfficerIdResult.value is Result.Error)
    }

    @Test
    fun verifyInternetConnection() {
        every { simpleNetworkManager.verifyInternetConnection(any()) } just Runs
        viewModel.verifyInternetConnection { }
        verify { simpleNetworkManager.verifyInternetConnection(any()) }
    }
}
