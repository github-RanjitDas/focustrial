package com.lawmobile.presentation.ui.videoPlayback

import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.domain.entities.DomainInformationVideo
import com.lawmobile.domain.entities.DomainMetadata
import com.lawmobile.domain.entities.DomainVideoMetadata
import com.lawmobile.domain.entities.FilesAssociatedByUser
import com.lawmobile.domain.entities.MetadataEvent
import com.lawmobile.domain.enums.CatalogTypes
import com.lawmobile.domain.usecase.liveStreaming.LiveStreamingUseCase
import com.lawmobile.domain.usecase.videoPlayback.VideoPlaybackUseCase
import com.lawmobile.presentation.ui.videoPlayback.state.VideoPlaybackState
import com.lawmobile.presentation.utils.VLCMediaPlayer
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@ExperimentalCoroutinesApi
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class VideoPlaybackViewModelTest {

    private val videoPlaybackUseCase: VideoPlaybackUseCase = mockk()
    private val vlcMediaPlayer: VLCMediaPlayer = mockk()
    private val informationManager: VideoInformationManager = mockk {
        every { setInformation(any()) } returns Unit
        every { setSpinners() } returns Unit
    }
    private val getMetadataEvents: LiveStreamingUseCase = mockk()

    private val viewModel by lazy {
        VideoPlaybackViewModel(
            videoPlaybackUseCase,
            vlcMediaPlayer,
            informationManager,
            getMetadataEvents
        )
    }

    private val dispatcher = TestCoroutineDispatcher()
    private val job by lazy { Job() }
    private val testScope by lazy { TestCoroutineScope() }

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        mockkObject(CameraInfo)
        mockkObject(FilesAssociatedByUser)
    }

    @AfterEach
    fun clean() {
        job.cancel()
        clearAllMocks()
    }

    private fun videoMetadataEventsMock() {
        every { informationManager.setSpinners() } returns Unit
        CameraInfo.metadataEvents = mutableListOf(mockk())
    }

    private fun mediaInformationMock() {
        coEvery {
            videoPlaybackUseCase.getInformationResourcesVideo(any())
        } returns Result.Success(mockk())
    }

    private fun videoInformationMock() {
        coEvery {
            videoPlaybackUseCase.getVideoMetadata(any(), any())
        } returns Result.Success(mockk())
    }

    @Test
    fun testGetMetadataEventsSuccess() {
        CameraInfo.metadataEvents = mutableListOf()
        val videoFile: DomainCameraFile = mockk(relaxed = true)
        val metadataEvents = listOf(
            mockk<MetadataEvent>(relaxed = true) {
                every { type } returns CatalogTypes.EVENT.value
            }
        )

        coEvery { getMetadataEvents.getCatalogInfo() } returns Result.Success(metadataEvents)
        mediaInformationMock()
        videoInformationMock()

        viewModel.getVideoPlaybackInfo(videoFile)
        dispatcher.advanceTimeBy(250)

        testScope.launch {
            Assert.assertEquals(metadataEvents, CameraInfo.metadataEvents)
        }
        coVerify {
            getMetadataEvents.getCatalogInfo()
            informationManager.setSpinners()
        }
    }

    @Test
    fun testGetMetadataEventsError() {
        every { CameraInfo.metadataEvents } returns mutableListOf()

        val videoFile: DomainCameraFile = mockk(relaxed = true)
        val exception = Exception()

        coEvery { getMetadataEvents.getCatalogInfo() } returns Result.Error(exception)
        mediaInformationMock()
        videoInformationMock()

        viewModel.getVideoPlaybackInfo(videoFile)
        dispatcher.advanceTimeBy(600)

        testScope.launch {
            Assert.assertEquals(exception, viewModel.videoInformationException.first())
        }
        coVerify { getMetadataEvents.getCatalogInfo() }
    }

    @Test
    fun testGetMediaInformationSuccess() = runBlockingTest {
        val videoFile: DomainCameraFile = mockk()
        val mediaInformation: DomainInformationVideo = mockk()

        videoMetadataEventsMock()
        coEvery {
            videoPlaybackUseCase.getInformationResourcesVideo(any())
        } returns Result.Success(mediaInformation)
        videoInformationMock()

        viewModel.getVideoPlaybackInfo(videoFile)
        testScope.launch {
            Assert.assertEquals(mediaInformation, viewModel.mediaInformation.value)
        }
        coVerify { videoPlaybackUseCase.getInformationResourcesVideo(any()) }
    }

    @Test
    fun testGetMediaInformationError() = runBlockingTest {
        val videoFile: DomainCameraFile = mockk()
        val exception = Exception()

        videoMetadataEventsMock()
        coEvery {
            videoPlaybackUseCase.getInformationResourcesVideo(any())
        } returns Result.Error(exception)
        videoInformationMock()

        viewModel.getVideoPlaybackInfo(videoFile)
        dispatcher.advanceTimeBy(600)

        testScope.launch {
            Assert.assertEquals(exception, viewModel.videoInformationException.first())
        }
        coVerify { videoPlaybackUseCase.getInformationResourcesVideo(any()) }
    }

    @Test
    fun testGetVideoInformationSuccess() {
        val videoFile: DomainCameraFile = mockk(relaxed = true)
        val videoMetadata: DomainMetadata = mockk(relaxed = true)
        val videoInformation: DomainVideoMetadata = mockk(relaxed = true) {
            every { metadata } returns videoMetadata
            every { associatedFiles } returns emptyList()
        }

        videoMetadataEventsMock()
        mediaInformationMock()
        coEvery {
            videoPlaybackUseCase.getVideoMetadata(any(), any())
        } returns Result.Success(videoInformation)

        viewModel.getVideoPlaybackInfo(videoFile)
        dispatcher.advanceTimeBy(250)

        testScope.launch {
            Assert.assertEquals(videoInformation, viewModel.videoInformation.value)
        }
        coVerify {
            videoPlaybackUseCase.getVideoMetadata(any(), any())
            informationManager.setInformation(any())
        }
    }

    @Test
    fun testGetVideoInformationError() {
        val videoFile: DomainCameraFile = mockk(relaxed = true)
        val exception = Exception()

        videoMetadataEventsMock()
        mediaInformationMock()
        coEvery {
            videoPlaybackUseCase.getVideoMetadata(any(), any())
        } returns Result.Error(exception)

        viewModel.getVideoPlaybackInfo(videoFile)
        dispatcher.advanceTimeBy(250)

        testScope.launch {
            Assert.assertEquals(exception, viewModel.videoInformationException.first())
        }
        coVerify {
            videoPlaybackUseCase.getVideoMetadata(any(), any())
        }
    }

    @Test
    fun testSaveVideoMetadataSuccess() {
        every { informationManager.getEditedInformation(any()) } returns mockk()
        coEvery { videoPlaybackUseCase.saveVideoMetadata(any()) } returns Result.Success(mockk())

        viewModel.saveVideoMetadata()

        testScope.launch {
            Assert.assertTrue(viewModel.updateMetadataResult.first() is Result.Success)
        }
        coVerify {
            informationManager.getEditedInformation(any())
            videoPlaybackUseCase.saveVideoMetadata(any())
        }
    }

    @Test
    fun testSaveVideoMetadataError() {
        val exception = Exception()
        every { informationManager.getEditedInformation(any()) } returns mockk()
        coEvery { videoPlaybackUseCase.saveVideoMetadata(any()) } returns Result.Error(exception)

        viewModel.saveVideoMetadata()

        testScope.launch {
            Assert.assertEquals(exception, viewModel.updateMetadataResult.first())
        }
        coVerify { videoPlaybackUseCase.saveVideoMetadata(any()) }
    }

    @Test
    fun theMetadataWasEditedTrue() {
        testGetVideoInformationSuccess()

        val videoMetadata: DomainMetadata =
            mockk(relaxed = true) {
                every { convertNullParamsToEmpty() } returns this
                every { isDifferentFrom(any()) } returns true
            }
        val videoInformation: DomainVideoMetadata =
            mockk(relaxed = true) { every { metadata } returns videoMetadata }

        every { informationManager.getEditedInformation(any()) } returns videoInformation

        Assert.assertTrue(viewModel.theMetadataWasEdited())

        verify {
            informationManager.getEditedInformation(any())
            videoMetadata.isDifferentFrom(any())
        }
    }

    @Test
    fun theMetadataWasEditedFalse() {
        testGetVideoInformationSuccess()
        every { FilesAssociatedByUser.value } returns mutableListOf()

        val videoMetadata: DomainMetadata =
            mockk(relaxed = true) {
                every { convertNullParamsToEmpty() } returns this
                every { isDifferentFrom(any()) } returns false
            }
        val videoInformation: DomainVideoMetadata =
            mockk(relaxed = true) { every { metadata } returns videoMetadata }

        every { informationManager.getEditedInformation(any()) } returns videoInformation

        Assert.assertFalse(viewModel.theMetadataWasEdited())

        verify {
            informationManager.getEditedInformation(any())
            videoMetadata.isDifferentFrom(any())
        }
    }

    @Test
    fun theMetadataWasEditedTrueCachedMetadataNull() {
        testGetVideoInformationError()
        val videoMetadata: DomainMetadata =
            mockk(relaxed = true) {
                every { hasAnyInformation() } returns true
                every { convertNullParamsToEmpty() } returns this
            }
        val videoInformation: DomainVideoMetadata =
            mockk(relaxed = true) { every { metadata } returns videoMetadata }

        every { informationManager.getEditedInformation(any()) } returns videoInformation
        Assert.assertTrue(viewModel.theMetadataWasEdited())
        verify {
            informationManager.getEditedInformation(any())
            videoMetadata.hasAnyInformation()
        }
    }

    @Test
    fun theMetadataWasEditedFalseCachedMetadataNull() {
        testGetVideoInformationError()
        val videoMetadata: DomainMetadata =
            mockk(relaxed = true) {
                every { hasAnyInformation() } returns false
                every { convertNullParamsToEmpty() } returns this
            }
        val videoInformation: DomainVideoMetadata =
            mockk(relaxed = true) { every { metadata } returns videoMetadata }

        every { informationManager.getEditedInformation(any()) } returns videoInformation
        Assert.assertFalse(viewModel.theMetadataWasEdited())
        verify {
            informationManager.getEditedInformation(any())
            videoMetadata.hasAnyInformation()
        }
    }

    @Test
    fun isAssociateDialogOpen() {
        Assert.assertFalse(viewModel.isAssociateDialogOpen)
    }

    @Test
    fun setAssociateDialogOpen() {
        viewModel.isAssociateDialogOpen = true
        Assert.assertTrue(viewModel.isAssociateDialogOpen)
    }

    @Test
    fun getState() {
        Assert.assertTrue(viewModel.getState() is VideoPlaybackState.Default)
    }

    @Test
    fun setState() {
        viewModel.setState(VideoPlaybackState.FullScreen)
        Assert.assertTrue(viewModel.getState() is VideoPlaybackState.FullScreen)
    }

    @Test
    fun getMediaPlayer() {
        Assert.assertEquals(vlcMediaPlayer, viewModel.mediaPlayer)
    }

    @Test
    fun getInformationManager() {
        Assert.assertEquals(informationManager, viewModel.informationManager)
    }
}
