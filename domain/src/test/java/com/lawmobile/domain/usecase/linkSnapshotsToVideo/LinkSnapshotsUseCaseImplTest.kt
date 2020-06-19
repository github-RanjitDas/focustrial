package com.lawmobile.domain.usecase.linkSnapshotsToVideo

import com.lawmobile.domain.entities.DomainInformationImage
import com.lawmobile.domain.repository.linkSnapshotsToVideo.LinkSnapshotsRepository
import com.safefleet.mobile.commons.helpers.Result
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.BeforeEach

internal class LinkSnapshotsUseCaseImplTest {

    private val linkSnapshotsRepository: LinkSnapshotsRepository = mockk()
    private val linkSnapshotsUseCase: LinkSnapshotsUseCaseImpl by lazy {
        LinkSnapshotsUseCaseImpl(linkSnapshotsRepository)
    }

    @BeforeEach
    fun setup() {
        clearAllMocks()
    }

    @Test
    fun testGetImagesByteListFlow() {
        val imageList = listOf<DomainInformationImage>(mockk())
        coEvery { linkSnapshotsRepository.getImageByteList(1) } returns Result.Success(imageList)

        runBlocking {
            when (val result = linkSnapshotsUseCase.getImagesByteList(1)) {
                is Result.Success -> Assert.assertEquals(imageList, result.data)
            }
        }

        coVerify { linkSnapshotsRepository.getImageByteList(1) }
    }

    @Test
    fun testGetImagesByteListSuccess() {
        coEvery { linkSnapshotsRepository.getImageByteList(1) } returns Result.Success(listOf(mockk()))

        runBlocking {
            val result = linkSnapshotsUseCase.getImagesByteList(1)
            Assert.assertTrue(result is Result.Success)
        }
    }

    @Test
    fun testGetImagesByteListError() {
        coEvery { linkSnapshotsRepository.getImageByteList(1) } returns Result.Error(mockk())

        runBlocking {
            val result = linkSnapshotsUseCase.getImagesByteList(1)
            Assert.assertTrue(result is Result.Error)
        }
    }

    @Test
    fun testGetImageListSizeValue() {
        every { linkSnapshotsRepository.getImageListSize() } returns 1
        val result = linkSnapshotsUseCase.getImageListSize()
        Assert.assertEquals(1, result)
    }

    @Test
    fun testGetImagesListSizeFlow() {
        every { linkSnapshotsRepository.getImageListSize() } returns 1
        linkSnapshotsUseCase.getImageListSize()
        verify { linkSnapshotsRepository.getImageListSize() }
    }


}