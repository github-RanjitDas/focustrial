package com.lawmobile.presentation.ui.live.controls

import android.media.MediaActionSound
import com.lawmobile.domain.usecase.liveStreaming.LiveStreamingUseCase
import com.lawmobile.presentation.InstantExecutorExtension
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
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
internal class LiveControlsBaseViewModelTest {

    private val mediaActionSound: MediaActionSound = mockk()
    private val liveStreamingUseCase: LiveStreamingUseCase = mockk()

    private val liveControlsBaseViewModel: LiveControlsBaseViewModel by lazy {
        LiveControlsBaseViewModel(liveStreamingUseCase, mediaActionSound)
    }

    private val dispatcher = TestCoroutineDispatcher()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @Test
    fun testStartRecordVideoFlow() {
        val result = Result.Success(Unit)
        coEvery { liveStreamingUseCase.startRecordVideo() } returns result
        liveControlsBaseViewModel.startRecordVideo()
        Assert.assertEquals(
            result,
            liveControlsBaseViewModel.resultRecordVideoLiveData.value?.getContent()
        )
        coVerify { liveStreamingUseCase.startRecordVideo() }
    }

    @Test
    fun testStopRecordVideoFlow() {
        val result = Result.Success(Unit)
        coEvery { liveStreamingUseCase.stopRecordVideo() } returns result
        runBlocking {
            liveControlsBaseViewModel.stopRecordVideo()
            Assert.assertEquals(
                result,
                liveControlsBaseViewModel.resultStopVideoLiveData.value?.getContent()
            )
        }
        coVerify { liveStreamingUseCase.stopRecordVideo() }
    }

    @Test
    fun testTakePhotoFlow() {
        val result = Result.Success(Unit)
        coEvery { liveStreamingUseCase.takePhoto() } returns result
        liveControlsBaseViewModel.takePhoto()
        Assert.assertEquals(
            result,
            liveControlsBaseViewModel.resultTakePhotoLiveData.value?.getContent()
        )
        coVerify { liveStreamingUseCase.takePhoto() }
    }

    @Test
    fun playSoundTakePhoto() {
        every { mediaActionSound.play(any()) } just Runs
        liveControlsBaseViewModel.playSoundTakePhoto()
        verify { mediaActionSound.play(MediaActionSound.SHUTTER_CLICK) }
    }
}
