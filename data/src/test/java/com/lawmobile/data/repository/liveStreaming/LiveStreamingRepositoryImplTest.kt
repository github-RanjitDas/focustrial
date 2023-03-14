package com.lawmobile.data.repository.liveStreaming

import com.lawmobile.body_cameras.enums.CatalogTypesDto
import com.lawmobile.data.datasource.remote.liveStreaming.LiveStreamingRemoteDataSource
import com.lawmobile.domain.entities.FileList
import com.lawmobile.domain.enums.CatalogTypes
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@ExperimentalCoroutinesApi
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LiveStreamingRepositoryImplTest {

    private val liveStreamingRemoteDataSource: LiveStreamingRemoteDataSource = mockk()
    private val dispatcher = TestCoroutineDispatcher()

    private val liveStreamingRepositoryImpl: LiveStreamingRepositoryImpl by lazy {
        LiveStreamingRepositoryImpl(liveStreamingRemoteDataSource)
    }

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        clearAllMocks()
    }

    @Test
    fun testGetUrlForLiveStreamVerifyFlow() {
        every { liveStreamingRemoteDataSource.getUrlForLiveStream() } returns "xyz"
        liveStreamingRepositoryImpl.getUrlForLiveStream()
        verify { liveStreamingRemoteDataSource.getUrlForLiveStream() }
    }

    @Test
    fun testGetUrlForLiveStreamVerifyValue() {
        every { liveStreamingRemoteDataSource.getUrlForLiveStream() } returns "xyz"
        val url = liveStreamingRepositoryImpl.getUrlForLiveStream()
        Assert.assertEquals("xyz", url)
    }

    @Test
    fun testGetUrlForLiveStreamVerifyValueEmpty() {
        every { liveStreamingRemoteDataSource.getUrlForLiveStream() } returns ""
        val url = liveStreamingRemoteDataSource.getUrlForLiveStream()
        Assert.assertEquals(url, "")
    }

    @Test
    fun testStartRecordVideoFlow() = dispatcher.runBlockingTest {
        coEvery { liveStreamingRemoteDataSource.startRecordVideo() } returns Result.Success(Unit)
        liveStreamingRepositoryImpl.startRecordVideo()
        dispatcher.advanceTimeBy(RECORD_OPERATION_TIME)
        coVerify { liveStreamingRemoteDataSource.startRecordVideo() }
    }

    @Test
    fun testStartRecordVideoSuccess() = dispatcher.runBlockingTest {
        val expectedResult = Result.Success(Unit)
        coEvery { liveStreamingRemoteDataSource.startRecordVideo() } returns expectedResult
        val actualResult = liveStreamingRepositoryImpl.startRecordVideo()
        dispatcher.advanceTimeBy(RECORD_OPERATION_TIME)
        Assert.assertEquals(expectedResult, actualResult)
    }

    @Test
    fun testStartRecordVideoFailed() = dispatcher.runBlockingTest {
        val expectedResult = Result.Error(Exception("Message"))
        coEvery { liveStreamingRemoteDataSource.startRecordVideo() } returns expectedResult
        val actualResult = liveStreamingRepositoryImpl.startRecordVideo()
        dispatcher.advanceTimeBy(RECORD_OPERATION_TIME)
        Assert.assertEquals(expectedResult, actualResult)
    }

    @Test
    fun testStopRecordVideoFlow() = dispatcher.runBlockingTest {
        coEvery { liveStreamingRemoteDataSource.stopRecordVideo() } returns Result.Success(Unit)
        liveStreamingRepositoryImpl.stopRecordVideo()
        dispatcher.advanceTimeBy(RECORD_OPERATION_TIME)
        coVerify { liveStreamingRemoteDataSource.stopRecordVideo() }
    }

    @Test
    fun testStopRecordVideoSuccess() = dispatcher.runBlockingTest {
        val expectedResult = Result.Success(Unit)
        coEvery { liveStreamingRemoteDataSource.stopRecordVideo() } returns expectedResult
        FileList.videoList = listOf(mockk(relaxed = true))
        val actualResult = liveStreamingRepositoryImpl.stopRecordVideo()
        dispatcher.advanceTimeBy(RECORD_OPERATION_TIME)
        Assert.assertEquals(expectedResult, actualResult)
        Assert.assertTrue(FileList.videoList.isEmpty())
    }

    @Test
    fun testStopRecordVideoFailed() = dispatcher.runBlockingTest {
        val expectedResult = Result.Error(Exception("Message"))
        coEvery { liveStreamingRemoteDataSource.stopRecordVideo() } returns expectedResult
        val actualResult = liveStreamingRepositoryImpl.stopRecordVideo()
        dispatcher.advanceTimeBy(RECORD_OPERATION_TIME)
        Assert.assertEquals(expectedResult, actualResult)
    }

    @Test
    fun testTakePhotoFlow() = runBlockingTest {
        coEvery { liveStreamingRemoteDataSource.takePhoto() } returns Result.Success(Unit)
        liveStreamingRepositoryImpl.takePhoto()
        coVerify { liveStreamingRemoteDataSource.takePhoto() }
    }

    @Test
    fun testTakePhotoSuccess() = runBlockingTest {
        val result = Result.Success(Unit)
        coEvery { liveStreamingRemoteDataSource.takePhoto() } returns result
        FileList.imageList = listOf(mockk(relaxed = true))
        Assert.assertEquals(liveStreamingRepositoryImpl.takePhoto(), result)
        Assert.assertTrue(FileList.imageList.isEmpty())
    }

    @Test
    fun testTakePhotoFailed() = runBlockingTest {
        val result = Result.Error(Exception(""))
        coEvery { liveStreamingRemoteDataSource.takePhoto() } returns result
        Assert.assertEquals(liveStreamingRepositoryImpl.takePhoto(), result)
    }

    @Test
    fun testGetCatalogInfoSuccess() = runBlockingTest {
        coEvery {
            liveStreamingRemoteDataSource.getCatalogInfo(CatalogTypesDto.CATEGORIES)
        } returns Result.Success(listOf(mockk(relaxed = true)))

        val result = liveStreamingRepositoryImpl.getCatalogInfo(CatalogTypes.CATEGORIES)
        Assert.assertTrue(result is Result.Success)

        coVerify { liveStreamingRemoteDataSource.getCatalogInfo(CatalogTypesDto.CATEGORIES) }
    }

    @Test
    fun testGetCatalogInfoError() = runBlockingTest {
        coEvery { liveStreamingRemoteDataSource.getCatalogInfo(CatalogTypesDto.CATEGORIES) } returns Result.Error(mockk())
        val result = liveStreamingRepositoryImpl.getCatalogInfo(CatalogTypes.CATEGORIES)
        Assert.assertTrue(result is Result.Error)
        coVerify { liveStreamingRemoteDataSource.getCatalogInfo(CatalogTypesDto.CATEGORIES) }
    }

    @Test
    fun testGetBatteryLevel() = runBlockingTest {
        val result = Result.Success(10)
        coEvery { liveStreamingRemoteDataSource.getBatteryLevel() } returns result
        val response = liveStreamingRepositoryImpl.getBatteryLevel()
        Assert.assertEquals(response, result)
        coVerify { liveStreamingRemoteDataSource.getBatteryLevel() }
    }

    @Test
    fun testGetFreeStorage() = runBlockingTest {
        val result = Result.Success("10000")
        coEvery { liveStreamingRemoteDataSource.getFreeStorage() } returns result
        val response = liveStreamingRepositoryImpl.getFreeStorage()
        Assert.assertEquals(response, result)
        coVerify { liveStreamingRemoteDataSource.getFreeStorage() }
    }

    @Test
    fun testGetTotalStorage() = runBlockingTest {
        val result = Result.Success("10000")
        coEvery { liveStreamingRemoteDataSource.getTotalStorage() } returns result
        val response = liveStreamingRepositoryImpl.getTotalStorage()
        Assert.assertEquals(response, result)
        coVerify { liveStreamingRemoteDataSource.getTotalStorage() }
    }

    @Test
    fun disconnectCameraFlow() = runBlockingTest {
        coEvery { liveStreamingRemoteDataSource.disconnectCamera() } returns Result.Success(Unit)
        liveStreamingRepositoryImpl.disconnectCamera()
        coVerify { liveStreamingRemoteDataSource.disconnectCamera() }
    }

    @Test
    fun testIsFolderOnCameraTrue() = runBlockingTest {
        coEvery { liveStreamingRemoteDataSource.isFolderOnCamera(any()) } returns true
        liveStreamingRepositoryImpl.isFolderOnCamera("SAFE")
        coVerify { liveStreamingRemoteDataSource.isFolderOnCamera(any()) }
    }

    @Test
    fun testIsFolderOnCameraFalse() = runBlockingTest {
        coEvery { liveStreamingRemoteDataSource.isFolderOnCamera(any()) } returns false
        liveStreamingRepositoryImpl.isFolderOnCamera("SAFE")
        coVerify { liveStreamingRemoteDataSource.isFolderOnCamera(any()) }
    }

    companion object {
        private const val RECORD_OPERATION_TIME = 1001L
    }
}
