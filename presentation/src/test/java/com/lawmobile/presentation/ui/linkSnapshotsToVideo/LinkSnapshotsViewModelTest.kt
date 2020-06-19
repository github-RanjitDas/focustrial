package com.lawmobile.presentation.ui.linkSnapshotsToVideo

import com.lawmobile.domain.entities.DomainInformationImage
import com.lawmobile.domain.usecase.linkSnapshotsToVideo.LinkSnapshotsUseCase
import com.lawmobile.presentation.InstantExecutorExtension
import com.safefleet.mobile.commons.helpers.Result
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(InstantExecutorExtension::class)
internal class LinkSnapshotsViewModelTest {

    private val linkSnapshotsUseCase: LinkSnapshotsUseCase = mockk()
    private val linkSnapshotsViewModel: LinkSnapshotsViewModel by lazy {
        LinkSnapshotsViewModel(linkSnapshotsUseCase)
    }

    @ExperimentalCoroutinesApi
    @BeforeEach
    fun setUp() {
        clearAllMocks()
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @Test
    fun testGetImageBytesListFlow() {
        val result = Result.Success(listOf<DomainInformationImage>(mockk()))
        coEvery { linkSnapshotsUseCase.getImagesByteList(1) } returns result

        runBlocking {
            linkSnapshotsViewModel.getImageBytesList(1)
            Assert.assertEquals(result, linkSnapshotsViewModel.imageListLiveData.value)
        }

        coVerify { linkSnapshotsUseCase.getImagesByteList(1) }
    }

    @Test
    fun testGetImageBytesListSuccess() {
        coEvery { linkSnapshotsUseCase.getImagesByteList(1) } returns Result.Success(listOf(mockk()))

        runBlocking {
            linkSnapshotsViewModel.getImageBytesList(1)
            Assert.assertTrue(linkSnapshotsViewModel.imageListLiveData.value is Result.Success)
        }
    }

    @Test
    fun testGetImageBytesListError() {
        coEvery { linkSnapshotsUseCase.getImagesByteList(1) } returns Result.Error(mockk())

        runBlocking {
            linkSnapshotsViewModel.getImageBytesList(1)
            Assert.assertTrue(linkSnapshotsViewModel.imageListLiveData.value is Result.Error)
        }
    }

    @Test
    fun testGetImageListSizeValue() {
        every { linkSnapshotsUseCase.getImageListSize() } returns 1
        val result = linkSnapshotsViewModel.getImageListSize()
        Assert.assertEquals(1, result)
    }

    @Test
    fun testGetImagesListSizeFlow() {
        every { linkSnapshotsUseCase.getImageListSize() } returns 1
        linkSnapshotsViewModel.getImageListSize()
        verify { linkSnapshotsViewModel.getImageListSize() }
    }
}