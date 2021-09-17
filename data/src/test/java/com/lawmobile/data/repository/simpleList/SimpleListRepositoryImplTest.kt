package com.lawmobile.data.repository.simpleList

import com.lawmobile.data.datasource.remote.simpleList.SimpleListRemoteDataSource
import com.lawmobile.data.mappers.impl.FileMapper.toDomain
import com.lawmobile.domain.entities.DomainInformationFile
import com.lawmobile.domain.entities.DomainInformationFileResponse
import com.lawmobile.domain.entities.FileList
import com.lawmobile.domain.entities.VideoListMetadata
import com.safefleet.mobile.external_hardware.cameras.entities.CameraFile
import com.safefleet.mobile.external_hardware.cameras.entities.FileResponseWithErrors
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
internal class SimpleListRepositoryImplTest {

    private val simpleListRemoteDataSource: SimpleListRemoteDataSource = mockk()
    private val dispatcher = TestCoroutineDispatcher()

    private val simpleListRepositoryImpl: SimpleListRepositoryImpl by lazy {
        SimpleListRepositoryImpl(simpleListRemoteDataSource)
    }

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(dispatcher)
        clearAllMocks()
    }

    @Test
    fun getSnapshotListFlow() = runBlockingTest {
        val cameraConnectFile: CameraFile = mockk(relaxed = true)
        val cameraResponse: FileResponseWithErrors = mockk {
            every { items } returns arrayListOf(cameraConnectFile)
            every { errors } returns arrayListOf()
        }

        coEvery {
            simpleListRemoteDataSource.getSnapshotList()
        } returns Result.Success(cameraResponse)

        simpleListRepositoryImpl.getSnapshotList()
        coVerify { simpleListRemoteDataSource.getSnapshotList() }
    }

    @Test
    fun getSnapshotListSuccess() = runBlockingTest {
        val cameraFile: CameraFile = mockk(relaxed = true)
        val cameraResponse = FileResponseWithErrors().apply {
            items.addAll(mutableListOf(cameraFile))
        }
        val listDomain =
            mutableListOf(DomainInformationFile(cameraFile.toDomain(), null))
        val domainInformationFileResponse =
            DomainInformationFileResponse(listDomain, mutableListOf())

        coEvery {
            simpleListRemoteDataSource.getSnapshotList()
        } returns Result.Success(cameraResponse)

        FileList.imageList = emptyList()

        val result = simpleListRepositoryImpl.getSnapshotList() as Result.Success
        Assert.assertEquals(result.data.items, domainInformationFileResponse.items)
        Assert.assertEquals(result.data.errors, domainInformationFileResponse.errors)
    }

    @Test
    fun getSnapshotListSuccessWithLessValuesInDataSource() = runBlockingTest {
        val cameraFile: CameraFile = mockk(relaxed = true)
        val cameraResponse = FileResponseWithErrors()
        cameraResponse.items.addAll(arrayListOf(cameraFile, cameraFile))

        coEvery { simpleListRemoteDataSource.getSnapshotList() } returns Result.Success(
            cameraResponse
        )

        FileList.imageList = listOf(
            DomainInformationFile(cameraFile.toDomain(), null),
            DomainInformationFile(cameraFile.toDomain(), null)
        )
        val imageList = FileList.imageList as MutableList
        val domainInformationFileResponse =
            DomainInformationFileResponse(imageList, mutableListOf())

        val result = simpleListRepositoryImpl.getSnapshotList() as Result.Success
        Assert.assertEquals(result.data.items, domainInformationFileResponse.items)
        Assert.assertEquals(result.data.errors, domainInformationFileResponse.errors)
    }

    @Test
    fun getSnapshotListSuccessWithEqualsValuesInDataSource() = runBlockingTest {
        mockkObject(FileList)
        every { FileList.imageList = any() } just Runs

        val cameraConnectFile: CameraFile = mockk(relaxed = true)
        val cameraResponse = FileResponseWithErrors()
        cameraResponse.items.addAll(arrayListOf(cameraConnectFile, cameraConnectFile))
        val listImages = listOf(
            DomainInformationFile(cameraConnectFile.toDomain(), null),
            DomainInformationFile(cameraConnectFile.toDomain(), null)
        )

        coEvery {
            simpleListRemoteDataSource.getSnapshotList()
        } returns Result.Success(cameraResponse)

        FileList.imageList = listImages

        val result = simpleListRepositoryImpl.getSnapshotList() as Result.Success
        Assert.assertEquals(result.data.items, listImages)

        coVerify { FileList.imageList = listImages }
    }

    @Test
    fun getSnapshotListFailed() = runBlockingTest {
        val result = Result.Error(mockk())
        coEvery { simpleListRemoteDataSource.getSnapshotList() } returns result
        Assert.assertEquals(simpleListRepositoryImpl.getSnapshotList(), result)
    }

    @Test
    fun getVideoListFlow() = runBlockingTest {
        val cameraConnectFile = CameraFile("fileName.PNG", "date", "path", "nameFolder/")
        val cameraResponse = FileResponseWithErrors()
        cameraResponse.items.add(cameraConnectFile)

        coEvery { simpleListRemoteDataSource.getVideoList() } returns Result.Success(cameraResponse)
        simpleListRepositoryImpl.getVideoList()
        coVerify { simpleListRemoteDataSource.getVideoList() }
    }

    @Test
    fun getVideoListSuccess() = runBlockingTest {
        mockkObject(VideoListMetadata)
        every { VideoListMetadata.getVideoMetadata(any()) } returns null

        val cameraConnectFile: CameraFile = mockk(relaxed = true)
        val cameraResponse = FileResponseWithErrors()
        cameraResponse.items.addAll(arrayListOf(cameraConnectFile))
        val listDomain = mutableListOf(
            DomainInformationFile(cameraConnectFile.toDomain(), null)
        )
        val domainInformationFileResponse =
            DomainInformationFileResponse(listDomain, mutableListOf())

        coEvery { simpleListRemoteDataSource.getVideoList() } returns Result.Success(cameraResponse)
        FileList.videoList = emptyList()

        val result = simpleListRepositoryImpl.getVideoList() as Result.Success
        Assert.assertEquals(result.data.items, domainInformationFileResponse.items)
        Assert.assertEquals(result.data.errors, result.data.errors)
    }

    @Test
    fun getVideoListSuccessLessInResponseDataSource() = runBlockingTest {
        mockkObject(VideoListMetadata)
        every { VideoListMetadata.getVideoMetadata(any()) } returns null

        val cameraConnectFile: CameraFile = mockk(relaxed = true)
        val cameraResponse: FileResponseWithErrors = mockk {
            every { items } returns arrayListOf(cameraConnectFile)
            every { errors } returns arrayListOf("20201228/")
        }

        coEvery { simpleListRemoteDataSource.getVideoList() } returns Result.Success(cameraResponse)

        FileList.videoList = listOf(
            DomainInformationFile(cameraConnectFile.toDomain(), null)
        )
        val videoList = FileList.videoList as MutableList
        val domainInformationFileResponse =
            DomainInformationFileResponse(videoList, mutableListOf("20201228/"))

        val result = simpleListRepositoryImpl.getVideoList() as Result.Success
        Assert.assertEquals(result.data.items, domainInformationFileResponse.items)
        Assert.assertEquals(result.data.errors, domainInformationFileResponse.errors)
    }

    @Test
    fun getVideoListSuccessEqualsInResponseDataSource() = runBlockingTest {
        mockkObject(VideoListMetadata)
        every { VideoListMetadata.getVideoMetadata(any()) } returns null
        mockkObject(FileList)
        every { FileList.videoList = any() } just Runs

        val cameraConnectFile: CameraFile = mockk(relaxed = true)
        val cameraResponse: FileResponseWithErrors = mockk {
            every { items } returns arrayListOf(cameraConnectFile, cameraConnectFile)
            every { errors } returns arrayListOf()
        }

        coEvery { simpleListRemoteDataSource.getVideoList() } returns Result.Success(cameraResponse)
        val listOfVideos = listOf(
            DomainInformationFile(cameraConnectFile.toDomain(), null),
            DomainInformationFile(cameraConnectFile.toDomain(), null)
        )
        FileList.videoList = listOfVideos

        val result = simpleListRepositoryImpl.getVideoList() as Result.Success
        Assert.assertEquals(result.data.items, listOfVideos)
        coVerify { FileList.videoList = listOfVideos }
    }

    @Test
    fun getVideoListFailed() = runBlockingTest {
        val result = Result.Error(mockk())
        coEvery { simpleListRemoteDataSource.getVideoList() } returns result
        Assert.assertEquals(simpleListRepositoryImpl.getVideoList(), result)
    }

    @Test
    fun getAudioListFlow() = runBlockingTest {
        val cameraConnectFile: CameraFile = mockk(relaxed = true)
        val cameraResponse: FileResponseWithErrors = mockk {
            every { items } returns arrayListOf(cameraConnectFile)
            every { errors } returns arrayListOf()
        }

        coEvery {
            simpleListRemoteDataSource.getAudioList()
        } returns Result.Success(cameraResponse)

        simpleListRepositoryImpl.getAudioList()
        coVerify { simpleListRemoteDataSource.getAudioList() }
    }

    @Test
    fun getAudioListSuccess() = runBlockingTest {
        val cameraFile: CameraFile = mockk(relaxed = true)
        val cameraResponse = FileResponseWithErrors().apply {
            items.addAll(mutableListOf(cameraFile))
        }
        val listDomain =
            mutableListOf(DomainInformationFile(cameraFile.toDomain(), null))
        val domainInformationFileResponse =
            DomainInformationFileResponse(listDomain, mutableListOf())

        coEvery {
            simpleListRemoteDataSource.getAudioList()
        } returns Result.Success(cameraResponse)

        FileList.audioList = emptyList()

        val result = simpleListRepositoryImpl.getAudioList() as Result.Success
        Assert.assertEquals(result.data.items, domainInformationFileResponse.items)
        Assert.assertEquals(result.data.errors, domainInformationFileResponse.errors)
    }

    @Test
    fun getAudioListSuccessWithLessValuesInDataSource() = runBlockingTest {
        val cameraFile: CameraFile = mockk(relaxed = true)
        val cameraResponse = FileResponseWithErrors()
        cameraResponse.items.addAll(arrayListOf(cameraFile, cameraFile))

        coEvery { simpleListRemoteDataSource.getAudioList() } returns Result.Success(
            cameraResponse
        )

        FileList.audioList = listOf(
            DomainInformationFile(cameraFile.toDomain(), null),
            DomainInformationFile(cameraFile.toDomain(), null)
        )
        val imageList = FileList.audioList as MutableList
        val domainInformationFileResponse =
            DomainInformationFileResponse(imageList, mutableListOf())

        val result = simpleListRepositoryImpl.getAudioList() as Result.Success
        Assert.assertEquals(result.data.items, domainInformationFileResponse.items)
        Assert.assertEquals(result.data.errors, domainInformationFileResponse.errors)
    }

    @Test
    fun getAudioListSuccessWithEqualsValuesInDataSource() = runBlockingTest {
        mockkObject(FileList)
        every { FileList.audioList = any() } just Runs

        val cameraConnectFile: CameraFile = mockk(relaxed = true)
        val cameraResponse = FileResponseWithErrors()
        cameraResponse.items.addAll(arrayListOf(cameraConnectFile, cameraConnectFile))
        val listImages = listOf(
            DomainInformationFile(cameraConnectFile.toDomain(), null),
            DomainInformationFile(cameraConnectFile.toDomain(), null)
        )

        coEvery {
            simpleListRemoteDataSource.getAudioList()
        } returns Result.Success(cameraResponse)

        FileList.audioList = listImages

        val result = simpleListRepositoryImpl.getAudioList() as Result.Success
        Assert.assertEquals(result.data.items, listImages)

        coVerify { FileList.audioList = listImages }
    }

    @Test
    fun getAudioListFailed() = runBlockingTest {
        val result = Result.Error(mockk())
        coEvery { simpleListRemoteDataSource.getAudioList() } returns result
        Assert.assertEquals(simpleListRepositoryImpl.getAudioList(), result)
    }
}
