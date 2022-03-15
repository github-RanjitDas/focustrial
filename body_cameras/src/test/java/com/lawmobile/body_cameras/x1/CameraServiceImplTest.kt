package com.lawmobile.body_cameras.x1

import com.lawmobile.body_cameras.CameraServiceImpl
import com.lawmobile.body_cameras.cache.CameraServiceCache
import com.lawmobile.body_cameras.constants.CameraConstants.COMPLEMENT_LIVE
import com.lawmobile.body_cameras.constants.CameraConstants.PROTOCOL_LIVE
import com.lawmobile.body_cameras.entities.AudioInformation
import com.lawmobile.body_cameras.entities.BWCConnectionParams.hostnameToConnect
import com.lawmobile.body_cameras.entities.CameraFile
import com.lawmobile.body_cameras.entities.FileResponseWithErrors
import com.lawmobile.body_cameras.entities.PhotoInformation
import com.lawmobile.body_cameras.entities.VideoInformation
import com.lawmobile.body_cameras.enums.FileListType
import com.lawmobile.body_cameras.enums.XCameraStatus
import com.lawmobile.body_cameras.utils.CommandHelper
import com.lawmobile.body_cameras.utils.FileInformationHelper
import com.lawmobile.body_cameras.utils.MetadataHelper
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.Test

internal class CameraServiceImplTest {

    private val fileInformationHelper: FileInformationHelper = mockk(relaxed = true)
    private val commandHelper: CommandHelper = mockk(relaxed = true)
    private val metadataHelper: MetadataHelper = mockk(relaxed = true)
    private val x1CameraService =
        CameraServiceImpl(fileInformationHelper, commandHelper, metadataHelper)

    @Test
    fun loadPairingCameraSuccess() {

        coEvery { commandHelper.connectCMDSocket(any()) } returns Result.Success(Unit)
        coEvery { commandHelper.getSessionToken() } returns Result.Success(1)
        coEvery { commandHelper.isCommandSuccess(any()) } returns Result.Success(Unit)
        coEvery { commandHelper.getResponseParam(any()) } returns Result.Success(XCameraStatus.VIEW_FINDER.value)

        runBlocking { x1CameraService.loadPairingCamera("", "") }

        coVerify {
            commandHelper.connectCMDSocket(any())
            commandHelper.getSessionToken()
            commandHelper.isCommandSuccess(any())
            commandHelper.getResponseParam(any())
        }
    }

    @Test
    fun loadPairingCameraSocketError() {
        coEvery { commandHelper.connectCMDSocket(any()) } returns Result.Error(mockk())

        runBlocking { x1CameraService.loadPairingCamera("", "") }

        coVerify {
            commandHelper.connectCMDSocket(any())
        }
    }

    @Test
    fun loadPairingCameraTokenIdError() {
        coEvery { commandHelper.connectCMDSocket(any()) } returns Result.Success(Unit)
        coEvery { commandHelper.getSessionToken() } returns Result.Error(mockk())

        runBlocking { x1CameraService.loadPairingCamera("", "") }

        coVerify {
            commandHelper.connectCMDSocket(any())
            commandHelper.getSessionToken()
        }
    }

    @Test
    fun loadPairingCameraCommandError() {
        coEvery { commandHelper.connectCMDSocket(any()) } returns Result.Success(Unit)
        coEvery { commandHelper.getSessionToken() } returns Result.Success(1)
        coEvery { commandHelper.isCommandSuccess(any()) } returns Result.Error(mockk())
        coEvery { commandHelper.getResponseParam(any()) } returns Result.Success(XCameraStatus.VIEW_FINDER.value)

        runBlocking { x1CameraService.loadPairingCamera("", "") }

        coVerify {
            commandHelper.connectCMDSocket(any())
            commandHelper.getSessionToken()
            commandHelper.isCommandSuccess(any())
            commandHelper.getResponseParam(any())
        }
    }

    @Test
    fun getUserResponseSuccess() {
        val byteArray = ByteArray(1)
        coEvery { fileInformationHelper.getFileInformation(any()) } returns Result.Success(
            byteArray
        )

        runBlocking { x1CameraService.getUserResponse() }

        coVerify {
            fileInformationHelper.getFileInformation(any())
        }
    }

