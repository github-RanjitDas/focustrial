package com.lawmobile.presentation.ui.login.validateOfficerId

import com.lawmobile.presentation.InstantExecutorExtension
import com.safefleet.mobile.android_commons.helpers.network_manager.ListenableNetworkManager
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(InstantExecutorExtension::class)
internal class ValidateOfficerIdViewModelTest {

    private val simpleNetworkManager = mockk<ListenableNetworkManager>(relaxed = true)
    private val dispatcher = TestCoroutineDispatcher()
    private val viewModel = ValidateOfficerIdViewModel(simpleNetworkManager, dispatcher)

    @Test
    fun verifyInternetConnection() = runBlockingTest {
        coEvery { simpleNetworkManager.verifyInternetConnection(any()) } just Runs
        viewModel.verifyInternetConnection { }
        coVerify { simpleNetworkManager.verifyInternetConnection(any()) }
    }
}
