package com.lawmobile.data.repository.audioDetail

import com.lawmobile.data.datasource.remote.audioDetail.AudioDetailRemoteDataSource
import com.lawmobile.data.mappers.impl.FileMapper.toDomain
import com.lawmobile.domain.entities.FileList
import com.safefleet.mobile.external_hardware.cameras.entities.AudioInformation
import com.safefleet.mobile.external_hardware.cameras.entities.CameraFile
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

@ExperimentalCoroutinesApi
class AudioDetailRepositoryImplTest {

    private val audioDetailRemoteDataSource: AudioDetailRemoteDataSource = mockk()
    private val dispatcher = TestCoroutineDispatcher()

    private val audioDetailRepositoryImpl by lazy {
        AudioDetailRepositoryImpl(audioDetailRemoteDataSource)
    }

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @Test
    fun testGetAudioBytesSuccess() = runBlockingTest {
        val cameraConnectFile = CameraFile("fileName.PNG", "date", "path", "nameFolder/")
        val byte = ByteArray(1)

        coEvery { audioDetailRemoteDataSource.getAudioBytes(cameraConnectFile) } returns
            Result.Success(byte)

        val result = audioDetailRepositoryImpl.getAudioBytes(
            cameraConnectFile.toDomain()
        ) as Result.Success
        Assert.assertEquals(result.data, byte)

        coVerify { audioDetailRemoteDataSource.getAudioBytes(cameraConnectFile) }
    }

    @Test
    fun testGetAudioBytesError() = runBlockingTest {
        val cameraConnectFile = CameraFile("fileName.PNG", "date", "path", "nameFolder/")

        coEvery { audioDetailRemoteDataSource.getAudioBytes(cameraConnectFile) } returns
            Result.Error(Exception(""))

        val result = audioDetailRepositoryImpl.getAudioBytes(
            cameraConnectFile.toDomain()
        )
        Assert.assertTrue(result is Result.Error)

        coVerify { audioDetailRemoteDataSource.getAudioBytes(cameraConnectFile) }
    }

    @Test
    fun testSavePartnerIdAudioFlow() = dispatcher.runBlockingTest {
        val cameraConnectFile = CameraFile("fileName.PNG", "date", "path", "nameFolder/")

        coEvery { audioDetailRemoteDataSource.savePartnerIdAudio(any()) } returns
            Result.Success(Unit)

        FileList.audioMetadataList = mutableListOf()

        audioDetailRepositoryImpl.saveAudioPartnerId(
            cameraConnectFile.toDomain(),
            "partnerId"
        )

        coVerify {
            audioDetailRemoteDataSource.savePartnerIdAudio(any())
        }
    }

    @Test
    fun testSavePartnerIdAudioSuccessWithInformationInFileList() = dispatcher.runBlockingTest {
        val cameraConnectFile = CameraFile("fileName.PNG", "date", "path", "nameFolder/")

        coEvery { audioDetailRemoteDataSource.savePartnerIdAudio(any()) } returns
            Result.Success(Unit)

        val response = audioDetailRepositoryImpl.saveAudioPartnerId(
            cameraConnectFile.toDomain(),
            "partnerId"
        )

        val item = FileList.findAndGetAudioMetadata("fileName.PNG")
        Assert.assertTrue(response is Result.Success)
        Assert.assertEquals(item?.audioMetadata?.metadata?.partnerID, "partnerId")
    }

    @Test
    fun testSavePartnerIdAudioError() = dispatcher.runBlockingTest {
        val cameraConnectFile = CameraFile("fileName.PNG", "date", "path", "nameFolder/")

        coEvery { audioDetailRemoteDataSource.savePartnerIdAudio(any()) } returns
            Result.Error(Exception(""))

        val response = audioDetailRepositoryImpl.saveAudioPartnerId(
            cameraConnectFile.toDomain(),
            "partnerId"
        )
        Assert.assertTrue(response is Result.Error)
    }

    @Test
    fun testGetInformationOfAudioError() = runBlockingTest {
        FileList.audioMetadataList = mutableListOf()
        coEvery { audioDetailRemoteDataSource.getInformationOfAudio(any()) } returns Result.Error(
            mockk()
        )
        val cameraConnectFile = CameraFile("name", "date", "path", "nameFol")

        val response = audioDetailRepositoryImpl.getInformationOfAudio(
            cameraConnectFile.toDomain()
        )
        Assert.assertTrue(response is Result.Error)
    }

    @Test
    fun testGetInformationOfAudioSuccess() = runBlockingTest {
        val cameraConnectAudioMetadata = AudioInformation(fileName = "name")
        coEvery { audioDetailRemoteDataSource.getInformationOfAudio(any()) } returns Result.Success(
            cameraConnectAudioMetadata
        )
        val cameraSend = CameraFile("name", "date", "path", "nameFol")

        val response = audioDetailRepositoryImpl.getInformationOfAudio(cameraSend.toDomain())
        Assert.assertTrue(response is Result.Success)
    }

    @Test
    fun testGetAssociatedVideosSuccess() = runBlockingTest {
        val cameraFile = CameraFile("name", "date", "path", "nameFol")
        coEvery {
            audioDetailRemoteDataSource.getAssociatedVideos(any())
        } returns Result.Success(listOf(cameraFile))
        val result = audioDetailRepositoryImpl.getAssociatedVideos(cameraFile.toDomain())
        Assert.assertTrue(result is Result.Success)
        coVerify { audioDetailRemoteDataSource.getAssociatedVideos(any()) }
    }

    @Test
    fun testGetAssociatedVideosError() = runBlockingTest {
        val cameraFile = CameraFile("name", "date", "path", "nameFol")
        coEvery {
            audioDetailRemoteDataSource.getAssociatedVideos(any())
        } returns Result.Error(Exception())
        val result = audioDetailRepositoryImpl.getAssociatedVideos(cameraFile.toDomain())
        Assert.assertTrue(result is Result.Error)
        coVerify { audioDetailRemoteDataSource.getAssociatedVideos(any()) }
    }
}