    @Test
    fun getUserResponseError() {
        coEvery { fileInformationHelper.getFileInformation(any()) } returns Result.Error(mockk())

        runBlocking { x1CameraService.getUserResponse() }

        coVerify {
            fileInformationHelper.getFileInformation(any())
        }
    }

    @Test
    fun getUrlForLiveStream() {
        Assert.assertEquals(
            x1CameraService.getUrlForLiveStream(),
            PROTOCOL_LIVE + hostnameToConnect + COMPLEMENT_LIVE
        )
    }

    @Test
    fun takePhotoCommandSuccess() {
        coEvery { commandHelper.isCommandSuccess(any()) } returns Result.Success(Unit)

        runBlocking { x1CameraService.takePhoto() }

        coVerify {
            commandHelper.isCommandSuccess(any())
        }
    }

    @Test
    fun takePhotoCommandError() {
        coEvery { commandHelper.isCommandSuccess(any()) } returns Result.Error(mockk())
        coEvery { commandHelper.getNumberOfSnapshots() } returns Result.Error(mockk())

        runBlocking {
            val response = x1CameraService.takePhoto()
            Assert.assertTrue(response is Result.Error)
        }

        coVerify {
            commandHelper.isCommandSuccess(any())
            commandHelper.getNumberOfSnapshots()
        }
    }

    @Test
    fun startRecordVideo() {
        coEvery { commandHelper.isCommandSuccess(any()) } returns Result.Success(Unit)

        runBlocking { x1CameraService.startRecordVideo() }

        coVerify {
            commandHelper.isCommandSuccess(any())
        }
    }

    @Test
    fun stopRecordVideo() {
        coEvery { commandHelper.isCommandSuccess(any()) } returns Result.Success(Unit)

        runBlocking { x1CameraService.stopRecordVideo() }

        coVerify {
            commandHelper.isCommandSuccess(any())
        }
    }

    @Test
    fun isCameraConnectedTrue() {
        hostnameToConnect = ""
        val isConnected = x1CameraService.isCameraConnected("")
        Assert.assertEquals(isConnected, true)
    }

    @Test
    fun isCameraConnectedFalse() {
        hostnameToConnect = ""
        val isConnected = x1CameraService.isCameraConnected("x")
        Assert.assertEquals(isConnected, false)
    }

    @Test
    fun disconnectCamera() {
        coEvery { commandHelper.isCommandSuccess(any()) } returns Result.Success(Unit)

        runBlocking { x1CameraService.disconnectCamera() }

        coVerify {
            commandHelper.isCommandSuccess(any())
        }
    }

    @Test
    fun getListOfVideosSuccess() {
        CameraServiceCache.videos = emptyList()
        coEvery {
            commandHelper.getMediaListOfType(FileListType.VIDEO)
        } returns Result.Success(mockk(relaxed = true))

        coEvery { commandHelper.getResponseParam(any()) } returns Result.Success("2")
        runBlocking { x1CameraService.getListOfVideos() }

        coVerify {
            commandHelper.getMediaListOfType(FileListType.VIDEO)
        }
    }

    @Test
    fun getListOfVideosError() {
        CameraServiceCache.videos = emptyList()
        coEvery { commandHelper.getMediaListOfType(FileListType.VIDEO) } returns Result.Error(mockk())
        coEvery { commandHelper.getResponseParam(any()) } returns Result.Success("2")
        runBlocking { x1CameraService.getListOfVideos() }

        coVerify {
            commandHelper.getMediaListOfType(FileListType.VIDEO)
        }
    }

    @Test
    fun getListOfImagesSuccess() {
        CameraServiceCache.snapshots = emptyList()
        coEvery { commandHelper.getMediaListOfType(FileListType.SNAPSHOT) } returns Result.Success(
            mockk(relaxed = true)
        )
        coEvery { commandHelper.getResponseParam(any()) } returns Result.Success("2")
        runBlocking { x1CameraService.getListOfImages() }

        coVerify {
            commandHelper.getMediaListOfType(FileListType.SNAPSHOT)
        }
    }

