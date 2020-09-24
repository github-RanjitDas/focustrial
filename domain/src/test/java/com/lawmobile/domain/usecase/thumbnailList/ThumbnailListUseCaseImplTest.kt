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
    fun testGetImagesBytes() {
        val cameraConnectFile =
            CameraConnectFile("1010202000", "10-10-2020 12:00:00", "", "1010202000")
        val imageBytes = mockk<DomainInformationImage>()
        coEvery { thumbnailListRepository.getImageBytes(cameraConnectFile) } returns Result.Success(
            imageBytes
        )

        runBlocking {
            when (val result = linkSnapshotsUseCase.getImageBytes(cameraConnectFile)) {
                is Result.Success -> Assert.assertEquals(imageBytes, result.data)
            }
        }

        coVerify { thumbnailListRepository.getImageBytes(cameraConnectFile) }
    }

    @Test
    fun testGetImagesByteListSuccess() {
        val cameraConnectFile =
            CameraConnectFile("1010202000", "10-10-2020 12:00:00", "", "1010202000")
        coEvery { thumbnailListRepository.getImageBytes(cameraConnectFile) } returns Result.Success(
            mockk()
        )

        runBlocking {
            val result = linkSnapshotsUseCase.getImageBytes(cameraConnectFile)
            Assert.assertTrue(result is Result.Success)
        }
    }

    @Test
    fun testGetImagesByteListError() {
        val cameraConnectFile =
            CameraConnectFile("1010202000", "10-10-2020 12:00:00", "", "1010202000")
        coEvery { thumbnailListRepository.getImageBytes(cameraConnectFile) } returns Result.Error(
            mockk()
        )

        runBlocking {
            val result = linkSnapshotsUseCase.getImageBytes(cameraConnectFile)
            Assert.assertTrue(result is Result.Error)
        }
    }

    @Test
    fun testGetImagesListFlow() {
        coEvery { thumbnailListRepository.getSnapshotList() } returns Result.Success(mockk())
        runBlocking { linkSnapshotsUseCase.getSnapshotList() }
        coVerify { thumbnailListRepository.getSnapshotList() }
    }


}