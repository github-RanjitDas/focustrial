package com.lawmobile.body_cameras.utils

import com.google.gson.Gson
import com.lawmobile.body_cameras.constants.CameraConstants.PATH_DOWNLOAD_OFFICER_INFO
import com.lawmobile.body_cameras.socket.SocketHelper
import com.lawmobile.body_cameras.x1.entities.X1FileResponse
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.Test

internal class FileInformationHelperTest {

    private val format: Gson = mockk(relaxed = true)
    private var dataSocket: SocketHelper = mockk(relaxed = true)
    private var commandHelper: CommandHelper = mockk(relaxed = true)
    private val fileInformationHelper = FileInformationHelper(format, commandHelper, dataSocket)

    @Test
    fun getFileInformationSuccess() {

        val responseInformation = "This is content of document in the camera"
        val responseCameraSocketCommand = X1FileResponse(
            rval = 0,
            token = 1,
            size = responseInformation.toByteArray().size.toLong()
        )
        coEvery { dataSocket.connectSocket(any(), any()) } returns Result.Success(Unit)
        coEvery { commandHelper.requestCommandDownloadFile(any()) } returns Result.Success(
            responseCameraSocketCommand
        )
        coEvery { dataSocket.getBytesWithSize(any()) } returns Result.Success(
            responseInformation.toByteArray()
        )
        coEvery { commandHelper.isInputStreamAvailable() } returns true
        coEvery { commandHelper.readInputStream() } returns "{'token':'1', 'size': ${responseInformation.toByteArray().size}, 'type': 'file_completed'}"
        runBlocking {
            val bytes =
                fileInformationHelper.getFileInformation(PATH_DOWNLOAD_OFFICER_INFO)
            Assert.assertTrue(bytes is Result.Success)
            Assert.assertEquals(String((bytes as Result.Success).data), responseInformation)
        }
    }

    @Test
    fun getFileInformationFailedConnectDataSocket() {

        coEvery { dataSocket.connectSocket(any(), any()) } returns Result.Error(mockk())
        runBlocking {
            val result =
                fileInformationHelper.getFileInformation(PATH_DOWNLOAD_OFFICER_INFO)
            Assert.assertTrue(result is Result.Error)
        }
    }

    @Test
    fun getFileInformationFailedRequestCommand() {
        coEvery { dataSocket.connectSocket(any(), any()) } returns Result.Success(Unit)
        coEvery { commandHelper.requestCommandDownloadFile(any()) } returns Result.Error(Exception(""))
        runBlocking {
            val result =
                fileInformationHelper.getFileInformation(PATH_DOWNLOAD_OFFICER_INFO)
            Assert.assertTrue(result is Result.Error)
        }
    }

    @Test
    fun getBatteryLevel() {
        val file =
            "07/22/2020 08:51:11, 1280x720 30P 16:9,6.00 Mbps,0MB,58%,3.904V,TS=43%,400mA\n" + "07/22/2020 08:52:51, 1280x720 30P 16:9,6.00 Mbps,0MB,57%,3.904V,TS=42%,400mA"
        mockkObject(FileInformationHelper)
        every { FileInformationHelper.dateToString(any(), any(), any()) } returns "07/22/2020"
        val responseCameraSocketCommand =
            X1FileResponse(rval = 0, token = 1, size = file.toByteArray().size.toLong())
        coEvery { dataSocket.connectSocket(any(), any()) } returns Result.Success(Unit)
        coEvery { commandHelper.requestCommandDownloadFile(any()) } returns Result.Success(
            responseCameraSocketCommand
        )
        coEvery { dataSocket.getBytesWithSize(any()) } returns Result.Success(file.toByteArray())
        coEvery { commandHelper.isInputStreamAvailable() } returns true
        coEvery { commandHelper.readInputStream() } returns "{'token':'1', 'size': ${file.toByteArray().size}, 'type': 'file_completed'}"

        runBlocking {
            val response = fileInformationHelper.getBatteryLevelFromLog()
            val battery = (response as Result.Success).data
            Assert.assertEquals(battery, 57)
        }
    }
}