    @Test
    fun getListOfImagesError() {
        CameraServiceCache.snapshots = emptyList()
        coEvery { commandHelper.getMediaListOfType(FileListType.SNAPSHOT) } returns Result.Error(
            mockk()
        )
        coEvery { commandHelper.getResponseParam(any()) } returns Result.Success("2")
        runBlocking { x1CameraService.getListOfImages() }
        CameraServiceCache.snapshots = emptyList()
        coVerify { commandHelper.getMediaListOfType(FileListType.SNAPSHOT) }
    }

    @Test
    fun getListOfAudiosSuccess() {
        CameraServiceCache.audios = emptyList()
        coEvery { commandHelper.getMediaListOfType(FileListType.AUDIO) } returns Result.Success(
            mockk(relaxed = true)
        )
        coEvery { commandHelper.getResponseParam(any()) } returns Result.Success("2")
        runBlocking { x1CameraService.getListOfAudios() }

        coVerify {
            commandHelper.getMediaListOfType(FileListType.AUDIO)
        }
    }

    @Test
    fun getListOfAudiosError() {
        CameraServiceCache.audios = emptyList()
        coEvery { commandHelper.getMediaListOfType(FileListType.AUDIO) } returns Result.Error(mockk())
        coEvery { commandHelper.getResponseParam(any()) } returns Result.Success("2")
        runBlocking { x1CameraService.getListOfAudios() }
        CameraServiceCache.audios = emptyList()
        coVerify { commandHelper.getMediaListOfType(FileListType.AUDIO) }
    }

    @Test
    fun getInformationResourcesVideo() {
        coEvery {
            commandHelper.getInfoMediaFromCMDSocket(any())
        } returns Result.Success(mockk())

        runBlocking { x1CameraService.getInformationResourcesVideo(mockk(relaxed = true)) }

        coVerify {
            commandHelper.getInfoMediaFromCMDSocket(any())
        }
    }

    @Test
    fun getImageBytes() {
        coEvery {
            fileInformationHelper.getFileInformation(any())
        } returns Result.Success(ByteArray(1))
        runBlocking { x1CameraService.getImageBytes(mockk(relaxed = true)) }
        coVerify { fileInformationHelper.getFileInformation(any()) }
    }

    @Test
    fun getAudioBytes() {
        coEvery {
            fileInformationHelper.getFileInformation(any())
        } returns Result.Success(ByteArray(1))
        runBlocking { x1CameraService.getAudioBytes(mockk(relaxed = true)) }
        coVerify { fileInformationHelper.getFileInformation(any()) }
    }

    @Test
    fun getCatalogInfoSuccess() {
        val file = "0--Default--0|0|0|0|0|0|0|0|0|0|0|0|0|0--||||||||||||\n" +
            "7--Other--0|0|0|0|0|0|0|0|0|0|0|0|0|0--||||||||||||\n" +
            "9--Jenn main--0|0|1|0|1|0|0|0|0|0|0|0|0|0--||||||||||||\n" +
            "11--Cloud Sub--0|0|0|0|0|0|0|0|0|0|0|0|0|0--||||||||||||\n" +
            "----------------------\n" +
            "1--White\n" +
            "2--Black\n" +
            "----------------------\n" +
            "1--Other\n" +
            "2--Female\n" +
            "3--Male\n"

        coEvery { fileInformationHelper.getFileInformation(any()) } returns Result.Success(file.toByteArray())
        coEvery { commandHelper.getNumberOfSnapshots() } returns Result.Success(12)

        runBlocking {
            val response = x1CameraService.getCatalogInfo()
            Assert.assertTrue(response is Result.Success)
        }

        coVerify {
            fileInformationHelper.getFileInformation(any())
        }
    }

    @Test
    fun getCatalogInfoError() {
        coEvery { fileInformationHelper.getFileInformation(any()) } returns Result.Error(Exception(""))
        coEvery { commandHelper.getNumberOfSnapshots() } returns Result.Success(12)

        runBlocking {
            val response = x1CameraService.getCatalogInfo()
            Assert.assertTrue(response is Result.Error)
        }

        coVerify {
            fileInformationHelper.getFileInformation(any())
        }
    }

    @Test
    fun saveVideoMetadataSuccess() {
        val videoInformation: VideoInformation = mockk(relaxed = true)

        coEvery {
            metadataHelper.saveVideoInformation(videoInformation)
        } returns Result.Success(Unit)

        runBlocking { x1CameraService.saveVideoMetadata(videoInformation) }

        coVerify { metadataHelper.saveVideoInformation(videoInformation) }
    }

