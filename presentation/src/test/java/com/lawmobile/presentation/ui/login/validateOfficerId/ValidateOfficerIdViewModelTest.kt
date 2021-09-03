package com.lawmobile.presentation.ui.login.validateOfficerId

import com.lawmobile.presentation.InstantExecutorExtension
import com.lawmobile.presentation.utils.SimpleNetworkManager
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.Runs
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

@ExperimentalCoroutinesApi
@ExtendWith(InstantExecutorExtension::class)
internal class ValidateOfficerIdViewModelTest {

    private val dispatcher = TestCoroutineDispatcher()
    private val simpleNetworkManager = mockk<SimpleNetworkManager>(relaxed = true)

    private val viewModel = ValidateOfficerIdViewModel(simpleNetworkManager, dispatcher)

    @Test
    fun validateOfficerId() = runBlockingTest {
        viewModel.validateOfficerId("")
        Assert.assertTrue(viewModel.validateOfficerIdResult.value is Result.Error)
    }

    @Test
    fun verifyInternetConnection() {
        every { simpleNetworkManager.verifyInternetConnection(any()) } just Runs
        viewModel.verifyInternetConnection { }
        verify { simpleNetworkManager.verifyInternetConnection(any()) }
    }
}
