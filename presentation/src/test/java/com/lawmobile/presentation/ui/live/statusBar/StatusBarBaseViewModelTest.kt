package com.lawmobile.presentation.ui.live.statusBar

import com.lawmobile.domain.usecase.liveStreaming.LiveStreamingUseCase
import com.lawmobile.presentation.InstantExecutorExtension
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ExtendWith(InstantExecutorExtension::class)
internal class StatusBarBaseViewModelTest {

    private val liveStreamingUseCase: LiveStreamingUseCase = mockk()

    private val viewModel: StatusBarBaseViewModel by lazy {
        StatusBarBaseViewModel(liveStreamingUseCase)
    }

    private val dispatcher = TestCoroutineDispatcher()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    private fun storageLevelMock() {
        val result = Result.Success("1")
        coEvery { liveStreamingUseCase.getFreeStorage() } returns result
        coEvery { liveStreamingUseCase.getTotalStorage() } returns result
    }

    private fun batteryLevelMock() {
        coEvery { liveStreamingUseCase.getBatteryLevel() } returns Result.Success(1)
    }

    @Test
    fun getBatteryLevelSuccess() = runBlockingTest {
        val result = Result.Success(23)
        coEvery { liveStreamingUseCase.getBatteryLevel() } returns result
        storageLevelMock()

        viewModel.getCameraStatus()
        Assert.assertEquals(viewModel.batteryLevel.value?.getContent(), result)

        coVerify { liveStreamingUseCase.getBatteryLevel() }
    }

    @Test
    fun getBatteryLevelError() = dispatcher.runBlockingTest {
        val result = Result.Error(mockk())
        coEvery { liveStreamingUseCase.getBatteryLevel() } returns result
        storageLevelMock()

        viewModel.getCameraStatus()
        dispatcher.advanceTimeBy(1500)

        Assert.assertEquals(viewModel.batteryLevel.value?.getContent(), result)
        coVerify { liveStreamingUseCase.getBatteryLevel() }
    }

    @Test
    fun getStorageLevelsSuccess() = dispatcher.runBlockingTest {
        val freeStorage = 63456789
        val totalStorage = 67456789
        val scaleByte = 1024
        val usedStorage =
            (totalStorage.toDouble() / scaleByte) - (freeStorage.toDouble() / scaleByte)

        batteryLevelMock()
        coEvery { liveStreamingUseCase.getFreeStorage() } returns Result.Success(freeStorage.toString())
        coEvery { liveStreamingUseCase.getTotalStorage() } returns Result.Success(totalStorage.toString())

        viewModel.getCameraStatus()
        dispatcher.advanceTimeBy(500)
        val storage =
            (viewModel.storageLevel.value?.getContent() as Result.Success<List<Double>>).data
        Assert.assertEquals(storage[0].toInt(), freeStorage / scaleByte)
        Assert.assertEquals(storage[1].toInt(), usedStorage.toInt())
        Assert.assertEquals(storage[2].toInt(), totalStorage / scaleByte)

        coVerify {
            liveStreamingUseCase.getFreeStorage()
            liveStreamingUseCase.getTotalStorage()
        }
    }

    @Test
    fun getStorageLevelsFreeStorageError() = dispatcher.runBlockingTest {
        val result = Result.Error(mockk())
        batteryLevelMock()
        coEvery { liveStreamingUseCase.getFreeStorage() } returns result

        viewModel.getCameraStatus()
        dispatcher.advanceTimeBy(2000)
        Assert.assertEquals(viewModel.storageLevel.value?.getContent(), result)

        coVerify {
            liveStreamingUseCase.getFreeStorage()
        }
    }

    @Test
    fun getStorageLevelsTotalStorageError() = dispatcher.runBlockingTest {
        val result = Result.Error(mockk())
        batteryLevelMock()
        coEvery { liveStreamingUseCase.getFreeStorage() } returns Result.Success("63456789")
        coEvery { liveStreamingUseCase.getTotalStorage() } returns result

        viewModel.getCameraStatus()
        dispatcher.advanceTimeBy(2000)
        Assert.assertEquals(viewModel.storageLevel.value?.getContent(), result)

        coVerify {
            liveStreamingUseCase.getFreeStorage()
            liveStreamingUseCase.getTotalStorage()
        }
    }

    @Test
    fun getLowStorageShowed() {
        Assert.assertFalse(viewModel.wasLowStorageShowed)
    }

    @Test
    fun setAndGetLowStorageShowed() {
        viewModel.wasLowStorageShowed = true
        Assert.assertTrue(viewModel.wasLowStorageShowed)
    }

    @Test
    fun getLowBatteryShowed() {
        Assert.assertFalse(viewModel.wasLowBatteryShowed)
    }

    @Test
    fun setAndGetLowBatteryShowed() {
        viewModel.wasLowBatteryShowed = true
        Assert.assertTrue(viewModel.wasLowBatteryShowed)
    }
}