    @Test
    fun saveVideoMetadataError() {
        val videoInformation: VideoInformation = mockk(relaxed = true)

        coEvery {
            metadataHelper.saveVideoInformation(videoInformation)
        } returns Result.Error(mockk())

        runBlocking { x1CameraService.saveVideoMetadata(videoInformation) }

        coVerify { metadataHelper.saveVideoInformation(videoInformation) }
    }

    @Test
    fun getVideoMetadataSuccess() {
        val bytes = "1234".toByteArray()
        coEvery { fileInformationHelper.getFileInformation(any()) } returns Result.Success(bytes)
        runBlocking { x1CameraService.getVideoMetadata("", "") }
        coVerify { fileInformationHelper.getFileInformation(any()) }
    }

    @Test
    fun getVideoMetadataSuccessNoInfo() {
        val bytes = "".toByteArray()
        coEvery { fileInformationHelper.getFileInformation(any()) } returns Result.Success(bytes)

        runBlocking { x1CameraService.getVideoMetadata("", "") }

        coVerify {
            fileInformationHelper.getFileInformation(any())
        }
    }

    @Test
    fun getVideoMetadataError() {
        val bytes = "1234".toByteArray()
        coEvery { fileInformationHelper.getFileInformation(any()) } returns Result.Success(bytes)
        runBlocking { x1CameraService.getVideoMetadata("", "") }
        coVerify { fileInformationHelper.getFileInformation(any()) }
    }

    @Test
    fun getVideoMetadataBytesError() {
        coEvery { fileInformationHelper.getFileInformation(any()) } returns Result.Error(mockk())

        runBlocking { x1CameraService.getVideoMetadata("", "") }

        coVerify { fileInformationHelper.getFileInformation(any()) }
    }

    @Test
    fun testGetMetadataOfPhotosFlow() {
        val bytes = "qweqwe".toByteArray()
        coEvery { fileInformationHelper.getFileInformation(any()) } returns Result.Success(bytes)
        coEvery { metadataHelper.getPhotoInformationList(bytes) } returns Result.Success(emptyList())
        runBlocking { x1CameraService.getMetadataOfPhotos() }
        coVerify { fileInformationHelper.getFileInformation(any()) }
        coVerify { metadataHelper.getPhotoInformationList(bytes) }
    }

    @Test
    fun testGetMetadataOfPhotosSuccess() {
        val bytes = "qweqwe".toByteArray()
        coEvery { fileInformationHelper.getFileInformation(any()) } returns Result.Success(bytes)
        coEvery { metadataHelper.getPhotoInformationList(bytes) } returns Result.Success(
            listOf(
                PhotoInformation("", "")
            )
        )
        runBlocking {
            val response = x1CameraService.getMetadataOfPhotos()
            Assert.assertTrue(response is Result.Success)
            val data = (response as Result.Success).data
            Assert.assertEquals(data.size, 1)
        }
    }

    @Test
    fun testGetMetadataOfPhotosError() {

        coEvery { fileInformationHelper.getFileInformation(any()) } returns Result.Error(
            Exception("")
        )
        runBlocking {
            val response = x1CameraService.getMetadataOfPhotos()
            Assert.assertTrue(response is Result.Error)
        }
    }

    @Test
    fun testGetPhotoMetadataFlow() {
        val bytes = "qweqwe".toByteArray()
        coEvery { fileInformationHelper.getFileInformation(any()) } returns Result.Success(bytes)
        runBlocking { x1CameraService.getPhotoMetadata(CameraFile("", "", "", "")) }
        coVerify { fileInformationHelper.getFileInformation(any()) }
    }

    @Test
    fun testGetPhotoMetadataSuccess() {
        val bytes = "{'fileName':'Manuel', 'officerId': '12'}".toByteArray()
        coEvery { fileInformationHelper.getFileInformation(any()) } returns Result.Success(bytes)
        runBlocking {
            val response =
                x1CameraService.getPhotoMetadata(CameraFile("as", "", "", ""))
            Assert.assertTrue(response is Result.Success)
            val data = (response as Result.Success).data
            Assert.assertEquals("12", data.officerId)
        }
    }

