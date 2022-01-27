package com.lawmobile.presentation.ui.fileList.thumbnailList

import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.domain.entities.DomainInformationImage
import com.lawmobile.domain.usecase.thumbnailList.ThumbnailListUseCase
import com.lawmobile.presentation.InstantExecutorExtension
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ExtendWith(InstantExecutorExtension::class)
internal class ThumbnailListViewModelTest {

    private val useCase: ThumbnailListUseCase = mockk()
    private var job: Job? = mockk(relaxed = true)
    private val viewModel: ThumbnailListViewModel by lazy {
        ThumbnailListViewModel(useCase, job)
    }

    private val dispatcher = TestCoroutineDispatcher()

    @ExperimentalCoroutinesApi
    @BeforeEach
    fun setUp() {
        clearAllMocks()
        Dispatchers.setMain(dispatcher)
    }

    @Test
    fun testGetImageBytesListFlow() = runBlockingTest {
        val domainCameraFile =
            DomainCameraFile("1010202000", "10-10-2020 12:00:00", "", "1010202000")
        val result = Result.Success(mockk<DomainInformationImage>())
        coEvery { useCase.getImageBytes(any()) } returns result

        viewModel.getImageBytes(domainCameraFile)
        Assert.assertEquals(
            result,
            viewModel.thumbnailListLiveData.value?.getContent()
        )

        coVerify { useCase.getImageBytes(any()) }
    }

    @Test
    fun testGetImageBytesListSuccess() = runBlockingTest {
        val domainCameraFile =
            DomainCameraFile("1010202000", "10-10-2020 12:00:00", "", "1010202000")

        coEvery { useCase.getImageBytes(any()) } returns Result.Success(mockk())

        viewModel.getImageBytes(domainCameraFile)
        Assert.assertTrue(viewModel.thumbnailListLiveData.value?.getContent() is Result.Success)
    }

    @Test
    fun testGetImageBytesListError() = runBlockingTest {
        val domainCameraFile =
            DomainCameraFile("1010202000", "10-10-2020 12:00:00", "", "1010202000")

        coEvery { useCase.getImageBytes(any()) } returns Result.Error(mockk())

        viewModel.getImageBytes(domainCameraFile)
        dispatcher.advanceTimeBy(2000)
        Assert.assertTrue(viewModel.thumbnailListLiveData.value?.getContent() is Result.Error)
    }

    @Test
    fun testGetImageListFlow() = runBlockingTest {
        coEvery { useCase.getSnapshotList() } returns Result.Success(mockk())
        viewModel.getSnapshotList()
        coVerify { useCase.getSnapshotList() }
    }

    @Test
    fun testGetImageListSuccess() = runBlockingTest {
        coEvery { useCase.getSnapshotList() } returns Result.Success(mockk(relaxed = true))
        viewModel.getSnapshotList()
        viewModel.imageListLiveData.value as Result.Success
    }

    @Test
    fun testGetImageListError() = runBlockingTest {
        coEvery { useCase.getSnapshotList() } returns Result.Error(Exception(""))
        viewModel.getSnapshotList()
        val result = viewModel.imageListLiveData.value
        Assert.assertTrue(result is Result.Error)
    }

    @Test
    fun cancelGetImageBytes() {
        every { job?.cancel() } returns Unit
        viewModel.cancelGetImageBytes()
        verify { job?.cancel() }
    }
}
