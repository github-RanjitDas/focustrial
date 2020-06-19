package com.lawmobile.data.repository.linkSnapshotsToVideo

import com.lawmobile.data.datasource.remote.linkSnapshotsToVideo.LinkSnapshotsRemoteDataSource
import com.lawmobile.data.entities.FileList
import com.safefleet.mobile.commons.helpers.Result
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class LinkSnapshotsRepositoryImplTest {

    private val linkSnapshotsRemoteDataSource: LinkSnapshotsRemoteDataSource = mockk()

    private val linkSnapshotsRepositoryImpl: LinkSnapshotsRepositoryImpl by lazy {
        LinkSnapshotsRepositoryImpl(linkSnapshotsRemoteDataSource)
    }

    @BeforeEach
    fun setup() {
        clearAllMocks()
    }

    @Test
    fun testGetImageByteListFlow() {
        FileList.listOfImages = listOf(mockk(relaxed = true), mockk(relaxed = true))
        coEvery { linkSnapshotsRemoteDataSource.getImageBytes(any()) } returns Result.Success("Hola".toByteArray())

        runBlocking {
            linkSnapshotsRepositoryImpl.getImageByteList(1)
        }

        coVerify {
            linkSnapshotsRemoteDataSource.getImageBytes(any())
        }
    }

    @Test
    fun testGetImageByteListSnapshotListError() {
        FileList.listOfImages = emptyList()
        coEvery { linkSnapshotsRemoteDataSource.getSnapshotList() } returns Result.Error(mockk())

        runBlocking {
            val result = linkSnapshotsRepositoryImpl.getImageByteList(1)
            Assert.assertTrue(result is Result.Error)
        }
    }

    @Test
    fun testGetImageByteListError() {
        FileList.listOfImages = emptyList()
        coEvery { linkSnapshotsRemoteDataSource.getSnapshotList() } returns Result.Success(
            listOf(
                mockk(relaxed = true), mockk(relaxed = true)
            )
        )
        coEvery { linkSnapshotsRemoteDataSource.getImageBytes(any()) } returns Result.Error(mockk())

        runBlocking {
            val result = linkSnapshotsRepositoryImpl.getImageByteList(1)
            Assert.assertTrue(result is Result.Error)
        }
    }

    @Test
    fun testGetImageByteListSuccess() {
        FileList.listOfImages = emptyList()
        coEvery { linkSnapshotsRemoteDataSource.getSnapshotList() } returns Result.Success(
            listOf(
                mockk(relaxed = true), mockk(relaxed = true)
            )
        )
        coEvery { linkSnapshotsRemoteDataSource.getImageBytes(any()) } returns Result.Success("Hola".toByteArray())

        runBlocking {
            val result = linkSnapshotsRepositoryImpl.getImageByteList(1)
            Assert.assertTrue(result is Result.Success)
        }

    }

    @Test
    fun testGetImageListSizeValue() {
        FileList.listOfImages = listOf(mockk(), mockk())

        val result = linkSnapshotsRepositoryImpl.getImageListSize()
        Assert.assertEquals(2, result)
    }
}