    @Test
    fun testGetPhotoMetadataSuccessEmptyFromHelper() {
        val bytes = "".toByteArray()
        coEvery { fileInformationHelper.getFileInformation(any()) } returns Result.Success(bytes)
        runBlocking {
            val response =
                x1CameraService.getPhotoMetadata(CameraFile("as", "", "", ""))
            Assert.assertTrue(response is Result.Success)
            val data = (response as Result.Success).data
            Assert.assertEquals(null, data.officerId)
        }
    }

    @Test
    fun testGetPhotoMetadataError() {
        coEvery {
            fileInformationHelper.getFileInformation(any())
        } returns Result.Error(Exception(""))
        runBlocking {
            val response =
                x1CameraService.getPhotoMetadata(CameraFile("as", "", "", ""))
            Assert.assertTrue(response is Result.Error)
        }
    }

    @Test
    fun testGetAudioMetadataFlow() {
        val bytes = "qweqwe".toByteArray()
        coEvery { fileInformationHelper.getFileInformation(any()) } returns Result.Success(bytes)
        runBlocking { x1CameraService.getAudioMetadata(CameraFile("", "", "", "")) }
        coVerify { fileInformationHelper.getFileInformation(any()) }
    }

    @Test
    fun testGetAudioMetadataSuccess() {
        val bytes = "{'fileName':'Manuel', 'officerId': '12'}".toByteArray()
        coEvery { fileInformationHelper.getFileInformation(any()) } returns Result.Success(bytes)
        runBlocking {
            val response =
                x1CameraService.getAudioMetadata(CameraFile("as", "", "", ""))
            Assert.assertTrue(response is Result.Success)
            val data = (response as Result.Success).data
            Assert.assertEquals("12", data.officerId)
        }
    }

    @Test
    fun testGetAudioMetadataSuccessEmptyFromHelper() {
        val bytes = "".toByteArray()
        coEvery { fileInformationHelper.getFileInformation(any()) } returns Result.Success(bytes)
        runBlocking {
            val response =
                x1CameraService.getAudioMetadata(CameraFile("as", "", "", ""))
            Assert.assertTrue(response is Result.Success)
            val data = (response as Result.Success).data
            Assert.assertEquals(null, data.officerId)
        }
    }

    @Test
    fun testGetAudioMetadataError() {
        coEvery {
            fileInformationHelper.getFileInformation(any())
        } returns Result.Error(Exception(""))
        runBlocking {
            val response =
                x1CameraService.getAudioMetadata(CameraFile("as", "", "", ""))
            Assert.assertTrue(response is Result.Error)
        }
    }

    @Test
    fun testSaveAllPhotoMetadataFLow() {
        val photoInformation: PhotoInformation = mockk(relaxed = true)
        coEvery { metadataHelper.savePhotoInformationInOneFile(any()) } returns Result.Success(Unit)
        runBlocking { x1CameraService.saveAllPhotoMetadata(listOf(photoInformation)) }
        coVerify { metadataHelper.savePhotoInformationInOneFile(listOf(photoInformation)) }
    }

    @Test
    fun testSavePhotoMetadataFLow() {
        val photoInformation: PhotoInformation = mockk(relaxed = true)
        coEvery { metadataHelper.savePhotoInformation(any()) } returns Result.Success(Unit)
        runBlocking { x1CameraService.savePhotoMetadata(photoInformation) }
        coVerify { metadataHelper.savePhotoInformation(photoInformation) }
    }

    @Test
    fun testSavePhotoMetadataSuccess() {
        val photoInformation: PhotoInformation = mockk(relaxed = true)
        coEvery { metadataHelper.savePhotoInformation(any()) } returns Result.Success(Unit)
        runBlocking {
            val result = x1CameraService.savePhotoMetadata(photoInformation)
            Assert.assertTrue(result is Result.Success)
        }
    }

    @Test
    fun testSavePhotoMetadataError() {
        val photoInformation: PhotoInformation = mockk(relaxed = true)
        coEvery { metadataHelper.savePhotoInformation(any()) } returns Result.Error(Exception())
        runBlocking {
            val result = x1CameraService.savePhotoMetadata(photoInformation)
            Assert.assertTrue(result is Result.Error)
        }
    }

