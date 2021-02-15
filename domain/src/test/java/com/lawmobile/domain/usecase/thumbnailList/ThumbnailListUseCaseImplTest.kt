package com.lawmobile.domain.usecase.thumbnailList

import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.domain.entities.DomainInformationImage
import com.lawmobile.domain.repository.thumbnailList.ThumbnailListRepository
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

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
        val domainCameraFile =
            DomainCameraFile("1010202000", "10-10-2020 12:00:00", "", "1010202000")
        val imageBytes = mockk<DomainInformationImage>()
        coEvery { thumbnailListRepository.getImageBytes(domainCameraFile) } returns Result.Success(
            imageBytes
        )

        runBlocking {
            when (val result = linkSnapshotsUseCase.getImageBytes(domainCameraFile)) {
                is Result.Success -> Assert.assertEquals(imageBytes, result.data)
            }
        }

        coVerify { thumbnailListRepository.getImageBytes(domainCameraFile) }
    }

    @Test
    fun testGetImagesByteListSuccess() {
        val domainCameraFile =
            DomainCameraFile("1010202000", "10-10-2020 12:00:00", "", "1010202000")
        coEvery { thumbnailListRepository.getImageBytes(domainCameraFile) } returns Result.Success(
            mockk()
        )

        runBlocking {
            val result = linkSnapshotsUseCase.getImageBytes(domainCameraFile)
            Assert.assertTrue(result is Result.Success)
        }
    }

    @Test
    fun testGetImagesByteListError() {
        val domainCameraFile =
            DomainCameraFile("1010202000", "10-10-2020 12:00:00", "", "1010202000")
        coEvery { thumbnailListRepository.getImageBytes(domainCameraFile) } returns Result.Error(
            mockk()
        )

        runBlocking {
            val result = linkSnapshotsUseCase.getImageBytes(domainCameraFile)
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
