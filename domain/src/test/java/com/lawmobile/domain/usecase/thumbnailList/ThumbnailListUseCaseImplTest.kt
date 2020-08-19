package com.lawmobile.domain.usecase.thumbnailList

import com.lawmobile.domain.entities.DomainInformationImage
import com.lawmobile.domain.repository.thumbnailList.ThumbnailListRepository
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
        val imageList = listOf<DomainInformationImage>(mockk())
        coEvery { thumbnailListRepository.getImageByteList(1) } returns Result.Success(imageList)

        runBlocking {
            when (val result = linkSnapshotsUseCase.getImagesByteList(1)) {
                is Result.Success -> Assert.assertEquals(imageList, result.data)
            }
        }

        coVerify { thumbnailListRepository.getImageByteList(1) }
    }

    @Test
    fun testGetImagesByteListSuccess() {
        coEvery { thumbnailListRepository.getImageByteList(1) } returns Result.Success(listOf(mockk()))

        runBlocking {
            val result = linkSnapshotsUseCase.getImagesByteList(1)
            Assert.assertTrue(result is Result.Success)
        }
    }

    @Test
    fun testGetImagesByteListError() {
        coEvery { thumbnailListRepository.getImageByteList(1) } returns Result.Error(mockk())

        runBlocking {
            val result = linkSnapshotsUseCase.getImagesByteList(1)
            Assert.assertTrue(result is Result.Error)
        }
    }

    @Test
    fun testGetImageListSizeValue() {
        every { thumbnailListRepository.getImageListSize() } returns 1
        val result = linkSnapshotsUseCase.getImageListSize()
        Assert.assertEquals(1, result)
    }

    @Test
    fun testGetImagesListSizeFlow() {
        every { thumbnailListRepository.getImageListSize() } returns 1
        linkSnapshotsUseCase.getImageListSize()
        verify { thumbnailListRepository.getImageListSize() }
    }


}