    @Test
    fun testSaveAudioMetadataFLow() {
        val photoInformation: AudioInformation = mockk(relaxed = true)
        coEvery { metadataHelper.saveAudioInformation(any()) } returns Result.Success(Unit)
        runBlocking { x1CameraService.saveAudioMetadata(photoInformation) }
        coVerify { metadataHelper.saveAudioInformation(photoInformation) }
    }

    @Test
    fun testSaveAudioMetadataSuccess() {
        val photoInformation: AudioInformation = mockk(relaxed = true)
        coEvery { metadataHelper.saveAudioInformation(any()) } returns Result.Success(Unit)
        runBlocking {
            val result = x1CameraService.saveAudioMetadata(photoInformation)
            Assert.assertTrue(result is Result.Success)
        }
    }

    @Test
    fun testSaveAudioMetadataError() {
        val photoInformation: AudioInformation = mockk(relaxed = true)
        coEvery { metadataHelper.saveAudioInformation(any()) } returns Result.Error(Exception())
        runBlocking {
            val result = x1CameraService.saveAudioMetadata(photoInformation)
            Assert.assertTrue(result is Result.Error)
        }
    }

    @Test
    fun getFreeStorageSuccess() {
        coEvery { commandHelper.getResponseParam(any()) } returns Result.Success("")
        runBlocking {
            val result = x1CameraService.getFreeStorage()
            Assert.assertTrue(result is Result.Success)
        }
        coVerify { commandHelper.getResponseParam(any()) }
    }

    @Test
    fun getFreeStorageError() {
        coEvery { commandHelper.getResponseParam(any()) } returns Result.Error(Exception())
        runBlocking {
            val result = x1CameraService.getFreeStorage()
            Assert.assertTrue(result is Result.Error)
        }
        coVerify { commandHelper.getResponseParam(any()) }
    }

    @Test
    fun getTotalStorageSuccess() {
        coEvery { commandHelper.getResponseParam(any()) } returns Result.Success("")
        runBlocking {
            val result = x1CameraService.getTotalStorage()
            Assert.assertTrue(result is Result.Success)
        }
        coVerify { commandHelper.getResponseParam(any()) }
    }

    @Test
    fun getTotalStorageError() {
        coEvery { commandHelper.getResponseParam(any()) } returns Result.Error(Exception())
        runBlocking {
            val result = x1CameraService.getTotalStorage()
            Assert.assertTrue(result is Result.Error)
        }
        coVerify { commandHelper.getResponseParam(any()) }
    }

    @Test
    fun deleteFileSuccess() {
        coEvery { commandHelper.isCommandSuccess(any()) } returns Result.Success(Unit)
        runBlocking { x1CameraService.deleteFile("") }
        coVerify { commandHelper.isCommandSuccess(any()) }
    }

    @Test
    fun deleteFileError() {
        coEvery { commandHelper.isCommandSuccess(any()) } returns Result.Error(Exception())
        runBlocking { x1CameraService.deleteFile("") }
        coVerify { commandHelper.isCommandSuccess(any()) }
    }

    @Test
    fun getBatteryLevelFlow() {
        coEvery { fileInformationHelper.getBatteryLevelFromLog() } returns Result.Success(10)
        runBlocking { x1CameraService.getBatteryLevel() }
        coVerify { fileInformationHelper.getBatteryLevelFromLog() }
    }

    @Test
    fun testIsPossibleTheConnection() {
        coEvery { commandHelper.connectCMDSocket(any()) } returns Result.Success(Unit)
        runBlocking { x1CameraService.isPossibleTheConnection("") }
        coVerify { commandHelper.connectCMDSocket(any()) }
    }

    @Test
    fun testGetAssociatedVideosFlow() {
        mockkObject(CameraServiceCache)

        val cameraFile: CameraFile = mockk(relaxed = true)
        val fileResponseWithErrors =
            FileResponseWithErrors().apply {
                items.addAll(
                    listOf(cameraFile, cameraFile, cameraFile)
                )
            }

        coEvery { commandHelper.getMediaListOfType(any()) } returns Result.Success(
            fileResponseWithErrors
        )
        coEvery { fileInformationHelper.getFileInformation(any()) } returns Result.Success(
            ByteArray(
                0
            )
        )
        every { CameraServiceCache.isVideosInformationEmpty() } returns true andThen false

        runBlocking { x1CameraService.getAssociatedVideos(cameraFile) }

        coVerify {
            commandHelper.getMediaListOfType(any())
            fileInformationHelper.getFileInformation(any())
        }
    }

