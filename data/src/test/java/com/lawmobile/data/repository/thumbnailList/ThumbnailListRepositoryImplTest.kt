package com.lawmobile.data.repository.thumbnailList

import com.lawmobile.body_cameras.entities.CameraFile
import com.lawmobile.body_cameras.entities.FileResponseWithErrors
import com.lawmobile.data.datasource.remote.thumbnailList.ThumbnailListRemoteDataSource
import com.lawmobile.data.mappers.impl.FileMapper.toDomain
import com.lawmobile.domain.entities.DomainInformationFile
import com.lawmobile.domain.entities.FileList
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
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
internal class ThumbnailListRepositoryImplTest {

    private val thumbnailListRemoteDataSource: ThumbnailListRemoteDataSource = mockk()
    private val dispatcher = TestCoroutineDispatcher()

    private val linkSnapshotsRepositoryImpl: ThumbnailListRepositoryImpl by lazy {
        ThumbnailListRepositoryImpl(thumbnailListRemoteDataSource)
    }

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(dispatcher)
        clearAllMocks()
    }

    @Test
    fun testGetImageByteListFlow() = runBlockingTest {
        val cameraFile =
            CameraFile("1010202000", "10-10-2020 12:00:00", "", "1010202000")
        FileList.imageList = listOf(mockk(relaxed = true), mockk(relaxed = true))

        coEvery { thumbnailListRemoteDataSource.getImageBytes(any()) } returns Result.Success("Hola".toByteArray())
        linkSnapshotsRepositoryImpl.getImageBytes(cameraFile.toDomain())
        coVerify { thumbnailListRemoteDataSource.getImageBytes(any()) }
    }

    @Test
    fun testGetImageByteListError() = runBlockingTest {
        val cameraFile =
            CameraFile("1010202000", "10-10-2020 12:00:00", "", "1010202000")
        val domainInformationFile =
            DomainInformationFile(cameraFile.toDomain())
        FileList.imageList = listOf(domainInformationFile, domainInformationFile)

        coEvery { thumbnailListRemoteDataSource.getImageBytes(any()) } returns
            Result.Error(mockk())

        val result =
            linkSnapshotsRepositoryImpl.getImageBytes(cameraFile.toDomain())
        Assert.assertTrue(result is Result.Error)
    }

    @Test
    fun testGetImageByteListSuccess() = runBlockingTest {
        FileList.imageList = emptyList()
        val cameraFile: CameraFile = mockk(relaxed = true) {
            every { nameFolder } returns "200710009"
            every { name } returns "12345678"
        }
        val cameraResponse: FileResponseWithErrors = mockk {
            every { items } returns arrayListOf(cameraFile, cameraFile)
        }

        coEvery { thumbnailListRemoteDataSource.getSnapshotList() } returns
            Result.Success(cameraResponse)
        coEvery { thumbnailListRemoteDataSource.getImageBytes(any()) } returns
            Result.Success("Hola".toByteArray())

        val result =
            linkSnapshotsRepositoryImpl.getImageBytes(cameraFile.toDomain())
        Assert.assertTrue(result is Result.Success)
    }

    @Test
    fun testGetImageListFlow() = runBlockingTest {
        FileList.imageList = listOf(mockk(), mockk())
        val cameraConnectFileResponseWithErrors = FileResponseWithErrors().apply {
            items.addAll(listOf(mockk(relaxed = true), mockk(relaxed = true)))
        }

        coEvery { thumbnailListRemoteDataSource.getSnapshotList() } returns
            Result.Success(cameraConnectFileResponseWithErrors)
        linkSnapshotsRepositoryImpl.getSnapshotList()
        coVerify { thumbnailListRemoteDataSource.getSnapshotList() }
    }

    @Test
    fun testGetImageListSuccessMoreItems() = runBlockingTest {
        val cameraFile =
            CameraFile("1010202000", "10-10-2020 12:00:00", "", "1010202000")
        val domainInformationFile =
            DomainInformationFile(cameraFile.toDomain())
        FileList.imageList = listOf(domainInformationFile, domainInformationFile)
        val cameraConnectFileResponseWithErrors = FileResponseWithErrors().apply {
            items.addAll(listOf(cameraFile, cameraFile, cameraFile))
        }

        coEvery { thumbnailListRemoteDataSource.getSnapshotList() } returns
            Result.Success(cameraConnectFileResponseWithErrors)
        val result = linkSnapshotsRepositoryImpl.getSnapshotList()
        Assert.assertEquals((result as Result.Success).data.items.size, 3)
    }

    @Test
    fun testGetImageListSuccessWithLessItemsFromCamera() = runBlockingTest {
        val cameraFile =
            CameraFile("1010202000", "10-10-2020 12:00:00", "", "1010202000")
        val domainInformationFile =
            DomainInformationFile(cameraFile.toDomain())

        FileList.imageList = listOf(
            domainInformationFile,
            domainInformationFile,
            domainInformationFile,
            domainInformationFile
        )

        val cameraConnectFileResponseWithErrors = FileResponseWithErrors().apply {
            items.addAll(listOf(cameraFile, cameraFile))
        }

        coEvery { thumbnailListRemoteDataSource.getSnapshotList() } returns
            Result.Success(cameraConnectFileResponseWithErrors)
        val result = linkSnapshotsRepositoryImpl.getSnapshotList()
        Assert.assertEquals((result as Result.Success).data.items.size, 4)
    }

    @Test
    fun testGetImageListError() = runBlockingTest {
        FileList.imageList = emptyList()
        coEvery { thumbnailListRemoteDataSource.getSnapshotList() } returns Result.Error(Exception("Exception"))
        val result = linkSnapshotsRepositoryImpl.getSnapshotList()
        Assert.assertTrue(result is Result.Error)
    }
}
