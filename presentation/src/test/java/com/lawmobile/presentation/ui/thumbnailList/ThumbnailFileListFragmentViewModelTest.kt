package com.lawmobile.presentation.ui.thumbnailList

import com.lawmobile.domain.entities.DomainInformationImage
import com.lawmobile.domain.usecase.thumbnailList.ThumbnailListUseCase
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
internal class ThumbnailFileListFragmentViewModelTest {

    private val thumbnailListUseCase: ThumbnailListUseCase = mockk()
    private val thumbnailListFragmentViewModel: ThumbnailListFragmentViewModel by lazy {
        ThumbnailListFragmentViewModel(thumbnailListUseCase)
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
        coEvery { thumbnailListUseCase.getImagesByteList(1) } returns result

        runBlocking {
            thumbnailListFragmentViewModel.getImageBytesList(1)
            Assert.assertEquals(result, thumbnailListFragmentViewModel.thumbnailListLiveData.value)
        }

        coVerify { thumbnailListUseCase.getImagesByteList(1) }
    }

    @Test
    fun testGetImageBytesListSuccess() {
        coEvery { thumbnailListUseCase.getImagesByteList(1) } returns Result.Success(listOf(mockk()))

        runBlocking {
            thumbnailListFragmentViewModel.getImageBytesList(1)
            Assert.assertTrue(thumbnailListFragmentViewModel.thumbnailListLiveData.value is Result.Success)
        }
    }

    @Test
    fun testGetImageBytesListError() {
        coEvery { thumbnailListUseCase.getImagesByteList(1) } returns Result.Error(mockk())

        runBlocking {
            thumbnailListFragmentViewModel.getImageBytesList(1)
            Assert.assertTrue(thumbnailListFragmentViewModel.thumbnailListLiveData.value is Result.Error)
        }
    }

    @Test
    fun testGetImageListSizeValue() {
        every { thumbnailListUseCase.getImageListSize() } returns 1
        val result = thumbnailListFragmentViewModel.getImageListSize()
        Assert.assertEquals(1, result)
    }

    @Test
    fun testGetImagesListSizeFlow() {
        every { thumbnailListUseCase.getImageListSize() } returns 1
        thumbnailListFragmentViewModel.getImageListSize()
        verify { thumbnailListFragmentViewModel.getImageListSize() }
    }
}