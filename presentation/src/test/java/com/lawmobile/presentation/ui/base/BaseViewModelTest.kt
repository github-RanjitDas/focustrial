package com.lawmobile.presentation.ui.base

import com.lawmobile.presentation.InstantExecutorExtension
import com.lawmobile.presentation.utils.CameraEventsManager
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(InstantExecutorExtension::class)
internal class BaseViewModelTest {

    private val cameraEventsManager: CameraEventsManager = mockk {
        every { startReading() } just Runs
        every { stopReading() } just Runs
    }

    private val baseViewModel: BaseViewModel by lazy {
        BaseViewModel()
    }

    @ExperimentalCoroutinesApi
    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        baseViewModel.setNotificationManager(cameraEventsManager)
    }

    @Test
    fun waitToFinish() {
        baseViewModel.waitToFinish(100)
        runBlocking {
            delay(150)
            Assert.assertTrue(baseViewModel.isWaitFinishedLiveData.value!!.getContent())
        }
    }

    @Test
    fun startReadingEventsFlow() {
        baseViewModel.startReadingEvents()
        verify { cameraEventsManager.startReading() }
    }

    @Test
    fun stopReadingEventsFlow() {
        baseViewModel.stopReadingEvents()
        verify { cameraEventsManager.stopReading() }
    }

    @Test
    fun logsEventLiveDataSuccess() {
        every { cameraEventsManager.logEventsLiveData.value } returns Result.Success(mockk())
        Assert.assertTrue(baseViewModel.logEventsLiveData().value is Result.Success)
    }

    @Test
    fun logsEventLiveDataError() {
        every { cameraEventsManager.logEventsLiveData.value } returns Result.Error(mockk())
        Assert.assertTrue(baseViewModel.logEventsLiveData().value is Result.Error)
    }

    @Test
    fun getLoadingTimeout() {
        val timeout = 20000L
        Assert.assertEquals(
            timeout,
            BaseViewModel.getLoadingTimeOut()
        )
    }
}
