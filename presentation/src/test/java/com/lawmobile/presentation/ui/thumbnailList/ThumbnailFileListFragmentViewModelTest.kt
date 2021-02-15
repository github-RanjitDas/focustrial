package com.lawmobile.presentation.ui.thumbnailList

import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.domain.entities.DomainInformationImage
import com.lawmobile.domain.usecase.thumbnailList.ThumbnailListUseCase
import com.lawmobile.presentation.InstantExecutorExtension
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
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
        val domainCameraFile =
            DomainCameraFile("1010202000", "10-10-2020 12:00:00", "", "1010202000")
        val result = Result.Success(mockk<DomainInformationImage>())
        coEvery { thumbnailListUseCase.getImageBytes(any()) } returns result

        runBlocking {
            thumbnailListFragmentViewModel.getImageBytes(domainCameraFile)
            Assert.assertEquals(
                result,
                thumbnailListFragmentViewModel.thumbnailListLiveData.value?.getContent()
            )
        }

        coVerify { thumbnailListUseCase.getImageBytes(any()) }
    }

    @Test
    fun testGetImageBytesListSuccess() {
        val domainCameraFile =
            DomainCameraFile("1010202000", "10-10-2020 12:00:00", "", "1010202000")

        coEvery { thumbnailListUseCase.getImageBytes(any()) } returns
            Result.Success(mockk())

        runBlocking {
            thumbnailListFragmentViewModel.getImageBytes(domainCameraFile)
            Assert.assertTrue(thumbnailListFragmentViewModel.thumbnailListLiveData.value?.getContent() is Result.Success)
        }
    }

    @Test
    fun testGetImageBytesListError() {
        val domainCameraFile =
            DomainCameraFile("1010202000", "10-10-2020 12:00:00", "", "1010202000")

        coEvery { thumbnailListUseCase.getImageBytes(any()) } returns Result.Error(mockk())

        runBlocking {
            thumbnailListFragmentViewModel.getImageBytes(domainCameraFile)
            delay(2000)
            Assert.assertTrue(thumbnailListFragmentViewModel.thumbnailListLiveData.value?.getContent() is Result.Error)
        }
    }

    @Test
    fun testGetImageListFlow() {
        coEvery { thumbnailListUseCase.getSnapshotList() } returns Result.Success(mockk())
        runBlocking { thumbnailListFragmentViewModel.getSnapshotList() }
        coVerify { thumbnailListUseCase.getSnapshotList() }
    }

    @Test
    fun testGetImageListSuccess() {
        coEvery { thumbnailListUseCase.getSnapshotList() } returns Result.Success(mockk(relaxed = true))
        runBlocking {
            thumbnailListFragmentViewModel.getSnapshotList()
            thumbnailListFragmentViewModel.imageListLiveData.value as Result.Success
        }
    }

    @Test
    fun testGetImageListError() {
        coEvery { thumbnailListUseCase.getSnapshotList() } returns Result.Error(Exception(""))
        runBlocking {
            thumbnailListFragmentViewModel.getSnapshotList()
            val result = thumbnailListFragmentViewModel.imageListLiveData.value
            Assert.assertTrue(result is Result.Error)
        }
    }
}
