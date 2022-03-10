package com.lawmobile.body_cameras.utils

import com.google.gson.Gson
import com.lawmobile.body_cameras.entities.AudioInformation
import com.lawmobile.body_cameras.entities.PhotoInformation
import com.lawmobile.body_cameras.entities.VideoInformation
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.Runs
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MetadataHelperTest {

    private val format: Gson = mockk(relaxed = true)
    private var fileInformationHelper: FileInformationHelper = mockk(relaxed = true)
    private var commandHelper: CommandHelper = mockk(relaxed = true)
    private val metadataHelper = MetadataHelper(format, fileInformationHelper, commandHelper)

    @BeforeEach
    fun setUp() {
        clearMocks()
    }

    @Test
    fun saveVideoMetadataSuccess() {
        val cameraConnectVideoMetadata = VideoInformation(fileName = "", nameFolder = "")
        every { format.toJson(cameraConnectVideoMetadata) } returns ""
        coEvery { fileInformationHelper.connectDataSocket() } returns Result.Success(Unit)
        coEvery { commandHelper.isCommandSuccess(any()) } returns Result.Success(Unit)
        coEvery { fileInformationHelper.writeInOutputStream(any()) } just Runs
        coEvery { commandHelper.isInputStreamAvailable() } returns true
        coEvery { commandHelper.readInputStream() } returns "{'msg_id': '7', 'type': 'put_file_complete'}"
        runBlocking {
            val response = metadataHelper.saveVideoInformation(cameraConnectVideoMetadata)
            Assert.assertTrue(response is Result.Success)
        }
    }

    @Test
    fun saveVideoMetadataError() {
        val cameraConnectVideoMetadata = VideoInformation(fileName = "", nameFolder = "")
        every { format.toJson(cameraConnectVideoMetadata) } returns ""
        coEvery { fileInformationHelper.connectDataSocket() } returns Result.Success(Unit)
        coEvery { commandHelper.isCommandSuccess(any()) } returns Result.Error(Exception(""))
        coEvery { fileInformationHelper.writeInOutputStream(any()) } just Runs
        runBlocking {
            val response = metadataHelper.saveVideoInformation(cameraConnectVideoMetadata)
            Assert.assertTrue(response is Result.Error)
        }
    }

    @Test
    fun savePhotoMetadataSuccess() {
        val cameraConnectPhotoMetadata = PhotoInformation("name")

        every { format.toJson(cameraConnectPhotoMetadata) } returns ""
        coEvery { fileInformationHelper.connectDataSocket() } returns Result.Success(Unit)
        coEvery { commandHelper.isCommandSuccess(any()) } returns Result.Success(Unit)
        coEvery { fileInformationHelper.writeInOutputStream(any()) } just Runs
        coEvery { commandHelper.isInputStreamAvailable() } returns true
        coEvery { commandHelper.readInputStream() } returns "{'msg_id': '7', 'type': 'put_file_complete'}"

        runBlocking {
            val response = metadataHelper.savePhotoInformation(cameraConnectPhotoMetadata)
            Assert.assertTrue(response is Result.Success)
        }
    }

    @Test
    fun savePhotoMetadataError() {
        val cameraConnectPhotoMetadata = PhotoInformation("name")
        every { format.toJson(cameraConnectPhotoMetadata) } returns ""
        coEvery { fileInformationHelper.connectDataSocket() } returns Result.Success(Unit)
        coEvery { commandHelper.isCommandSuccess(any()) } returns Result.Error(Exception(""))
        coEvery { fileInformationHelper.writeInOutputStream(any()) } just Runs

        runBlocking {
            val response = metadataHelper.savePhotoInformation(cameraConnectPhotoMetadata)
            Assert.assertTrue(response is Result.Error)
        }
    }

    @Test
    fun saveAudioMetadataSuccess() {
        val audioInformation = AudioInformation("name")

        every { format.toJson(audioInformation) } returns ""
        coEvery { fileInformationHelper.connectDataSocket() } returns Result.Success(Unit)
        coEvery { commandHelper.isCommandSuccess(any()) } returns Result.Success(Unit)
        coEvery { fileInformationHelper.writeInOutputStream(any()) } just Runs
        coEvery { commandHelper.isInputStreamAvailable() } returns true
        coEvery { commandHelper.readInputStream() } returns "{'msg_id': '7', 'type': 'put_file_complete'}"

        runBlocking {
            val response = metadataHelper.saveAudioInformation(audioInformation)
            Assert.assertTrue(response is Result.Success)
        }
    }

    @Test
    fun saveAudioMetadataError() {
        val audioInformation = AudioInformation("name")
        every { format.toJson(audioInformation) } returns ""
        coEvery { fileInformationHelper.connectDataSocket() } returns Result.Success(Unit)
        coEvery { commandHelper.isCommandSuccess(any()) } returns Result.Error(Exception(""))
        coEvery { fileInformationHelper.writeInOutputStream(any()) } just Runs

        runBlocking {
            val response = metadataHelper.saveAudioInformation(audioInformation)
            Assert.assertTrue(response is Result.Error)
        }
    }

    @Test
    fun testGetPhotoListMetadataSuccess() {
        val stringJSON = "json"
        val arrayReturn: Array<PhotoInformation> =
            Array(2) { PhotoInformation("") }

        every {
            format.fromJson(
                stringJSON,
                Array<PhotoInformation>::class.java
            )
        } returns arrayReturn
        val response = metadataHelper.getPhotoInformationList(stringJSON.toByteArray())
        Assert.assertTrue(response is Result.Success)
    }

    @Test
    fun testGetPhotoListMetadataError() {
        val stringJSON = "json"
        every {
            format.fromJson(
                stringJSON,
                Array<PhotoInformation>::class.java
            )
        } throws Exception()
        val response = metadataHelper.getPhotoInformationList(stringJSON.toByteArray())
        Assert.assertTrue(response is Result.Error)
    }
}
