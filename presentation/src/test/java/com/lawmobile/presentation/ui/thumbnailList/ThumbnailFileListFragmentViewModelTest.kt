package com.lawmobile.presentation.ui.thumbnailList

import com.lawmobile.domain.entities.DomainInformationImage
import com.lawmobile.domain.usecase.thumbnailList.ThumbnailListUseCase
import com.lawmobile.presentation.InstantExecutorExtension
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.commons.helpers.Result
import io.mockk.*
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
import java.lang.Exception

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
        val cameraConnectFile =
            CameraConnectFile("1010202000", "10-10-2020 12:00:00", "", "1010202000")
        val result = Result.Success(listOf<DomainInformationImage>(mockk()))
        coEvery { thumbnailListUseCase.getImagesByteList(cameraConnectFile) } returns result

        runBlocking {
            thumbnailListFragmentViewModel.getImageBytes(cameraConnectFile)
            Assert.assertEquals(result, thumbnailListFragmentViewModel.thumbnailListLiveData.value)
        }

        coVerify { thumbnailListUseCase.getImagesByteList(cameraConnectFile) }
    }

    @Test
    fun testGetImageBytesListSuccess() {
        val cameraConnectFile =
            CameraConnectFile("1010202000", "10-10-2020 12:00:00", "", "1010202000")
        coEvery { thumbnailListUseCase.getImagesByteList(cameraConnectFile) } returns Result.Success(listOf(mockk()))

        runBlocking {
            thumbnailListFragmentViewModel.getImageBytes(cameraConnectFile)
            Assert.assertTrue(thumbnailListFragmentViewModel.thumbnailListLiveData.value is Result.Success)
        }
    }

    @Test
    fun testGetImageBytesListError() {
        val cameraConnectFile =
            CameraConnectFile("1010202000", "10-10-2020 12:00:00", "", "1010202000")
        coEvery { thumbnailListUseCase.getImagesByteList(cameraConnectFile) } returns Result.Error(mockk())

        runBlocking {
            thumbnailListFragmentViewModel.getImageBytes(cameraConnectFile)
            delay(2000)
            Assert.assertTrue(thumbnailListFragmentViewModel.thumbnailListLiveData.value is Result.Error)
        }
    }

    @Test
    fun testGetImageListFlow() {
        coEvery { thumbnailListUseCase.getImageList() } returns Result.Success(emptyList())
        runBlocking { thumbnailListFragmentViewModel.getImageList() }
        coVerify { thumbnailListUseCase.getImageList() }
    }

    @Test
    fun testGetImageListSuccess() {
        coEvery { thumbnailListUseCase.getImageList() } returns Result.Success(emptyList())
        runBlocking {
            thumbnailListFragmentViewModel.getImageList()
            val result = thumbnailListFragmentViewModel.imageListLiveData.value as Result.Success
            Assert.assertTrue(result.data.isEmpty())
        }
    }

    @Test
    fun testGetImageListError() {
        coEvery { thumbnailListUseCase.getImageList() } returns Result.Error(Exception(""))
        runBlocking {
            thumbnailListFragmentViewModel.getImageList()
            val result = thumbnailListFragmentViewModel.imageListLiveData.value
            Assert.assertTrue(result is Result.Error)
        }
    }
}