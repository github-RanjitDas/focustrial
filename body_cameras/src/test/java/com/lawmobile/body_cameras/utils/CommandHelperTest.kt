package com.lawmobile.body_cameras.utils

import com.google.gson.Gson
import com.lawmobile.body_cameras.cache.CameraServiceCache
import com.lawmobile.body_cameras.entities.BWCConnectionParams.hostnameToConnect
import com.lawmobile.body_cameras.enums.FileListType
import com.lawmobile.body_cameras.socket.SocketHelper
import com.lawmobile.body_cameras.x1.entities.XCameraCommand
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class CommandHelperTest {

    private val format: Gson = mockk(relaxed = true)
    private var commandSocket: SocketHelper = mockk(relaxed = true)
    private val commandHelper = CommandHelper(format, commandSocket)

    @BeforeEach
    fun setUp() {
        clearMocks()
    }

    @Test
    fun connectSocketCameraCMDSuccess() {
        coEvery { commandSocket.connectSocket(any(), any()) } returns Result.Success(Unit)
        runBlocking {
            val result = commandHelper.connectCMDSocket("")
            Assert.assertTrue(result is Result.Success)
        }

        coVerify {
            commandSocket.connectSocket(any(), any())
        }
    }

    @Test
    fun connectSocketCameraCMDError() {
        coEvery { commandSocket.connectSocket(any(), any()) } returns Result.Error(mockk())
        runBlocking {
            val result = commandHelper.connectCMDSocket("")
            Assert.assertTrue(result is Result.Error)
        }

        coVerify {
            commandSocket.connectSocket(any(), any())
        }
    }

    @Test
    fun getTokenSessionIdSuccess() {
        coEvery { commandSocket.writeBytesToGetStringResponse(any()) } returns "{'rval':'1','msg_id':'1', 'param': '1'}"
        runBlocking {
            val response = commandHelper.getSessionToken()
            val idToken = (response as Result.Success).data
            Assert.assertEquals(1, idToken)
        }
        coVerify {
            commandSocket.writeBytesToGetStringResponse(any())
        }
    }

    @Test
    fun getTokenSessionIdParamError() {
        coEvery { commandSocket.writeBytesToGetStringResponse(any()) } returns "{'rval':-21,'msg_id':'1'}"
        runBlocking {
            val response = commandHelper.getSessionToken()
            Assert.assertTrue(response is Result.Error)
        }
        coVerify {
            commandSocket.writeBytesToGetStringResponse(any())
        }
    }

    @Test
    fun getTokenSessionIdException() {
        coEvery { commandSocket.writeBytesToGetStringResponse(any()) } returns ""
        runBlocking {
            val response = commandHelper.getSessionToken()
            Assert.assertTrue(response is Result.Error)
        }
        coVerify {
            commandSocket.writeBytesToGetStringResponse(any())
        }
    }

    @Test
    fun requestCommandDownloadFileSuccess() {
        val responseCamera = "{'token':1, 'rval':0}"
        coEvery { commandSocket.writeBytesToGetStringResponse(any()) } returns responseCamera
        runBlocking {
            val response = commandHelper.requestCommandDownloadFile("path")
            Assert.assertTrue(response is Result.Success)
        }
        coVerify { commandSocket.writeBytesToGetStringResponse(any()) }
    }

    @Test
    fun requestCommandDownloadFileError() {
        coEvery { commandSocket.writeBytesToGetStringResponse(any()) } returns ""
        runBlocking {
            val response = commandHelper.requestCommandDownloadFile("path")
            Assert.assertTrue(response is Result.Error)
        }
        coVerify { commandSocket.writeBytesToGetStringResponse(any()) }
    }

    @Test
    fun isCommandSuccess() {
        val commandX1 = mockk<XCameraCommand>(relaxed = true)
        val responseCamera = "{'rval':'0','msg_id':'1', 'param': '1'}"
        coEvery { commandSocket.writeBytesToGetStringResponse(any()) } returns responseCamera
        runBlocking {
            val response = commandHelper.isCommandSuccess(commandX1)
            Assert.assertTrue(response is Result.Success)
        }
        coVerify { commandSocket.writeBytesToGetStringResponse(any()) }
    }

    @Test
    fun isCommandErrorInResponse() {
        val commandX1 = mockk<XCameraCommand>(relaxed = true)
        val responseCamera = "{'rval':'-26','msg_id':'1', 'param': '1'}"
        coEvery { commandSocket.writeBytesToGetStringResponse(any()) } returns responseCamera
        runBlocking {
            val response = commandHelper.isCommandSuccess(commandX1)
            Assert.assertTrue(response is Result.Error)
        }
        coVerify { commandSocket.writeBytesToGetStringResponse(any()) }
    }

    @Test
    fun isCommandSuccessErrorException() {
        val commandX1 = mockk<XCameraCommand>(relaxed = true)
        val responseCamera = ""
        coEvery { commandSocket.writeBytesToGetStringResponse(any()) } returns responseCamera
        runBlocking {
            val response = commandHelper.isCommandSuccess(commandX1)
            Assert.assertTrue(response is Result.Error)
        }
        coVerify { commandSocket.writeBytesToGetStringResponse(any()) }
    }

    @Test
    fun getResponseParam() {
        coEvery { commandSocket.writeBytesToGetStringResponse(any()) } returns "{'rval':'1','msg_id':'1', 'param': '1'}"
        runBlocking {
            val response = commandHelper.getResponseParam(mockk())
            Assert.assertTrue(response is Result.Success)
            Assert.assertEquals((response as Result.Success).data, "1")
        }
    }

    @Test
    fun getResponseParamErrorInParam() {
        coEvery { commandSocket.writeBytesToGetStringResponse(any()) } returns "{'rval':'1','msg_id':'1', 'param': ''}"
        runBlocking {
            val response = commandHelper.getResponseParam(mockk())
            Assert.assertTrue(response is Result.Error)
        }
    }

    @Test
    fun getResponseParamErrorInRequest() {
        coEvery { commandSocket.writeBytesToGetStringResponse(any()) } returns ""
        runBlocking {
            val response = commandHelper.getResponseParam(mockk())
            Assert.assertTrue(response is Result.Error)
        }
    }

    @Test
    fun getInfoMediaFromCMDSocketSuccess() {
        val responseCMD =
            "{\"rval\": 10, \"msg_id\": 12, \"size\": 20, \"date\": \"1212\", \"resolution\":\"12:20\", \"duration\":12, \"media_type\":\"1212\"}"
        coEvery { commandSocket.writeBytesToGetStringResponse(any()) } returns responseCMD
        val path = "folder/video.MP4"
        runBlocking {
            val response = commandHelper.getInfoMediaFromCMDSocket(path)
            Assert.assertTrue(response is Result.Success)
            Assert.assertEquals((response as Result.Success).data.duration, 12)
            Assert.assertEquals(
                (response).data.urlVideo,
                "rtsp://$hostnameToConnect$path"
            )
        }

        coVerify { commandSocket.writeBytesToGetStringResponse(any()) }
    }

    @Test
    fun getInfoMediaFromCMDSocketError() {
        val responseCMD = ""
        coEvery { commandSocket.writeBytesToGetStringResponse(any()) } returns responseCMD
        val path = "folder/video.MP4"
        runBlocking {
            val response = commandHelper.getInfoMediaFromCMDSocket(path)
            Assert.assertTrue(response is Result.Error)
        }

        coVerify { commandSocket.writeBytesToGetStringResponse(any()) }
    }

    @Test
    fun getMediaListOfTypeSnapshotSuccessFromCache() {
        coEvery { commandSocket.writeBytesToGetStringResponse(any()) } returns "{'rval':'1','msg_id':'1', 'param': '2'}"
        CameraServiceCache.snapshots = listOf(mockk(), mockk())
        runBlocking {
            val response = commandHelper.getMediaListOfType(FileListType.SNAPSHOT)
            Assert.assertTrue(response is Result.Success)
            Assert.assertEquals(2, (response as Result.Success).data.items.size)
        }
    }

    @Test
    fun getMediaListOfTypeVideoSuccessFromCache() {
        coEvery { commandSocket.writeBytesToGetStringResponse(any()) } returns "{'rval':'1','msg_id':'1', 'param': '4'}"
        CameraServiceCache.videos = listOf(mockk(), mockk())
        runBlocking {
            val response = commandHelper.getMediaListOfType(FileListType.VIDEO)
            Assert.assertTrue(response is Result.Success)
            Assert.assertEquals(2, (response as Result.Success).data.items.size)
        }
    }

    @Test
    fun getMediaListOfTypeSnapshotSuccessFromCamera() {
        val responseNumber = "{'rval':'1','msg_id':'1', 'param': '4'}"
        val responseStringMain =
            "{\"rval\":0,\"msg_id\":1282,\"listing\":[{\"200128000/\":\"2020-01-28 11:25:16\"},{\"200206000/\":\"2020-02-06 07:45:24\"}]}"
        val responseFolderOne =
            "{\"rval\":0,\"msg_id\":1282,\"listing\":[{\"2001281125160.JPG\":\"2020-01-28 11:25:16\"},{\"200128074524.JPG\":\"2020-01-28 07:45:24\"}]}"
        val responseFolderTwo =
            "{\"rval\":0,\"msg_id\":1282,\"listing\":[{\"2002061125160.JPG\":\"2020-02-06 11:25:16\"},{\"200206074524.JPG\":\"2020-02-06 07:45:24\"}]}"
        CameraServiceCache.snapshots = listOf(mockk(), mockk())
        coEvery { commandSocket.writeBytesToGetStringResponse(any()) } returns responseNumber andThen responseStringMain andThen responseFolderOne andThen responseFolderTwo
        runBlocking {
            val response = commandHelper.getMediaListOfType(FileListType.SNAPSHOT)
            Assert.assertTrue(response is Result.Success)
            Assert.assertEquals(4, (response as Result.Success).data.items.size)
        }
    }

    @Test
    fun getMediaListOfTypeVideoSuccessFromCamera() {
        val responseNumber = "{'rval':'1','msg_id':'1', 'param': '4'}"
        val responseStringMain =
            "{\"rval\":0,\"msg_id\":1282,\"listing\":[{\"200128000/\":\"2020-01-28 11:25:16\"},{\"200206000/\":\"2020-02-06 07:45:24\"}]}"
        val responseFolderOne =
            "{\"rval\":0,\"msg_id\":1282,\"listing\":[{\"2001281125160AB.MP4\":\"2020-01-28 11:25:16\"},{\"2001281125160AA.MP4\":\"2020-01-28 11:25:16\"}]}"
        val responseFolderTwo =
            "{\"rval\":0,\"msg_id\":1282,\"listing\":[{\"2002061125160AB.MP4\":\"2020-02-06 11:25:16\"},{\"2002061125160AA.MP4\":\"2020-02-06 11:25:16\"}]}"
        CameraServiceCache.videos = listOf(mockk())
        coEvery { commandSocket.writeBytesToGetStringResponse(any()) } returns responseNumber andThen responseStringMain andThen responseFolderOne andThen responseFolderTwo
        runBlocking {
            val response = commandHelper.getMediaListOfType(FileListType.VIDEO)
            Assert.assertTrue(response is Result.Success)
            Assert.assertEquals(2, (response as Result.Success).data.items.size)
        }
    }

    @Test
    fun getListOfItemsAudioSuccessFromCamera() {
        val responseNumber = "{'rval':'1','msg_id':'1', 'param': '4'}"
        val responseStringMain =
            "{\"rval\":0,\"msg_id\":1282,\"listing\":[{\"200128000/\":\"2020-01-28 11:25:16\"},{\"200206000/\":\"2020-02-06 07:45:24\"}]}"
        val responseFolderOne =
            "{\"rval\":0,\"msg_id\":1282,\"listing\":[{\"2001281125160.WAV\":\"2020-01-28 11:25:16\"},{\"200128074524.WAV\":\"2020-01-28 07:45:24\"}]}"
        val responseFolderTwo =
            "{\"rval\":0,\"msg_id\":1282,\"listing\":[{\"2002061125160.WAV\":\"2020-02-06 11:25:16\"},{\"200206074524.WAV\":\"2020-02-06 07:45:24\"}]}"
        CameraServiceCache.audios = listOf(mockk(), mockk())
        coEvery { commandSocket.writeBytesToGetStringResponse(any()) } returns responseNumber andThen responseStringMain andThen responseFolderOne andThen responseFolderTwo
        runBlocking {
            val response = commandHelper.getMediaListOfType(FileListType.AUDIO)
            Assert.assertTrue(response is Result.Success)
            Assert.assertEquals(4, (response as Result.Success).data.items.size)
        }
    }

    @Test
    fun getListOfItemsError() {
        val responseStringMain = ""
        coEvery { commandSocket.writeBytesToGetStringResponse(any()) } returns responseStringMain
        runBlocking {
            val response = commandHelper.getMediaListOfType(FileListType.SNAPSHOT)
            Assert.assertTrue(response is Result.Error)
        }
    }

    @Test
    fun isInputStreamAvailableTrue() {
        every { commandSocket.isInputStreamAvailable() } returns true
        val available = commandHelper.isInputStreamAvailable()
        Assert.assertTrue(available)
        verify { commandSocket.isInputStreamAvailable() }
    }

    @Test
    fun readInputStream() {
        coEvery { commandSocket.readInputStream() } returns ""
        runBlocking {
            val response = commandHelper.readInputStream()
            Assert.assertTrue(response.isEmpty())
        }

        coVerify { commandSocket.readInputStream() }
    }

    @Test
    fun readInformationInputStream() {
        every { commandSocket.readInformationInputStream() } returns ""
        val response = commandHelper.readInformationInputStream()
        Assert.assertTrue(response.isEmpty())
        verify { commandSocket.readInformationInputStream() }
    }

    @Test
    fun isSocketAvailableTrue() {
        every { commandSocket.isSocketAvailable } returns true
        val available = commandHelper.isSocketAvailable()
        Assert.assertTrue(available)
        verify { commandSocket.isSocketAvailable }
    }

    @Test
    fun isConnectedTrue() {
        every { commandSocket.isConnected() } returns true
        val available = commandHelper.isConnected()
        Assert.assertTrue(available)
        verify { commandSocket.isConnected() }
    }
}
