package com.lawmobile.domain.usecase.thumbnailList

import com.lawmobile.domain.entities.DomainInformationImage
import com.lawmobile.domain.repository.thumbnailList.ThumbnailListRepository
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.commons.helpers.Result
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.BeforeEach

internal class ThumbnailListUseCaseImplTest {

    private val thumbnailListRepository: ThumbnailListRepository = mockk()
    private val linkSnapshotsUseCase: ThumbnailListUseCaseImpl by lazy {
        ThumbnailListUseCaseImpl(thumbnailListRepository)
    }

    @BeforeEach
    fun setup() {
        clearAllMocks()
    }

    @Test
    fun testGetImagesByteListFlow() {
        val cameraConnectFile =
            CameraConnectFile("1010202000", "10-10-2020 12:00:00", "", "1010202000")
        val imageList = listOf<DomainInformationImage>(mockk())
        coEvery { thumbnailListRepository.getImageByteList(cameraConnectFile) } returns Result.Success(
            imageList
        )

        runBlocking {
            when (val result = linkSnapshotsUseCase.getImagesByteList(cameraConnectFile)) {
                is Result.Success -> Assert.assertEquals(imageList, result.data)
            }
        }

        coVerify { thumbnailListRepository.getImageByteList(cameraConnectFile) }
    }

    @Test
    fun testGetImagesByteListSuccess() {
        val cameraConnectFile =
            CameraConnectFile("1010202000", "10-10-2020 12:00:00", "", "1010202000")
        coEvery { thumbnailListRepository.getImageByteList(cameraConnectFile) } returns Result.Success(
            listOf(mockk())
        )

        runBlocking {
            val result = linkSnapshotsUseCase.getImagesByteList(cameraConnectFile)
            Assert.assertTrue(result is Result.Success)
        }
    }

    @Test
    fun testGetImagesByteListError() {
        val cameraConnectFile =
            CameraConnectFile("1010202000", "10-10-2020 12:00:00", "", "1010202000")
        coEvery { thumbnailListRepository.getImageByteList(cameraConnectFile) } returns Result.Error(
            mockk()
        )

        runBlocking {
            val result = linkSnapshotsUseCase.getImagesByteList(cameraConnectFile)
            Assert.assertTrue(result is Result.Error)
        }
    }

    @Test
    fun testGetImagesListFlow() {
        coEvery { thumbnailListRepository.getImageList() } returns Result.Success(emptyList())
        runBlocking { linkSnapshotsUseCase.getImageList() }
        coVerify { thumbnailListRepository.getImageList() }
    }


}