package com.lawmobile.presentation.ui.live.controls

import android.media.MediaActionSound
import com.lawmobile.domain.usecase.checkCameraRecordingVideo.CheckCameraRecordingVideo
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ExtendWith(InstantExecutorExtension::class)
internal class ControlsBaseViewModelTest {

    private val mediaActionSound: MediaActionSound = mockk()
    private val liveStreamingUseCase: LiveStreamingUseCase = mockk()
    private val checkCameraRecordingVideo: CheckCameraRecordingVideo = mockk()

    private val viewModel: ControlsBaseViewModel by lazy {
        ControlsBaseViewModel(liveStreamingUseCase, mediaActionSound, checkCameraRecordingVideo)
    }

    private val dispatcher = TestCoroutineDispatcher()
    private val job by lazy { Job() }
    private val testScope = TestCoroutineScope(dispatcher + job)

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @AfterEach
    fun clean() {
        job.cancel()
    }

    @Test
    fun testStartRecordVideoSuccess() {
        val result = Result.Success(Unit)
        coEvery { liveStreamingUseCase.startRecordVideo() } returns result
        viewModel.startRecordVideo()
        Assert.assertEquals(
            result,
            viewModel.resultRecordVideoLiveData.value?.getContent()
        )
        coVerify { liveStreamingUseCase.startRecordVideo() }
    }

    @Test
    fun testStartRecordVideoError() {
        val result = Result.Error(Exception())
        coEvery { liveStreamingUseCase.startRecordVideo() } returns result
        viewModel.startRecordVideo()
        Assert.assertEquals(
            result,
            viewModel.resultRecordVideoLiveData.value?.getContent()
        )
        coVerify { liveStreamingUseCase.startRecordVideo() }
    }

    @Test
    fun testStopRecordVideoSuccess() {
        val result = Result.Success(Unit)
        coEvery { liveStreamingUseCase.stopRecordVideo() } returns result
        runBlocking {
            viewModel.stopRecordVideo()
            Assert.assertEquals(
                result,
                viewModel.resultStopVideoLiveData.value?.getContent()
            )
        }
        coVerify { liveStreamingUseCase.stopRecordVideo() }
    }

    @Test
    fun testStopRecordVideoError() {
        val result = Result.Error(Exception())
        coEvery { liveStreamingUseCase.stopRecordVideo() } returns result
        runBlocking {
            viewModel.stopRecordVideo()
            Assert.assertEquals(
                result,
                viewModel.resultStopVideoLiveData.value?.getContent()
            )
        }
        coVerify { liveStreamingUseCase.stopRecordVideo() }
    }

    @Test
    fun testTakePhotoFlow() {
        val result = Result.Success(Unit)
        coEvery { liveStreamingUseCase.takePhoto() } returns result
        viewModel.takePhoto()
        Assert.assertEquals(
            result,
            viewModel.resultTakePhotoLiveData.value?.getContent()
        )
        coVerify { liveStreamingUseCase.takePhoto() }
    }

    @Test
    fun playSoundTakePhoto() {
        every { mediaActionSound.play(any()) } just Runs
        viewModel.playSoundTakePhoto()
        verify { mediaActionSound.play(MediaActionSound.SHUTTER_CLICK) }
    }

    @Test
    fun startRecordAudioFlow() {
        val result = Result.Success(Unit)

        runBlocking {
            viewModel.startRecordAudio()
            Assert.assertEquals(
                result,
                viewModel.resultRecordAudioLiveData.value?.getContent()
            )
        }
    }

    @Test
    fun stopRecordAudioFlow() {
        val result = Result.Success(Unit)

        runBlocking {
            viewModel.stopRecordAudio()
            Assert.assertEquals(
                result,
                viewModel.resultStopAudioLiveData.value?.getContent()
            )
        }
    }

    @Test
    fun checkIfCameraIsRecordingVideoTrue() = runBlockingTest {
        coEvery { checkCameraRecordingVideo() } returns true
        viewModel.checkCameraIsRecordingVideo()
        testScope.launch {
            Assert.assertTrue(viewModel.isCameraRecordingVideo.first())
        }
        coVerify { checkCameraRecordingVideo() }
    }

    @Test
    fun checkIfCameraIsRecordingVideoFalse() = runBlockingTest {
        coEvery { checkCameraRecordingVideo() } returns false
        viewModel.checkCameraIsRecordingVideo()
        testScope.launch {
            Assert.assertFalse(viewModel.isCameraRecordingVideo.first())
        }
        coVerify { checkCameraRecordingVideo() }
    }
}
