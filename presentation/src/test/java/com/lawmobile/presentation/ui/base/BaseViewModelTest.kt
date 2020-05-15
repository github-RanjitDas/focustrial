package com.lawmobile.presentation.ui.base

import com.lawmobile.presentation.InstantExecutorExtension
import com.safefleet.mobile.commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(InstantExecutorExtension::class)
internal class BaseViewModelTest {

    private val baseViewModel: BaseViewModel by lazy {
        BaseViewModel()
    }

    @ExperimentalCoroutinesApi
    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @Test
    fun deactivateCameraHotspot() {
        baseViewModel.cameraConnectService = mockk()
        coEvery { baseViewModel.cameraConnectService.disconnectCamera() } returns Result.Success(
            Unit
        )
        baseViewModel.deactivateCameraHotspot()
        coVerify {
            baseViewModel.cameraConnectService.disconnectCamera()
        }
    }
}