    @Test
    fun testGetAssociatedVideosSuccess() {
        mockkObject(CameraServiceCache)
        CameraServiceCache.cleanCache()

        val cameraFile: CameraFile = mockk(relaxed = true)
        val fileResponseWithErrors =
            FileResponseWithErrors().apply {
                items.addAll(
                    listOf(cameraFile, cameraFile, cameraFile)
                )
            }

        coEvery { commandHelper.getMediaListOfType(any()) } returns Result.Success(
            fileResponseWithErrors
        )
        coEvery { fileInformationHelper.getFileInformation(any()) } returns Result.Success(
            ByteArray(
                0
            )
        )
        every { CameraServiceCache.isVideosInformationEmpty() } returns true andThen false

        runBlocking {
            val result = x1CameraService.getAssociatedVideos(cameraFile)
            Assert.assertTrue(result is Result.Success)
        }
    }

    @Test
    fun testGetAssociatedVideosErrorInList() {
        val cameraFile: CameraFile = mockk(relaxed = true)

        coEvery { commandHelper.getMediaListOfType(any()) } returns Result.Error(Exception())
        coEvery { fileInformationHelper.getFileInformation(any()) } returns Result.Success(
            ByteArray(
                0
            )
        )

        runBlocking {
            val result = x1CameraService.getAssociatedVideos(cameraFile)
            Assert.assertTrue(result is Result.Error)
        }
    }

    @Test
    fun testGetAssociatedVideosErrorInFileInformation() {
        val cameraFile: CameraFile = mockk(relaxed = true)

        val fileResponseWithErrors =
            FileResponseWithErrors().apply {
                items.addAll(
                    listOf(cameraFile, cameraFile, cameraFile)
                )
            }

        coEvery { commandHelper.getMediaListOfType(any()) } returns Result.Success(
            fileResponseWithErrors
        )
        coEvery { fileInformationHelper.getFileInformation(any()) } returns Result.Error(Exception())

        runBlocking {
            val result = x1CameraService.getAssociatedVideos(cameraFile)
            Assert.assertTrue(result is Result.Error)
        }
    }

    @Test
    fun isRecordingTrue() {
        val result = Result.Success(XCameraStatus.RECORDING.value)
        coEvery { commandHelper.getResponseParam(any()) } returns result
        runBlocking { Assert.assertTrue(x1CameraService.isRecording()) }
        coVerify { commandHelper.getResponseParam(any()) }
    }

    @Test
    fun isRecordingFalse() {
        coEvery { commandHelper.getResponseParam(any()) } returns Result.Success("")
        runBlocking { Assert.assertFalse(x1CameraService.isRecording()) }
        coVerify { commandHelper.getResponseParam(any()) }
    }

    @Test
    fun isRecordingError() {
        coEvery { commandHelper.getResponseParam(any()) } returns Result.Error(Exception())
        runBlocking { Assert.assertFalse(x1CameraService.isRecording()) }
        coVerify { commandHelper.getResponseParam(any()) }
    }

    @Test
    fun startCovertModeTest() {
        coEvery { commandHelper.isCommandSuccess(any()) } returns Result.Success(Unit)

        runBlocking { x1CameraService.startCovertMode() }

        coVerify {
            commandHelper.isCommandSuccess(any())
        }
    }

    @Test
    fun stopCovertModeTest() {
        coEvery { commandHelper.isCommandSuccess(any()) } returns Result.Success(Unit)

        runBlocking { x1CameraService.stopCovertMode() }

        coVerify {
            commandHelper.isCommandSuccess(any())
        }
    }

    @Test
    fun turnOnBluetoothTest() {
        coEvery { commandHelper.isCommandSuccess(any()) } returns Result.Success(Unit)

        runBlocking { x1CameraService.turnOnBluetooth() }

        coVerify {
            commandHelper.isCommandSuccess(any())
        }
    }

    @Test
    fun turnOffBluetoothTest() {
        coEvery { commandHelper.isCommandSuccess(any()) } returns Result.Success(Unit)

        runBlocking { x1CameraService.turnOffBluetooth() }

        coVerify {
            commandHelper.isCommandSuccess(any())
        }
    }
}
