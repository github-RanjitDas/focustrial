package com.lawmobile.presentation.ui.live.statusBar

import com.lawmobile.domain.usecase.liveStreaming.LiveStreamingUseCase
import com.lawmobile.presentation.InstantExecutorExtension
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
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
internal class LiveStatusBarBaseViewModelTest {

    private val liveStreamingUseCase: LiveStreamingUseCase = mockk()

    private val liveStatusBarBaseViewModel: LiveStatusBarBaseViewModel by lazy {
        LiveStatusBarBaseViewModel(liveStreamingUseCase)
    }

    @ExperimentalCoroutinesApi
    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @Test
    fun testGetCatalogInfoSuccess() {
        coEvery { liveStreamingUseCase.getCatalogInfo() } returns Result.Success(
            mockk()
        )
        runBlocking {
            liveStatusBarBaseViewModel.getCatalogInfo()
            Assert.assertTrue(liveStatusBarBaseViewModel.catalogInfoLiveData.value is Result.Success)
        }
        coVerify { liveStreamingUseCase.getCatalogInfo() }
    }

    @Test
    fun testGetCatalogInfoError() {
        coEvery { liveStreamingUseCase.getCatalogInfo() } returns Result.Error(mockk())
        runBlocking {
            liveStatusBarBaseViewModel.getCatalogInfo()
            delay(1500)
            Assert.assertTrue(liveStatusBarBaseViewModel.catalogInfoLiveData.value is Result.Error)
        }
        coVerify { liveStreamingUseCase.getCatalogInfo() }
    }

    @Test
    fun getBatteryLevelSuccess() {
        val result = Result.Success(23)
        coEvery { liveStreamingUseCase.getBatteryLevel() } returns result
        runBlocking {
            liveStatusBarBaseViewModel.getBatteryLevel()
            Assert.assertEquals(
                liveStatusBarBaseViewModel.batteryLevelLiveData.value?.getContent(),
                result
            )
        }
        coVerify { liveStreamingUseCase.getBatteryLevel() }
    }

    @Test
    fun getBatteryLevelError() {
        val result = Result.Error(mockk())
        coEvery { liveStreamingUseCase.getBatteryLevel() } returns result
        runBlocking {
            liveStatusBarBaseViewModel.getBatteryLevel()
            delay(1500)
            Assert.assertEquals(
                liveStatusBarBaseViewModel.batteryLevelLiveData.value?.getContent(),
                result
            )
        }
        coVerify { liveStreamingUseCase.getBatteryLevel() }
    }

    @Test
    fun getStorageLevelsSuccess() {
        val freeStorage = 63456789
        val totalStorage = 67456789
        val scaleByte = 1024
        val usedStorage =
            (totalStorage.toDouble() / scaleByte) - (freeStorage.toDouble() / scaleByte)

        coEvery { liveStreamingUseCase.getFreeStorage() } returns Result.Success(freeStorage.toString())
        coEvery { liveStreamingUseCase.getTotalStorage() } returns Result.Success(totalStorage.toString())

        runBlocking {
            liveStatusBarBaseViewModel.getStorageLevels()
            delay(500)
            val storage =
                (liveStatusBarBaseViewModel.storageLiveData.value?.getContent() as Result.Success<List<Double>>).data
            Assert.assertEquals(storage[0].toInt(), freeStorage / scaleByte)
            Assert.assertEquals(storage[1].toInt(), usedStorage.toInt())
            Assert.assertEquals(storage[2].toInt(), totalStorage / scaleByte)
        }
        coVerify {
            liveStreamingUseCase.getFreeStorage()
            liveStreamingUseCase.getTotalStorage()
        }
    }

    @Test
    fun getStorageLevelsFreeStorageError() {
        val result = Result.Error(mockk())
        coEvery { liveStreamingUseCase.getFreeStorage() } returns result
        runBlocking {
            liveStatusBarBaseViewModel.getStorageLevels()
            delay(2000)
            Assert.assertEquals(liveStatusBarBaseViewModel.storageLiveData.value?.getContent(), result)
        }
        coVerify {
            liveStreamingUseCase.getFreeStorage()
        }
    }

    @Test
    fun getStorageLevelsTotalStorageError() {
        val result = Result.Error(mockk())
        coEvery { liveStreamingUseCase.getFreeStorage() } returns Result.Success("63456789")
        coEvery { liveStreamingUseCase.getTotalStorage() } returns result
        runBlocking {
            liveStatusBarBaseViewModel.getStorageLevels()
            delay(2000)
            Assert.assertEquals(liveStatusBarBaseViewModel.storageLiveData.value?.getContent(), result)
        }
        coVerify {
            liveStreamingUseCase.getFreeStorage()
            liveStreamingUseCase.getTotalStorage()
        }
    }
}
